package model;

public class ProduitCatalogue {
    private String idProduit;
    private String idProducteur;
    private String nomProduit;
    private String categorie;
    private String description;
    private String bio;
    private String label;
    private String allergene;
    private String origineGeographique;
    private Integer delaiDisponibilite;
    private Float prixVenteClient;
    private String typeConditionnement; // "Vrac" ou "Préconditionné"
    private Float poidsSachet; // Pour préconditionné uniquement
    private String statutProduit; // "Disponible" ou "Pas disponible"
    private Float quantiteDisponible; // Quantité en stock

    public ProduitCatalogue() {}

    // Getters et Setters
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

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getAllergene() { return allergene; }
    public void setAllergene(String allergene) { this.allergene = allergene; }

    public String getOrigineGeographique() { return origineGeographique; }
    public void setOrigineGeographique(String origineGeographique) { this.origineGeographique = origineGeographique; }

    public Integer getDelaiDisponibilite() { return delaiDisponibilite; }
    public void setDelaiDisponibilite(Integer delaiDisponibilite) { this.delaiDisponibilite = delaiDisponibilite; }

    public Float getPrixVenteClient() { return prixVenteClient; }
    public void setPrixVenteClient(Float prixVenteClient) { this.prixVenteClient = prixVenteClient; }

    public String getTypeConditionnement() { return typeConditionnement; }
    public void setTypeConditionnement(String typeConditionnement) { this.typeConditionnement = typeConditionnement; }

    public Float getPoidsSachet() { return poidsSachet; }
    public void setPoidsSachet(Float poidsSachet) { this.poidsSachet = poidsSachet; }

    public String getStatutProduit() { return statutProduit; }
    public void setStatutProduit(String statutProduit) { this.statutProduit = statutProduit; }

    public Float getQuantiteDisponible() { return quantiteDisponible; }
    public void setQuantiteDisponible(Float quantiteDisponible) { this.quantiteDisponible = quantiteDisponible; }

    @Override
    public String toString() {
        return "ProduitCatalogue{" +
                "nomProduit='" + nomProduit + '\'' +
                ", categorie='" + categorie + '\'' +
                ", prix=" + prixVenteClient +
                ", type=" + typeConditionnement +
                ", statut=" + statutProduit +
                '}';
    }
}
