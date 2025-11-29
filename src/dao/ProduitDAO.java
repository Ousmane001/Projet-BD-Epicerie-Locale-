package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import config.DataSourceProvider;
import model.ProduitDisponible;

public class ProduitDAO {

    private final Connection conn;

    public ProduitDAO() {
        this.conn = DataSourceProvider.getConnection();
    }

    public String getIdProducteurByProduit(String idProduit) {
        String sql = "SELECT idProducteur FROM Produit WHERE idProduit = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idProduit);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("idProducteur");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Float getPrixVenteClient(String idProduit, String idProducteur) {
        String sql = "SELECT prixVenteClient FROM Conditionnement WHERE idProduit = ? AND idProducteur = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idProduit);
            ps.setString(2, idProducteur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getFloat("prixVenteClient");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Float getPoidsSachet(String idProduit, String idProducteur) {
        String sql = "SELECT cp.poidsSachet FROM Conditionnement c JOIN ConditionnementPreconditionne cp ON c.idConditionnement = cp.idConditionnement WHERE c.idProduit = ? AND c.idProducteur = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idProduit);
            ps.setString(2, idProducteur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getFloat("poidsSachet");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean estDisponible(String idProduit, String idProducteur, java.sql.Date dateJour) {
        String sql = "SELECT 1 FROM ProduitEstDisponible ped JOIN Disponibilite d ON ped.idDisponibilite = d.idDisponibilite WHERE ped.idProduit = ? AND ped.idProducteur = ? AND ? BETWEEN d.debutDisponibilite AND d.finDisponibilite";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idProduit);
            ps.setString(2, idProducteur);
            ps.setDate(3, dateJour);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ProduitDisponible> getProduitsDisponibles() {
        List<ProduitDisponible> produits = new ArrayList<>();
        String sql = """
            SELECT DISTINCT p.idProduit, p.idProducteur, p.nomProduit, p.categorie, 
                   DBMS_LOB.SUBSTR(p.description, 4000, 1) AS description,
                   c.prixVenteClient,
                   CASE WHEN cv.idConditionnement IS NOT NULL THEN 'Vrac'
                        WHEN cp.idConditionnement IS NOT NULL THEN 'Preconditionne'
                        ELSE 'Inconnu' END AS typeConditionnement,
                   cp.poidsSachet
            FROM Produit p
            JOIN Conditionnement c ON p.idProduit = c.idProduit AND p.idProducteur = c.idProducteur
            LEFT JOIN ConditionnementVrac cv ON c.idConditionnement = cv.idConditionnement
            LEFT JOIN ConditionnementPreconditionne cp ON c.idConditionnement = cp.idConditionnement
            JOIN ProduitEstDisponible ped ON p.idProduit = ped.idProduit AND p.idProducteur = ped.idProducteur
            JOIN Disponibilite d ON ped.idDisponibilite = d.idDisponibilite
            WHERE TRUNC(SYSDATE) BETWEEN d.debutDisponibilite AND d.finDisponibilite
            ORDER BY p.categorie, p.nomProduit
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ProduitDisponible prod = new ProduitDisponible();
                prod.setIdProduit(rs.getString("idProduit"));
                prod.setIdProducteur(rs.getString("idProducteur"));
                prod.setNomProduit(rs.getString("nomProduit"));
                prod.setCategorie(rs.getString("categorie"));
                prod.setDescription(rs.getString("description"));
                prod.setPrixVenteClient(rs.getFloat("prixVenteClient"));
                prod.setTypeConditionnement(rs.getString("typeConditionnement"));
                Float poids = rs.getFloat("poidsSachet");
                if (!rs.wasNull()) prod.setPoidsSachet(poids);
                produits.add(prod);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
}
