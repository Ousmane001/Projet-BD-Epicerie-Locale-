import java.sql.Connection;

public class LigneCommande{
    String idCommande;
    String idLigneCommande;
    Commande commande;
    float sousTotalLigne;

    public LigneCommande(String idCommande, String idLigneCommande, Commande commande){
        this.idCommande = idCommande;
        this.idLigneCommande = idLigneCommande;
        this.commande = commande;
        this.sousTotalLigne = commande.getSousTotal();
    }

    public String getIdLigneCommande(){
        return idLigneCommande;
    }

    public Commande getCommande(){
        return commande;
    }

    public float getSousTotalLigne(){
        return sousTotalLigne;
    }

    
    
    public void enregistrerLigneCommande(Connection conn){
        // À implémenter : logique pour enregistrer la ligne de commande
        
        ligneCommandeDAO.enregistreLigneCommande(idLigneCommande, commande.getPrixUnitaire(), sousTotalLigne, idCommande, conn);
                    
        if(commande instanceof CommandeProduit){
            CommandeProduit cmdProd = (CommandeProduit) commande;
            ligneCommandeDAO.enregistreLigneCommandeProduit(idLigneCommande, cmdProd.getIdProduit(), cmdProd.getTypeConditionnement(), cmdProd.getQuantite(), conn);
            ligneCommandeDAO.enregistreLigneCommandeConditionnement(idLigneCommande, commande.getTypeConditionnement(), commande.getQuantite(), conn);
        }
        else if(commande instanceof CommandeContenant){
            CommandeContenant cmdCont = (CommandeContenant) commande;
            ligneCommandeDAO.enregistreLigneCommandeContenant(idLigneCommande, cmdCont.getRefContenant(), conn);
        }
    }


}