package model;

public class Contenant {
    private String referenceContenant;
    private String typeContenant;
    private float capaciteContenant;
    private int stockContenant;
    private String caractereContenant;
    private float prixContenant;

    public Contenant(String referenceContenant, String typeContenant, float capaciteContenant,
                     int stockContenant, String caractereContenant, float prixContenant) {
        this.referenceContenant = referenceContenant;
        this.typeContenant = typeContenant;
        this.capaciteContenant = capaciteContenant;
        this.stockContenant = stockContenant;
        this.caractereContenant = caractereContenant;
        this.prixContenant = prixContenant;
    }

    public String getReferenceContenant() { return referenceContenant; }
    public String getTypeContenant() { return typeContenant; }
    public float getCapaciteContenant() { return capaciteContenant; }
    public int getStockContenant() { return stockContenant; }
    public String getCaractereContenant() { return caractereContenant; }
    public float getPrixContenant() { return prixContenant; }

    @Override
    public String toString() {
        return typeContenant + " (" + capaciteContenant + "L) - " + 
               String.format("%.2f EUR", prixContenant) + " - " + caractereContenant;
    }
}
