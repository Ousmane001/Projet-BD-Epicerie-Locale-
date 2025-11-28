package interfaceGraphique;

public class CommandeContenant extends Commande {
    //private String idLigneCommande;
    //private String idCommande;
    private String refContenant;
    private int quantite;
    private float prixUnitaire;
    private float sousTotal;
    
    

    public CommandeContenant(String idCommande, String refContenant, int quantite) {
        //this.idLigneCommande = commandeDAO.generateId("LC");
        //this.idCommande = idCommande;
        this.refContenant = refContenant;
        this.quantite = quantite;
        this.prixUnitaire = contenantDAO.getPrixContenant(this.refContenant, conn); //A voir, normalement le prix devrait m'etre communique pour eviter qu'il ne soit modifié entre temps 
        this.sousTotal = this.prixUnitaire * this.quantite;
    }

    // public String getIdLigneCommande() {
    //     return idLigneCommande;
    // }

    // public String getIdCommande() {
    //     return idCommande;
    // }

    public String getRefContenant() {
        return refContenant;
    }

    public int getQuantite() {
        return quantite;
    }

    public float getPrixUnitaire() {
        return prixUnitaire;
    }

    public float getSousTotal() {
        return sousTotal;
    }
}
