package service;

import config.DataSourceProvider;
import dao.CommandeDAO;
import dao.ProduitDAO;
import dao.StockDAO;
import model.CommandeItem;
import model.ContenantItem;

import java.sql.*;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CommandeService {

    private final ProduitDAO produitDAO = new ProduitDAO();
    private final StockDAO stockDAO = new StockDAO();
    private final CommandeDAO commandeDAO = new CommandeDAO();

    public CommandeService(){ }

    public String generateId(String prefix) {
        int n = (int)(Math.random() * 1_000_0000);
        return prefix + String.format("%07d", n);
    }

    public String passerCommande(String idClient,
                                 String modeRecuperation, // "Boutique" | "Domicile"
                                 String modePaiement,     // "En ligne" | "En Boutique"
                                 List<CommandeItem> items,
                                 List<ContenantItem> contenants, // liste des contenants commandés
                                 String idAdresseDomicile, // nullable si Boutique
                                 Float distanceLivraison,  // distance en km (requis si Domicile)
                                 String typePaysLivraison  // "France Métropolitaine", "DOM-TOM", "International" (requis si Domicile)
    ) throws SQLException {
        // Panier vide uniquement si aucun produit et aucun contenant
        boolean aucunProduit = (items == null || items.isEmpty());
        boolean aucunContenant = (contenants == null || contenants.isEmpty());
        if (aucunProduit && aucunContenant) {
            throw new IllegalArgumentException("La commande est vide");
        }
        
        
        // Validation: livraison domicile requiert paiement en ligne
        if ("Domicile".equalsIgnoreCase(modeRecuperation) && !"En ligne".equalsIgnoreCase(modePaiement)) {
            throw new IllegalArgumentException("Le paiement en ligne est obligatoire pour une livraison à domicile");
        }
        if ("Domicile".equalsIgnoreCase(modeRecuperation) && (idAdresseDomicile == null || distanceLivraison == null || typePaysLivraison == null)) {
            throw new IllegalArgumentException("Les informations de livraison (adresse, distance, type pays) sont obligatoires pour une livraison à domicile");
        }

        Connection conn = DataSourceProvider.getValidConnection();
        int oldIsolation = Connection.TRANSACTION_READ_COMMITTED;
        
        try {
            oldIsolation = conn.getTransactionIsolation();
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            afficherColonnes(conn, "LigneCommandeProduitVrac");
            afficherTable(conn, "LigneCommandeProduitVrac");

            String idCommande = generateId("CM");
            //LocalDate dateEstimeeLivraison = LocalDate.now(); 
            

            // 1) Créer la commande (statut En préparation)
                String sqlCommande = "INSERT INTO Commande (idCommande, dateCommande, heureCommande, statutCommande, modePaiement, modeRecuperation, idClient) " +
                    "VALUES (?, TRUNC(SYSDATE), SYSTIMESTAMP, 'En préparation', ?, ?, ?)";
                System.out.println("[DEBUG] SQL Commande: " + sqlCommande);
                try (PreparedStatement ps = conn.prepareStatement(sqlCommande)) {
                ps.setString(1, idCommande);
                ps.setString(2, modePaiement);
                ps.setString(3, modeRecuperation);
                ps.setString(4, idClient);
                ps.executeUpdate();
                }         

            float poidsTotal = 0f;
            float capaciteTotaleContenants = 0f; // approximation pour domicile si aucun produit

            // 2) Pour chaque item: vérification saison + stock + insert lignes + maj stock
            for (CommandeItem it : items) {
                String idProduit = it.getIdProduit();
                String idProducteur = it.getIdProducteur();
                String typeCond = it.getTypeConditionnement();
                int quantite = it.getQuantite();
                // a) vérifier saisonnalité
                boolean dispo = produitDAO.estDisponible(idProduit, idProducteur, new java.sql.Date(System.currentTimeMillis()));
                if (!dispo) throw new SQLException("Produit hors saison: " + idProduit);

                // b) vérifier stock
                String idStock = stockDAO.getIdStock(idProduit, idProducteur, conn);
                if (idStock == null) throw new SQLException("Stock introuvable pour le produit: " + idProduit);
                StockService stockService = new StockService(idStock);
                ArrayList<String> lots_pris = stockService.stockSuffisantProduit(idProduit, idProducteur, quantite, null, typeCond, conn);
                if (lots_pris.isEmpty()) throw new SQLException("Stock insuffisant pour le produit: " + idProduit);

                // c) prix unitaire
                Float prixU = produitDAO.getPrixVenteClient(idProduit, idProducteur);
                if (prixU == null) throw new SQLException("Prix introuvable pour le produit: " + idProduit);
                float sousTotal = prixU * quantite;

                // calcul poids
                if ("Preconditionne".equalsIgnoreCase(typeCond)) {
                    Float poids = produitDAO.getPoidsSachet(idProduit, idProducteur);
                    if (poids != null) poidsTotal += poids * quantite;
                    } else if ("Vrac".equalsIgnoreCase(typeCond)) {
                        // quantite is stockée comme int (grammes) dans l'UI: convertir en kg
                        poidsTotal += quantite / 1000.0f; // kg
                }

                // d) insérer LigneCommande + LigneCommandeProduit + spc vrac/precond
                String idLigne = generateId("LC");
                String sqlLigne = "INSERT INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande) VALUES (?, ?, ?, ?)";
                System.out.println("[DEBUG] SQL LigneCommande: " + sqlLigne + " | idCommande=" + idCommande);
                try (PreparedStatement ps = conn.prepareStatement(sqlLigne)) {
                    ps.setString(1, idLigne);
                    ps.setFloat(2, prixU);
                    ps.setFloat(3, sousTotal);
                    ps.setString(4, idCommande);
                    ps.executeUpdate();
                }

                String sqlLigneProd = "INSERT INTO LigneCommandeProduit (idLigneCommande, idCommande, idProduit, idProducteur) VALUES (?, ?, ?, ?)";
                System.out.println("[DEBUG] SQL LigneCommandeProduit: " + sqlLigneProd + " | idCommande=" + idCommande);
                try (PreparedStatement ps = conn.prepareStatement(sqlLigneProd)) {
                    ps.setString(1, idLigne);
                    ps.setString(2, idCommande);
                    ps.setString(3, idProduit);
                    ps.setString(4, idProducteur);
                    ps.executeUpdate();
                }

                if ("Preconditionne".equalsIgnoreCase(typeCond)) {
                    String sqlPre = "INSERT INTO LigneCommandeProduitPreconditionne (idLigneCommande, idCommande, quantiteCommandePreconditionne) VALUES (?, ?, ?)";
                    System.out.println("[DEBUG] SQL Preconditionne: " + sqlPre);
                    try (PreparedStatement ps = conn.prepareStatement(sqlPre)) {
                        ps.setString(1, idLigne);
                        ps.setString(2, idCommande);
                        ps.setInt(3, quantite);
                        ps.executeUpdate();
                    }
                } else if ("Vrac".equalsIgnoreCase(typeCond)) {
                    String sqlVrac = "INSERT INTO LigneCommandeProduitVrac (idLigneCommande, idCommande, quantiteCommandeVrac) VALUES (?, ?, ?)";
                    System.out.println("[DEBUG] SQL Vrac: " + sqlVrac);
                    try (PreparedStatement ps = conn.prepareStatement(sqlVrac)) {
                        ps.setString(1, idLigne);
                        ps.setString(2, idCommande);
                            ps.setDouble(3, quantite); // convertir grammes -> kg
                        ps.executeUpdate();
                    } catch (SQLException ex) {
                        System.err.println("[FAILED EXECUTE] SQLVrac: " + sqlVrac + " | params: idLigne=" + idLigne + ", idCommande=" + idCommande);
                        ex.printStackTrace();
                        throw ex;
                    }
                }

                // e) NE PAS décrémenter le stock ici, ça se fait au passage à "Prête"
            }

            // 2bis) Insérer les lignes de contenant si présentes
            if (contenants != null && !contenants.isEmpty()) {
                for (ContenantItem contenant : contenants) {
                    String idLigneContenant = generateId("LC");
                    
                    // a) Créer LigneCommande pour le contenant
                    String sqlLC = "INSERT INTO LigneCommande (idLigneCommande, prixUnitaire, sousTotalLigne, idCommande) VALUES (?, ?, ?, ?)";
                    // Récupérer le prix et la capacité du contenant
                    float prixContenant = 0f;
                    float capaciteContenant = 0f;
                    String sqlPrix = "SELECT prixContenant, capaciteContenant FROM Contenant WHERE referenceContenant = ?";
                    try (PreparedStatement psP = conn.prepareStatement(sqlPrix)) {
                        psP.setString(1, contenant.getReferenceContenant());
                        try (ResultSet rsP = psP.executeQuery()) {
                            if (rsP.next()) {
                                prixContenant = rsP.getFloat("prixContenant");
                                capaciteContenant = rsP.getFloat("capaciteContenant");
                            }
                        }
                    }
                    float sousTotal = prixContenant * contenant.getQuantite();
                    // cumuler une estimation de poids (ou volume) pour satisfaire la contrainte (> 0)
                    if (capaciteContenant > 0) {
                        capaciteTotaleContenants += capaciteContenant * contenant.getQuantite();
                    } else {
                        capaciteTotaleContenants += 0.1f * contenant.getQuantite();
                    }
                    
                    try (PreparedStatement psLC = conn.prepareStatement(sqlLC)) {
                        psLC.setString(1, idLigneContenant);
                        psLC.setFloat(2, prixContenant);
                        psLC.setFloat(3, sousTotal);
                        psLC.setString(4, idCommande);
                        psLC.executeUpdate();
                    }
                    
                    // b) Créer LigneCommandeContenant
                    String sqlLCC = "INSERT INTO LigneCommandeContenant (idLigneCommande, idCommande, quantiteCommandeContenant) VALUES (?, ?, ?)";
                    try (PreparedStatement psLCC = conn.prepareStatement(sqlLCC)) {
                        psLCC.setString(1, idLigneContenant);
                        psLCC.setString(2, idCommande);
                        psLCC.setInt(3, contenant.getQuantite());
                        psLCC.executeUpdate();
                    }
                }
            }

            // // 3) Si domicile, créer ModeRecuperationDomicile avec infos saisies
            if ("Domicile".equalsIgnoreCase(modeRecuperation)) {
                // // Garantir poidsTotalCommande > 0 même sans produits (contenants seuls)
                // float poidsTotalCommande = poidsTotal;
                // if (poidsTotalCommande <= 0f) {
                //     // utiliser la capacité totale des contenants comme approximation (>0), sinon un minimum
                //     poidsTotalCommande = capaciteTotaleContenants > 0f ? capaciteTotaleContenants : 0.1f;
                

                 String idMode = generateId("MR");
                // Utiliser les paramètres fournis
                String paysLivraison = "International".equals(typePaysLivraison) ? "Autre" : "France";
            //     // Normaliser typePaysLivraison aux valeurs attendues par la contrainte

            //     String sqlMRD = "INSERT INTO ModeRecuperationDomicile (idModeRecuperationDomicile, paysLivraison, poidsTotalCommande, distanceAdresseBoutique, dateEstimeeLivraison, typePaysLivraison, idCommande, idAdresse) " +
            //             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            //     System.out.println("[DEBUG] SQL ModeRecuperationDomicile: " + sqlMRD + " | idCommande=" + idCommande);
            //     try (PreparedStatement ps = conn.prepareStatement(sqlMRD)) {
            //         ps.setString(1, idMode);
            //         ps.setString(2, paysLivraison);
            //         ps.setFloat(3, poidsTotalCommande);
            //         ps.setDate(4, java.sql.Date.valueOf(dateEstimeeLivraison));
            //         ps.setFloat(5, distanceLivraison);
            //         ps.setString(6, typePaysCanon);//probleme
            //         ps.setString(7, idCommande);
            //         ps.setString(8, idAdresseDomicile);
            //         ps.executeUpdate();
            //     }
            // }
                // String idMode = generateId("MR");
                // // Utiliser les paramètres fournis
                // String paysLivraison = "International".equals(typePaysLivraison) ? "Autre" : "France";
                // // Normaliser typePaysLivraison aux valeurs attendues par la contrainte
                String typePaysCanon;
                String t = typePaysLivraison == null ? "" : Normalizer.normalize(typePaysLivraison, Normalizer.Form.NFD)
                        .replaceAll("\\p{M}+", "")
                        .toLowerCase()
                        .trim();
                if (t.contains("metropol") || t.contains("metrop")) {
                    typePaysCanon = "France Métropolitaine";
                } else if (t.contains("dom")) {
                    typePaysCanon = "DOM-TOM";
                } else if (t.contains("inter")) {
                    typePaysCanon = "International";
                } else if (t.contains("france")) {
                    typePaysCanon = "France Métropolitaine";
                } else {
                    typePaysCanon = "International";
                }
                float poidsTotalCommande = poidsTotal;
                if (poidsTotalCommande <= 0f) {
                    poidsTotalCommande = capaciteTotaleContenants > 0f ? capaciteTotaleContenants : 0.1f;
                }

                LocalDate dateEstimeeLivraison = calculDateEstimeeDeLivraison(distanceLivraison, typePaysCanon, idCommande);
                String sqlMRD = "INSERT INTO ModeRecuperationDomicile (idModeRecuperationDomicile, paysLivraison, poidsTotalCommande, distanceAdresseBoutique, dateEstimeeLivraison, typePaysLivraison, idCommande, idAdresse) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                System.out.println("[DEBUG] SQL ModeRecuperationDomicile: " + sqlMRD + " | idCommande=" + idCommande);
                try (PreparedStatement ps = conn.prepareStatement(sqlMRD)) {
                    ps.setString(1, idMode);
                    ps.setString(2, paysLivraison);
                    ps.setFloat(3, poidsTotalCommande);
                    ps.setDate(4, java.sql.Date.valueOf(dateEstimeeLivraison));
                    ps.setFloat(5, distanceLivraison);
                    ps.setString(6, typePaysCanon);//probleme
                    ps.setString(7, idCommande);
                    ps.setString(8, idAdresseDomicile);
                    ps.executeUpdate();
                }
            }
            

            

            // 4) Si mode récupération Boutique: pas de statut "En préparation" selon vntre règle,
            // on passe immédiatement en "Prête" pour déclencher la sortie de stock à la clôture.
            if ("Boutique".equalsIgnoreCase(modeRecuperation)) {
                String sqlStatut = "UPDATE Commande SET statutCommande = 'Prête' WHERE idCommande = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlStatut)) {
                    ps.setString(1, idCommande);
                    ps.executeUpdate();
                    
                }
            }

            // 5) Si Domicile avec paiement En ligne, enregistrer datePaiement immédiatement
            if ("Domicile".equalsIgnoreCase(modeRecuperation) && "En ligne".equalsIgnoreCase(modePaiement)) {
                commandeDAO.enregistrerDatePaiement(idCommande);
            }

            conn.commit();
            if ("Boutique".equalsIgnoreCase(modeRecuperation)){
                ClotureCommande clotureCommande = new ClotureCommande();
                clotureCommande.cloturerCommande(idCommande);
                conn.commit();
            }
            return idCommande;
        }
    
         catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignore) {}
            throw e;
        } finally {
            try { 
                if ("Boutique".equalsIgnoreCase(modeRecuperation)){}
                conn.setAutoCommit(true); } catch (SQLException ignore) {}
        }
    }



    public LocalDate calculDateEstimeeDeLivraison(float distanceAdresseBoutique, String typePays, String idCommande){
                    
                    // Calcul des jours de base selon la zone
                    int joursBase = 2; // France Métropolitaine par défaut
                    if ("DOM-TOM".equals(typePays)) {
                        joursBase = 5;
                    } else if ("International".equals(typePays)) {
                        joursBase = 7;
                    }
                    
                    // Calcul des jours supplémentaires selon la distance
                    int joursSupplementaires = 0;
                    if (distanceAdresseBoutique > 2000) {
                        joursSupplementaires = 3;
                    } else if (distanceAdresseBoutique > 300) {
                        joursSupplementaires = 2;
                    } else if (distanceAdresseBoutique > 50) {
                        joursSupplementaires = 1;
                    }
                    
                    // Récupération du délai maximal de disponibilité des produits de la commande
                    int delaiMaxDisponibilite = commandeDAO.getDelaiMaxDisponibilite(idCommande);
                    
                    // Calcul de la date estimée avec tous les délais
                    int totalJours = joursBase + joursSupplementaires + delaiMaxDisponibilite;
                    LocalDate dateEstimee = LocalDate.now().plusDays(totalJours);
                    
                    // si besoin, on decommente cette ligne de mis à jour de la date estimée dans la base de données
                    //updateDateEstimeeLivraison(idModeDeRecuperationDomicile, dateEstimee);
                    
                    return dateEstimee;
        
        //return LocalDate.now();
                
}

public static void afficherColonnes(Connection conn, String nomTable) throws SQLException {
    String sql = "SELECT column_name FROM all_tab_columns WHERE LOWER(table_name) = LOWER(?)";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, nomTable);

        try (ResultSet rs = ps.executeQuery()) {
            System.out.println("=== Colonnes de la table " + nomTable + " ===");
            while (rs.next()) {
                System.out.println(rs.getString("column_name"));
            }
        }
    }
}

public static void afficherTable(Connection conn, String nomTable) throws SQLException {
    String sql = "SELECT * FROM " + nomTable;

    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        ResultSetMetaData meta = rs.getMetaData();
        int nbCol = meta.getColumnCount();

        System.out.println("=== Contenu de " + nomTable + " ===");

        while (rs.next()) {
            for (int i = 1; i <= nbCol; i++) {
                System.out.print(meta.getColumnName(i) + "=" + rs.getString(i) + "  ");
            }
            System.out.println();
        }
    }
}
}
