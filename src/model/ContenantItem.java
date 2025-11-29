package model;

public class ContenantItem {
    private String referenceContenant;
    private int quantite;

    public ContenantItem(String referenceContenant, int quantite) {
        this.referenceContenant = referenceContenant;
        this.quantite = quantite;
    }

    public String getReferenceContenant() { return referenceContenant; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
}
