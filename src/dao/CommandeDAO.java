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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        
        return LocalDate.now();
    }
    
    /**
     * Récupère le délai de disponibilité maximal parmi tous les produits d'une commande
     * @param idCommande L'identifiant de la commande
     * @return Le délai maximal en jours (0 si aucun délai ou erreur)
     */
    private int getDelaiMaxDisponibilite(String idCommande) {
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
            e.printStackTrace();
        }
        
        return 0; // Aucun délai par défaut
    }
    
    /**
     * Met à jour la date estimée de livraison dans la base de données
     * @param idModeDeRecuperationDomicile L'identifiant du mode de récupération domicile
     * @param dateEstimee La date estimée de livraison
     */
    private void updateDateEstimeeLivraison(String idModeDeRecuperationDomicile, LocalDate dateEstimee) {
        String sql = "UPDATE ModeRecuperationDomicile SET dateEstimeeLivraison = ? WHERE idModeRecuperationDomicile = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(dateEstimee));
            stmt.setString(2, idModeDeRecuperationDomicile);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la date estimée de livraison:");
            e.printStackTrace();
        }
    }
}
