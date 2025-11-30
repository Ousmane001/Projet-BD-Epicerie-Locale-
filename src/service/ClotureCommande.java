package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import config.DataSourceProvider;
import dao.CommandeDAO;
import dao.CommandeDAO.CommandeInfo;

/**
 * Service de préparation / clôture des commandes.
 * Règles rappelées:
 * - Domicile: paiement obligatoire "En ligne"; stock sorti au passage à "Prête".
 * - Boutique: pas de statut "En préparation"; statut passe directement à "Prête" à la création,
 *             stock peut être sorti juste avant la remise (ici lors de la clôture si pas déjà fait).
 * - Clôture interdite si déjà récupérée/livrée (dateRecuperation non nulle ou statut final).
 */
public class ClotureCommande {

    private final CommandeDAO commandeDAO = new CommandeDAO();

    /**
     * Prépare une commande (transition vers "Prête") pour les commandes domicile.
     * Déclenche la sortie de stock et enregistre la date de paiement si paiement en ligne.
     */
    public void preparerCommande(String idCommande) {
        Connection conn = DataSourceProvider.getConnection();
        int oldIsolation = Connection.TRANSACTION_READ_COMMITTED;
        
        try {
            oldIsolation = conn.getTransactionIsolation();
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            
            CommandeInfo info = commandeDAO.getCommandeInfo(idCommande);
            if (info == null) throw new IllegalStateException("Commande introuvable: " + idCommande);
            if (!"Domicile".equals(info.modeRecuperation)) {
                throw new IllegalStateException("La préparation explicite ne concerne que les commandes Domicile");
            }
            if (!"En préparation".equals(info.statut)) {
                throw new IllegalStateException("Statut attendu 'En préparation' pour préparer: actuel=" + info.statut);
            }
            if (!"En ligne".equals(info.modePaiement)) {
                throw new IllegalStateException("Paiement obligatoire en ligne pour Domicile");
            }

            // Encaisse si pas déjà payé (simulation) puis datePaiement
            if (info.datePaiement == null) {
                if (!commandeDAO.encaisseCommande(idCommande)) {
                    throw new IllegalStateException("Paiement refusé");
                }
                commandeDAO.enregistrerDatePaiement(idCommande);
            }

            // Sortie de stock FEFO au passage à "Prête"
            commandeDAO.enleveDansStock(idCommande);
            commandeDAO.changeStatutCommande(idCommande, "Prête");
            
            conn.commit();
        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ignore) {}
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException(e);
        } finally {
            try {
                conn.setTransactionIsolation(oldIsolation);
            } catch (SQLException ignore) {}
        }
    }

    /**
     * Clôture la commande (réception boutique ou livraison effectuée).
     * Enregistre date de paiement (si boutique) et date de récupération.
     */
    public void cloturerCommande(String idCommande) {
        Connection conn = DataSourceProvider.getConnection();
        int oldIsolation = Connection.TRANSACTION_READ_COMMITTED;
        
        try {
            oldIsolation = conn.getTransactionIsolation();
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            
            if (commandeDAO.estCloturee(idCommande)) {
                throw new IllegalStateException("Commande déjà clôturée: " + idCommande);
            }
            CommandeInfo info = commandeDAO.getCommandeInfo(idCommande);
            if (info == null) throw new IllegalStateException("Commande introuvable: " + idCommande);

            if (!"Prête".equals(info.statut)) {
                throw new IllegalStateException("Statut doit être 'Prête' pour clôturer. Actuel=" + info.statut);
            }

            if ("Domicile".equals(info.modeRecuperation) && !"En ligne".equals(info.modePaiement)) {
                throw new IllegalStateException("Livraison requiert paiement 'En ligne'");
            }

            // Paiement boutique au moment de la récupération
            if ("Boutique".equals(info.modeRecuperation) && "En Boutique".equals(info.modePaiement) && info.datePaiement == null) {
                if (!commandeDAO.encaisseCommande(idCommande)) {
                    throw new IllegalStateException("Paiement boutique refusé");
                }
                commandeDAO.enregistrerDatePaiement(idCommande);
            }

            // Pour Boutique: stock peut ne pas encore être décrémenté (puisque statut déjà 'Prête' sans décrément).
            if ("Boutique".equals(info.modeRecuperation)) {
                commandeDAO.enleveDansStock(idCommande);
            }

            commandeDAO.enregistreDateReceptionCommande(idCommande);
            commandeDAO.changeStatutCommande(idCommande, "Récupérée/Livrée");
            
            conn.commit();
        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ignore) {}
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException(e);
        } finally {
            try {
                conn.setTransactionIsolation(oldIsolation);
            } catch (SQLException ignore) {}
        }
    }

    /**
     * Passage manuel d'une commande au statut "Prête" sans la clôturer.
     * - Domicile: paiement en ligne obligatoire, sortie de stock immédiate (FEFO) et enregistrement datePaiement.
     * - Boutique: simple changement de statut (stock sortira à la clôture).
     */
    public void marquerPrete(String idCommande) {
        Connection conn = DataSourceProvider.getConnection();
        int oldIsolation = Connection.TRANSACTION_READ_COMMITTED;
        
        try {
            oldIsolation = conn.getTransactionIsolation();
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            
            CommandeInfo info = commandeDAO.getCommandeInfo(idCommande);
            if (info == null) throw new IllegalStateException("Commande introuvable: " + idCommande);
            if ("Prête".equals(info.statut)) {
                throw new IllegalStateException("Commande déjà au statut 'Prête'");
            }
            if ("Récupérée/Livrée".equals(info.statut)) {
                throw new IllegalStateException("Commande déjà clôturée");
            }

            if ("Domicile".equals(info.modeRecuperation)) {
                if (!"En ligne".equals(info.modePaiement)) {
                    throw new IllegalStateException("Paiement en ligne requis pour livraison domicile");
                }
                // Paiement si nécessaire
                if (info.datePaiement == null) {
                    if (!commandeDAO.encaisseCommande(idCommande)) {
                        throw new IllegalStateException("Paiement refusé");
                    }
                    commandeDAO.enregistrerDatePaiement(idCommande);
                }
                // Sortie de stock maintenant
                commandeDAO.enleveDansStock(idCommande);
                commandeDAO.changeStatutCommande(idCommande, "Prête");
            } else if ("Boutique".equals(info.modeRecuperation)) {
                // Juste passage à Prête (pas de sortie stock maintenant)
                commandeDAO.changeStatutCommande(idCommande, "Prête");
            } else {
                throw new IllegalStateException("Mode de récupération inconnu: " + info.modeRecuperation);
            }
            
            conn.commit();
        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ignore) {}
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException(e);
        } finally {
            try {
                conn.setTransactionIsolation(oldIsolation);
            } catch (SQLException ignore) {}
        }
    }

    /** Affiche info livraison (frais + date estimée) sans modifier l'état. */
    public void afficherInfosLivraison(String idCommande) {
        CommandeInfo info = commandeDAO.getCommandeInfo(idCommande);
        if (info == null) {
            System.err.println("Commande introuvable: " + idCommande);
            return;
        }
        if (!"Domicile".equals(info.modeRecuperation)) {
            System.out.println("Commande " + idCommande + " mode=" + info.modeRecuperation + ": pas de livraison.");
            return;
        }
        String idMode = commandeDAO.recupIdInfoLivraison(idCommande);
        if (idMode == null) {
            System.err.println("Informations livraison manquantes pour " + idCommande);
            return;
        }
        int frais = commandeDAO.calculFraisDeLivraison(idMode);
        LocalDate dateEstimee = commandeDAO.calculDateEstimeeDeLivraison(idMode);
        System.out.println("Livraison commande " + idCommande + ": frais=" + frais + "€, date estimée=" + dateEstimee);
    }
}