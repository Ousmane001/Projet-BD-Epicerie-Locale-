package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import config.DataSourceProvider;

public class CommandeDAO {
    
    private Connection connection;
    
    public CommandeDAO(){
        this.connection = DataSourceProvider.getConnection();
    }


    /**
     * Encaisse une commande en calculant le montant total et en vérifiant le paiement
     * @param idCommande L'identifiant de la commande
     * @return true si le paiement est accepté, false sinon
     */
    public boolean encaisseCommande(String idCommande){
        try {
            // 1. Calculer le montant total de la commande
            float montantTotal = calculerMontantTotal(idCommande);
            
            if (montantTotal <= 0) {
                System.err.println("Erreur: Montant de la commande invalide");
                return false;
            }
            
            // 2. Récupérer le mode de paiement
            String modePaiement = recupModePayement(idCommande);
            
            if (modePaiement == null) {
                System.err.println("Erreur: Mode de paiement non trouvé");
                return false;
            }
            
            // 3. Ajouter les frais de livraison si mode domicile
            String modeRecuperation = recupModeRecuperation(idCommande);
            float fraisLivraison = 0;
            
            if ("Domicile".equals(modeRecuperation)) {
                String idModeRecupDomicile = recupIdInfoLivraison(idCommande);
                if (idModeRecupDomicile != null) {
                    fraisLivraison = calculFraisDeLivraison(idModeRecupDomicile);
                }
            }
            
            float montantFinal = montantTotal + fraisLivraison;
            
            
            if ("En ligne".equals(modePaiement)) {
                traiterPaiementEnLigne(idCommande, montantFinal);
            } else if ("En Boutique".equals(modePaiement)) {
                traiterPaiementBoutique(idCommande, montantFinal);
            }
            

            System.out.println("Paiement accepté pour la commande " + idCommande);
            System.out.println("  - Montant produits: " + String.format("%.2f", montantTotal) + " €");
            System.out.println("  - Frais livraison: " + String.format("%.2f", fraisLivraison) + " €");
            System.out.println("  - TOTAL: " + String.format("%.2f", montantFinal) + " €");

            
            return true;
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'encaissement de la commande:");
            return false;
        }
    }
    
    /**
     * Calcule le montant total de la commande (somme des lignes)
     * @param idCommande L'identifiant de la commande
     * @return Le montant total
     */
    private float calculerMontantTotal(String idCommande) {
        String sql = "SELECT SUM(sousTotalLigne) AS montantTotal " +
                     "FROM LigneCommande " +
                     "WHERE idCommande = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCommande);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("montantTotal");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du montant total:");
        }
        
        return 0;
    }
    
    /**
     * Traite un paiement en ligne (simulation)
     * @param idCommande L'identifiant de la commande
     * @param montant Le montant à payer
     * @return true si le paiement est accepté
     */
    private void traiterPaiementEnLigne(String idCommande, float montant) {
        // Simulation de paiement en ligne
        // Dans une vraie application, on appellerait une API de paiement (Stripe, PayPal, etc.)
        
        System.out.println("Traitement du paiement en ligne...");
        System.out.println("Montant: " + String.format("%.2f", montant) + " €");
        
        // Pour la simulation, on accepte tous les paiements en ligne
        // Dans la réalité, on vérifierait la carte bancaire, les fonds disponibles, etc.
        
        try {
            // Simuler un délai de traitement
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
    }
    
    /**
     * Traite un paiement en boutique (simulation)
     * @param idCommande L'identifiant de la commande
     * @param montant Le montant à payer
     * @return true si le paiement est accepté
     */
    private boolean traiterPaiementBoutique(String idCommande, float montant) {
        // Simulation de paiement en boutique
        // Dans une vraie application, on attendrait la confirmation du caissier
        
        System.out.println("Paiement à effectuer en boutique");
        System.out.println("Montant à payer: " + String.format("%.2f", montant) + " €");
        
        // Pour la simulation, on considère que le paiement en boutique sera effectué
        // Le client paiera lors de la récupération
        return true;
    }

    public void enleveDansStock(String idCommande){
        // Décrémente le stock (FEFO) pour toutes les lignes de la commande
        try {
            connection.setAutoCommit(false);

            StockDAO stockDAO = new StockDAO();

            // Récupérer toutes les lignes produit de la commande
            String sqlLignes = "SELECT lcp.idLigneCommande, lcp.idProduit, lcp.idProducteur \n" +
                    "FROM LigneCommandeProduit lcp \n" +
                    "WHERE lcp.idCommande = ?";
            try (PreparedStatement psLignes = connection.prepareStatement(sqlLignes)) {
                psLignes.setString(1, idCommande);
                try (ResultSet rsLignes = psLignes.executeQuery()) {
                    while (rsLignes.next()) {
                        String idLigne = rsLignes.getString("idLigneCommande");
                        String idProduit = rsLignes.getString("idProduit");
                        String idProducteur = rsLignes.getString("idProducteur");

                        // Déterminer s'il s'agit de vrac ou préconditionné en regardant les tables spécifiques
                        Integer qtePre = null;
                        Double qteVrac = null;

                        String sqlPre = "SELECT quantiteCommandePreconditionne FROM LigneCommandeProduitPreconditionne " +
                                       "WHERE idLigneCommande = ? AND idCommande = ?";
                        try (PreparedStatement psPre = connection.prepareStatement(sqlPre)) {
                            psPre.setString(1, idLigne);
                            psPre.setString(2, idCommande);
                            try (ResultSet rsPre = psPre.executeQuery()) {
                                if (rsPre.next()) {
                                    qtePre = rsPre.getInt("quantiteCommandePreconditionne");
                                }
                            }
                        }

                        if (qtePre == null) {
                            String sqlVrac = "SELECT quantiteCommandeVrac FROM LigneCommandeProduitVrac " +
                                             "WHERE idLigneCommande = ? AND idCommande = ?";
                            try (PreparedStatement psVrac = connection.prepareStatement(sqlVrac)) {
                                psVrac.setString(1, idLigne);
                                psVrac.setString(2, idCommande);
                                try (ResultSet rsVrac = psVrac.executeQuery()) {
                                    if (rsVrac.next()) {
                                        qteVrac = rsVrac.getDouble("quantiteCommandeVrac");
                                    }
                                }
                            }
                        }

                        // Récupérer le stock pour ce produit/producteur
                        String idStock = stockDAO.getIdStock(idProduit, idProducteur, connection);
                        if (idStock == null) {
                            throw new SQLException("Stock introuvable pour le produit: " + idProduit);
                        }

                        // Parcourir les lots FEFO et décrémenter
                        try (ResultSet lots = stockDAO.getLotsOrdonnesByIdStock(idStock, connection)) {
                            int restePre = (qtePre != null ? qtePre : 0);
                            double resteVrac = (qteVrac != null ? qteVrac : 0.0);
                            while (lots != null && lots.next() && (restePre > 0 || resteVrac > 0.0)) {
                                String idLot = lots.getString("idLot");
                                java.sql.Date dateLimite = lots.getDate("dateLimite");
                                if (dateLimite != null && dateLimite.before(new java.util.Date())) continue; // lot périmé

                                if (qtePre != null) {
                                    Integer dispoPre = stockDAO.getQuantitePreconditionneLot(idLot, connection);
                                    if (dispoPre == null || dispoPre <= 0) continue;
                                    int prise = Math.min(dispoPre, restePre);
                                    stockDAO.decrementPreconditionneLot(idLot, prise, connection);
                                    restePre -= prise;
                                } else if (qteVrac != null) {
                                    Double dispoVrac = stockDAO.getQuantiteVracLot(idLot, connection);
                                    if (dispoVrac == null || dispoVrac <= 0) continue;
                                    double prise = Math.min(dispoVrac, resteVrac);
                                    stockDAO.decrementVracLot(idLot, prise, connection);
                                    resteVrac -= prise;
                                }
                            }
                            if (restePre > 0 || resteVrac > 0.0) {
                                throw new SQLException("Stock insuffisant ou lots périmés pour le produit: " + idProduit);
                            }
                        }
                    }
                }
            }

            connection.commit();
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ignore) {}
            throw new RuntimeException(e);
        }
    }


    /**
     * Récupère le mode de récupération d'une commande
     * @param idCommande L'identifiant de la commande
     * @return Le mode de récupération ("Boutique" ou "Domicile")
     */
    public String recupModeRecuperation(String idCommande){
        String sql = "SELECT modeRecuperation FROM Commande WHERE idCommande = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCommande);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("modeRecuperation");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du mode de récupération:");
        }
        
        return null;
    }

    /**
     * Récupère le mode de paiement d'une commande
     * @param idCommande L'identifiant de la commande
     * @return Le mode de paiement ("En ligne" ou "En Boutique")
     */
    public String recupModePayement(String idCommande){
        String sql = "SELECT modePaiement FROM Commande WHERE idCommande = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCommande);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("modePaiement");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du mode de paiement:");
        }
        
        return null;
    }

    /**
     * Récupère le statut actuel d'une commande
     * @param idCommande L'identifiant de la commande
     * @return Le statut de la commande ("En préparation", "Prête", "En livraison", "Annulée", "Récupérée/Livrée")
     */
    public String recupStatutCommande(String idCommande){
        String sql = "SELECT statutCommande FROM Commande WHERE idCommande = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCommande);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("statutCommande");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du statut de la commande:");
        }
        
        return null;
    }

    /**
     * Change le statut d'une commande
     * @param idCommande L'identifiant de la commande
     * @param nouveau_statut Le nouveau statut à définir
     */
    public void changeStatutCommande(String idCommande, String nouveau_statut){
        String sql = "UPDATE Commande SET statutCommande = ? WHERE idCommande = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nouveau_statut);
            stmt.setString(2, idCommande);
            
            int rowsUpdated = stmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("Statut de la commande " + idCommande + " mis à jour: " + nouveau_statut);
            } else {
                System.err.println("Aucune commande trouvée avec l'ID: " + idCommande);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut de la commande:");
        }
    }
    
    /**
     * Enregistre la date de récupération de la commande (date actuelle)
     * @param idCommande L'identifiant de la commande
     */
    public void enregistreDateReceptionCommande(String idCommande){
        String sql = "UPDATE Commande SET dateRecuperation = SYSDATE WHERE idCommande = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCommande);
            
            int rowsUpdated = stmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("Date de récupération enregistrée pour la commande " + idCommande);
            } else {
                System.err.println("Aucune commande trouvée avec l'ID: " + idCommande);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement de la date de récupération:");
        }
    }

    /**
     * Récupère l'identifiant du mode de récupération à domicile pour une commande
     * @param idCommande L'identifiant de la commande
     * @return L'identifiant du mode de récupération domicile
     */
    public String recupIdInfoLivraison(String idCommande){
        String sql = "SELECT idModeRecuperationDomicile FROM ModeRecuperationDomicile WHERE idCommande = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCommande);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("idModeRecuperationDomicile");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'ID du mode de livraison:");
        }
        
        return null;
    }

    /**
     * Calcule les frais de livraison basés sur la distance et le poids
     * @param idModeDeRecuperationDomicile L'identifiant du mode de récupération domicile
     * @return Les frais de livraison en euros
     * 
     * Règles de calcul:
     * Distance: 0 à 50 km → +0 €, 50 à 300 km → +1 €, 300 à 2000 km → +2 €, > 2000 km → +3 €
     * Poids: Par kilo → 0.8€ (France), 1.2€ (DOM-TOM), 1.5€ (International)
     */
    public int calculFraisDeLivraison(String idModeDeRecuperationDomicile){
        String sql = "SELECT distanceAdresseBoutique, poidsTotalCommande, typePaysLivraison " +
                     "FROM ModeRecuperationDomicile WHERE idModeRecuperationDomicile = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idModeDeRecuperationDomicile);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    float distance = rs.getFloat("distanceAdresseBoutique");
                    float poids = rs.getFloat("poidsTotalCommande");
                    String typePays = rs.getString("typePaysLivraison");
                    
                    // Calcul frais de distance
                    int fraisDistance = 0;
                    if (distance > 2000) {
                        fraisDistance = 3;
                    } else if (distance > 300) {
                        fraisDistance = 2;
                    } else if (distance > 50) {
                        fraisDistance = 1;
                    }
                    
                    // Calcul frais de poids
                    float tarifParKilo = 0.8f; // nous avons decide ça pour la France Métropolitaine 
                    if ("DOM-TOM".equals(typePays)) {
                        tarifParKilo = 1.2f;
                    } else if ("International".equals(typePays)) {
                        tarifParKilo = 1.5f;
                    }
                    
                    float fraisPoids = poids * tarifParKilo;
                    
                    // Total des frais (arrondi à l'entier supérieur)
                    return (int) Math.ceil(fraisDistance + fraisPoids);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul des frais de livraison:");
        }
        
        return 0;
    }

    /**
     * Calcule la date estimée de livraison basée sur la zone, la distance et le délai de disponibilité
     * @param idModeDeRecuperationDomicile L'identifiant du mode de récupération domicile
     * @return La date estimée de livraison
     * 
     * Règles de calcul:
     * France Métropolitaine: 2 jours de base
     * DOM-TOM: 5 jours de base
     * International: 7 jours de base
     * + délai supplémentaire selon distance (0-50km: +0j, 50-300km: +1j, 300-2000km: +2j, >2000km: +3j)
     * + délai maximal de disponibilité des produits commandés (temps d'acquisition par l'épicerie)
     */
    public LocalDate calculDateEstimeeDeLivraison(String idModeDeRecuperationDomicile){
        String sql = "SELECT m.distanceAdresseBoutique, m.typePaysLivraison, m.idCommande " +
                     "FROM ModeRecuperationDomicile m WHERE m.idModeRecuperationDomicile = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idModeDeRecuperationDomicile);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    float distance = rs.getFloat("distanceAdresseBoutique");
                    String typePays = rs.getString("typePaysLivraison");
                    String idCommande = rs.getString("idCommande");
                    
                    // Calcul des jours de base selon la zone
                    int joursBase = 2; // France Métropolitaine par défaut
                    if ("DOM-TOM".equals(typePays)) {
                        joursBase = 5;
                    } else if ("International".equals(typePays)) {
                        joursBase = 7;
                    }
                    
                    // Calcul des jours supplémentaires selon la distance
                    int joursSupplementaires = 0;
                    if (distance > 2000) {
                        joursSupplementaires = 3;
                    } else if (distance > 300) {
                        joursSupplementaires = 2;
                    } else if (distance > 50) {
                        joursSupplementaires = 1;
                    }
                    
                    // Récupération du délai maximal de disponibilité des produits de la commande
                    int delaiMaxDisponibilite = getDelaiMaxDisponibilite(idCommande);
                    
                    // Calcul de la date estimée avec tous les délais
                    int totalJours = joursBase + joursSupplementaires + delaiMaxDisponibilite;
                    LocalDate dateEstimee = LocalDate.now().plusDays(totalJours);
                    
                    // si besoin, on decommente cette ligne de mis à jour de la date estimée dans la base de données
                    //updateDateEstimeeLivraison(idModeDeRecuperationDomicile, dateEstimee);
                    
                    return dateEstimee;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul de la date estimée de livraison:");
        }
        
        return LocalDate.now();
    }
    
    /**
     * Récupère le délai de disponibilité maximal parmi tous les produits d'une commande
     * @param idCommande L'identifiant de la commande
     * @return Le délai maximal en jours (0 si aucun délai ou erreur)
     */
    public int getDelaiMaxDisponibilite(String idCommande) {
        String sql = "SELECT MAX(p.delaiDisponibilite) AS delaiMax " +
                     "FROM LigneCommande lc " +
                     "JOIN LigneCommandeProduit lcp ON lc.idLigneCommande = lcp.idLigneCommande " +
                     "JOIN Produit p ON lcp.idProduit = p.idProduit AND lcp.idProducteur = p.idProducteur " +
                     "WHERE lc.idCommande = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCommande);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int delaiMax = rs.getInt("delaiMax");
                    // Si le délai est NULL dans la BD, rs.getInt() retourne 0
                    return delaiMax;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du délai de disponibilité maximal:");
        }
        
        return 0; // Aucun délai par défaut
    }
    
    /**
     * Met à jour la date estimée de livraison dans la base de données
     * @param idModeDeRecuperationDomicile L'identifiant du mode de récupération domicile
     * @param dateEstimee La date estimée de livraison
     */
    // private void updateDateEstimeeLivraison(String idModeDeRecuperationDomicile, LocalDate dateEstimee) {
    //     String sql = "UPDATE ModeRecuperationDomicile SET dateEstimeeLivraison = ? WHERE idModeRecuperationDomicile = ?";
        
    //     try (PreparedStatement stmt = connection.prepareStatement(sql)) {
    //         stmt.setDate(1, java.sql.Date.valueOf(dateEstimee));
    //         stmt.setString(2, idModeDeRecuperationDomicile);
            
    //         stmt.executeUpdate();
    //     } catch (SQLException e) {
    //         System.err.println("Erreur lors de la mise à jour de la date estimée de livraison:");
    //         e.printStackTrace();
    //     }
    // }
    /** Fin des anciennes méthodes */

    /**
     * Retourne les informations essentielles d'une commande pour la clôture / préparation.
     */
    public CommandeInfo getCommandeInfo(String idCommande) {
        String sql = "SELECT statutCommande, modeRecuperation, modePaiement, dateRecuperation, datePaiement " +
                "FROM Commande WHERE idCommande = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, idCommande);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CommandeInfo ci = new CommandeInfo();
                    ci.idCommande = idCommande;
                    ci.statut = rs.getString("statutCommande");
                    ci.modeRecuperation = rs.getString("modeRecuperation");
                    ci.modePaiement = rs.getString("modePaiement");
                    ci.dateRecuperation = rs.getTimestamp("dateRecuperation");
                    ci.datePaiement = rs.getTimestamp("datePaiement");
                    return ci;
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }

    /** Enregistre la date de paiement si pas déjà renseignée. */
    public boolean enregistrerDatePaiement(String idCommande) {
        String check = "SELECT datePaiement FROM Commande WHERE idCommande = ?";
        try (PreparedStatement ps = connection.prepareStatement(check)) {
            ps.setString(1, idCommande);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getTimestamp("datePaiement") != null) {
                    return false; // déjà payé
                }
            }
        } catch (SQLException e) { }
        String upd = "UPDATE Commande SET datePaiement = SYSDATE WHERE idCommande = ?";
        try (PreparedStatement ps = connection.prepareStatement(upd)) {
            ps.setString(1, idCommande);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { }
        return false;
    }

    /** Vérifie si la commande est déjà clôturée (dateRecuperation non nulle ou statut final). */
    public boolean estCloturee(String idCommande) {
        String sql = "SELECT dateRecuperation, statutCommande FROM Commande WHERE idCommande = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, idCommande);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("dateRecuperation") != null ||
                            "Récupérée/Livrée".equals(rs.getString("statutCommande"));
                }
            }
        } catch (SQLException e) { }
        return false;
    }

    // DTO interne
    public static class CommandeInfo {
        public String idCommande;
        public String statut;
        public String modeRecuperation;
        public String modePaiement;
        public java.sql.Timestamp dateRecuperation;
        public java.sql.Timestamp datePaiement;
    }

}
