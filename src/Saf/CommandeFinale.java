import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import config.DataSourceProvider;
import dao.CommandeDAO;
import dao.StockDAO;


public class CommandeFinale {
    private String idCommande, idModeRecuperation;
    LocalDate datrCommande, heureCommande;
    String statutCommande, modePaiement, datePaiement, modeRecuperation, dateRecuperation;
    ArrayList<Commande> listeCommandesProduits;

    public CommandeFinale(ArrayList<Commande> listeCommandesProduits) {
        this.idCommande = this.generateId("CM");
        this.listeCommandesProduits = listeCommandesProduits;
    }

    public String getIdCommande() {
        return idCommande;
    }

    public String getIdModeRecuperation(){
        return idModeRecuperation;
    }

    public ArrayList<Commande> getListeCommandesProduits() {
        return listeCommandesProduits;
    }

    public String generateId(String prefix) {
        int n = (int)(Math.random() * 1_000_0000);
        return prefix + String.format("%07d", n);
    }

    public void enregistrerCommande(Connection conn){
        
    }


    public void validerCommande() throws SQLException {
    DataSourceProvider dsp = new DataSourceProvider();
    StockDAO stockDAO = new StockDAO();
    CommandeDAO commandeDAO = new CommandeDAO();
    CommandeService commandeService = new CommandeService();
;
    try (Connection conn = dsp.getConnection()) {

        conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        conn.setAutoCommit(false);

        float PoidsTotal = 0;
        float totalLivraison = 0;

        for (Commande cmd : listeCommandesProduits) {
            if (cmd instanceof CommandeProduit) {
                CommandeProduit cp = (CommandeProduit) cmd;

                // Hard check du stock dans la transaction
                String idStock = StockDAO.getIdStock(cp.getIdProduit(), cp.getIdProducteur(), conn);
                StockService stockService = new StockService(idStock);
                boolean stockOk = stockService.stock_suffisant_produit(cp.getIdProduit(), cp.getIdProducteur(), cp.getQuantite(), cp.getDateEstimeeLivraison(), cp.getTypeConditionnement(), conn);

                if (!stockOk) {
                    throw new SQLException("Stock insuffisant pour le produit ID: " + cp.getIdProduit());
                }

                // Crée et enregistre la ligne de commande
                LigneCommande ligne = new LigneCommande(this.idCommande, commandeService.generateId("LC"), cp);
                ligne.enregistrerLigneCommande(conn);

                // ajout du poids
                PoidsTotal+=cp.getPoids();
                totalLivraison+=cp.getSousTotal();

            } else if (cmd instanceof CommandeContenant) {
                CommandeContenant cc = (CommandeContenant) cmd;

                // Pareil, hard check si nécessaire


                // ajouts
                totalLivraison+=cc.getSousTotal();

                LigneCommande ligne = new LigneCommande(this.idCommande, commandeService.generateId("LC"), cc);
                ligne.enregistrerLigneCommande(conn);
            }
            
            String idModeRcuperationDomicile = commandeDAO.getIdModeRecuperation();

            if(idModeRcuperationDomicile.equals("Boutique")){
                totalLivraison+=commandeDAO.calculFraisDeLivraison(idModeRcuperationDomicile);
            }
            
            
            this.enregistrerCommande(conn);
        }

        conn.commit();

    } catch (SQLException e) {
        e.printStackTrace();
        throw e; // rollback automatique via try-with-resources ou à gérer explicitement
    }
}


}