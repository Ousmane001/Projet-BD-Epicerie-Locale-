package interfaceGraphique;
import java.sql.SQLException;
import java.time.LocalDate;

import config.DataSourceProvider;
import service.StockService.StockService;

DataSourceProvider DataSourceProvider = new DataSourceProvider();
Connection conn = DataSourceProvider.getConnection();

public class CommandeProduit extends Commande {
    //private String idLigneCommande = commandeDAO.generateId("LC");
    //private String idCommande;
    private String idProduit;
    private int quantite;
    private String typeConditionnement;
    private String idProducteur;
    private float prixUnitaire;
    private float sousTotal;
    private LocalDate dateEstimeeLivraison;


    public CommandeProduit(String idProduit, String typeConditionnement, int quantite, LocalDate dateEstimeeLivraison) {
        //this.idLigneCommande = commandeDAO.generateId("LC");
        //this.idCommande = idCommande;
        this.idProduit = idProduit;
        this.quantite = quantite;
        this.typeConditionnement = typeConditionnement;
        this.idProducteur =  produitDAO.getProducteur(this.idProduit, conn); //A voir 
        this.prixUnitaire = produitDAO.getPrixVenteClient(this.idProduit, conn);
        this.sousTotal = this.prixUnitaire * this.quantite;
        this.dateEstimeeLivraison = dateEstimeeLivraison;
    }

    public String getIdProduit() {
        return idProduit;
    }

    public String getIdProducteur() {
        return idProducteur;
    }

    public int getQuantite() {
        return quantite;
    }

    @Override
    public String getTypeConditionnement() {
        return typeConditionnement;
    }

    public float getPrixUnitaire() {
        return prixUnitaire;
    }

    public float getSousTotal() {
        return sousTotal;
    }

    public LocalDate getDateEstimeeLivraison() {
        return dateEstimeeLivraison;
    }

    public float getPoids(){
        if(typeConditionnement.equals("preconditionne")){
            return produitDAO.getPoidsUnitaire(idProduit, typeConditionnement, conn) * quantite;
        }
        else{
            return quantite;
        }
    }

    public boolean stock_suffisant_produit(LocalDate dateEstimeeLivraison){
        StockService stockService = new StockService();
        return stockService.stock_suffisant_produit(idProduit, idProducteur, quantite, dateEstimeeLivraison, typeConditionnement, conn);
    }
    
    
    
    
    public void executeTransaction(){
        // À implémenter : logique pour exécuter la transaction de la ligne de commande produit
        try{

            if(!this.stock_suffisant_produit(dateEstimeeLivraison)){
                throw new SQLException("Stock insuffisant pour le produit ID: " + idProduit);  
            }

            commandeContenant.enregistrerLigneCommandeContenant();

        } catch (SQLException e){
            e.printStackTrace();
        }

                    commandeContenant.enregistrerLigneCommandeContenant();
    }    
}
