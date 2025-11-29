package model;

public class ProduitDisponible {
    private String idProduit;
    private String idProducteur;
    private String nomProduit;
    private String categorie;
    private String description;
    private float prixVenteClient;
    private String typeConditionnement; // "Vrac" ou "Preconditionne"
    private Float poidsSachet; // pour préconditionné uniquement

    public ProduitDisponible() {}

    public String getIdProduit() { return idProduit; }
    public void setIdProduit(String idProduit) { this.idProduit = idProduit; }

    public String getIdProducteur() { return idProducteur; }
    public void setIdProducteur(String idProducteur) { this.idProducteur = idProducteur; }

    public String getNomProduit() { return nomProduit; }
    public void setNomProduit(String nomProduit) { this.nomProduit = nomProduit; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public float getPrixVenteClient() { return prixVenteClient; }
    public void setPrixVenteClient(float prixVenteClient) { this.prixVenteClient = prixVenteClient; }

    public String getTypeConditionnement() { return typeConditionnement; }
    public void setTypeConditionnement(String typeConditionnement) { this.typeConditionnement = typeConditionnement; }

    public Float getPoidsSachet() { return poidsSachet; }
    public void setPoidsSachet(Float poidsSachet) { this.poidsSachet = poidsSachet; }

    @Override
    public String toString() {
        if ("Vrac".equalsIgnoreCase(typeConditionnement)) {
            return nomProduit + " - " + prixVenteClient + " EUR/kg (Vrac)";
        } else {
            return nomProduit + " - " + prixVenteClient + " EUR (" + poidsSachet + " kg)";
        }
    }
}
