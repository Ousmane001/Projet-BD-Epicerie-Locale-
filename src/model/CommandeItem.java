package model;

public class CommandeItem {
    private final String idProduit;
    private final String idProducteur;
    private final String typeConditionnement; // "Vrac" ou "Preconditionne"
    private final double quantite; // kg pour Vrac, unités pour Préconditionné

    public CommandeItem(String idProduit, String idProducteur, String typeConditionnement, double quantite) {
        this.idProduit = idProduit;
        this.idProducteur = idProducteur;
        this.typeConditionnement = typeConditionnement;
        this.quantite = quantite;
    }

    public String getIdProduit() { return idProduit; }
    public String getIdProducteur() { return idProducteur; }
    public String getTypeConditionnement() { return typeConditionnement; }
    public double getQuantite() { return quantite; }
}
