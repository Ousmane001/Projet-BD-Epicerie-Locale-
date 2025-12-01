package model;

import java.time.LocalDate;

public class AlertePeremption {

    private String idLot;
    private String idProduit;
    private String idProducteur;
    private int joursRestants;
    private double reductionProposee = 0.30;
    private LocalDate dateAlerte = LocalDate.now();
    private LocalDate dateLimite;
    private String nomProduit;

    // ==== Champs perte ====
    private String idPerte;
    private LocalDate datePerte;
    private String typeAlerte; // "PEREMPTION" ou "PERTE"

    public AlertePeremption() {}

    // ==== Getters / Setters ====

    public String getTypeAlerte() { return typeAlerte; }
    public void setTypeAlerte(String typeAlerte) { this.typeAlerte = typeAlerte; }

    public String getIdPerte() { return idPerte; }
    public void setIdPerte(String idPerte) { this.idPerte = idPerte; }

    public LocalDate getDatePerte() { return datePerte; }
    public void setDatePerte(LocalDate datePerte) { this.datePerte = datePerte; }

    public String getIdLot() { return idLot; }
    public void setIdLot(String idLot) { this.idLot = idLot; }

    public String getIdProduit() { return idProduit; }
    public void setIdProduit(String idProduit) { this.idProduit = idProduit; }

    public String getIdProducteur() { return idProducteur; }
    public void setIdProducteur(String idProducteur) { this.idProducteur = idProducteur; }

    public int getJoursRestants() { return joursRestants; }
    public void setJoursRestants(int joursRestants) { this.joursRestants = joursRestants; }

    public double getReductionProposee() { return reductionProposee; }
    public void setReductionProposee(double r) { this.reductionProposee = r; }

    public LocalDate getDateAlerte() { return dateAlerte; }

    public LocalDate getDateLimite() { return dateLimite; }
    public void setDateLimite(LocalDate d) { this.dateLimite = d; }

    public String getNomProduit() { return nomProduit; }
    public void setNomProduit(String nomProduit) { this.nomProduit = nomProduit; }
}