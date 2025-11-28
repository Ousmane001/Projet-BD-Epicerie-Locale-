package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AlertePeremption {
    private String idLot;
    private String idProduit;
    private String idProducteur;
    private int joursRestants;
    private double reductionProposee = 0.30;
    private LocalDate dateAlerte = LocalDate.now();
    private LocalDate dateLimite;
    private String nomProduit;


    public AlertePeremption() {}

    // Getters + Setters
    public String getIdLot() { return idLot; }
    public void setIdLot(String idLot) { this.idLot = idLot; }

    public String getIdProduit() { return idProduit; }
    public void setIdProduit(String idProduit) { this.idProduit = idProduit; }

    public String getIdProducteur() { return idProducteur; }
    public void setIdProducteur(String idProducteur) { this.idProducteur = idProducteur; }

    public int getJoursRestants() { return joursRestants; }
    public void setJoursRestants(int joursRestants) { this.joursRestants = joursRestants; }

    public double getReductionProposee() { return reductionProposee; }
    public LocalDate getDateAlerte() { return dateAlerte; }

    public LocalDate getDateLimite(){
        return dateLimite;
    };
    public void setDateLimite(LocalDate d) {
        this.dateLimite = d;
    }

    public String getNomProduit() { return nomProduit; }
    public void setNomProduit(String nomProduit) { this.nomProduit = nomProduit; }

}
