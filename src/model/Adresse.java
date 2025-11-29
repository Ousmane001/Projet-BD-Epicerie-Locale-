package model;

public class Adresse {
    private String idAdresse;
    private String rue;
    private String ville;
    private String codePostal;

    public Adresse(String idAdresse, String rue, String ville, String codePostal) {
        this.idAdresse = idAdresse;
        this.rue = rue;
        this.ville = ville;
        this.codePostal = codePostal;
    }

    public String getIdAdresse() { return idAdresse; }
    public String getRue() { return rue; }
    public String getVille() { return ville; }
    public String getCodePostal() { return codePostal; }

    @Override
    public String toString() {
        return rue + ", " + ville + " (" + codePostal + ")";
    }
}