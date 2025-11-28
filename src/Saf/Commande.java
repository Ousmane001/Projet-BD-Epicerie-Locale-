package Saf;

public abstract class Commande {
    
    public abstract int getQuantite(){};    

    public abstract float getPrixUnitaire() {}

    public abstract float getSousTotal(){}
    
    public String getTypeConditionnement() {
        return "Vrac";
    }
}
