package dao;

import config.DataSourceProvider;
import model.Adresse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdresseDAO {
    public List<Adresse> getAdressesClient(String idClient) {
        String sql = "SELECT a.idAdresse, a.rue, a.ville, a.codePostal " +
                     "FROM PossedeAdresse pa JOIN Adresse a ON pa.idAdresse = a.idAdresse " +
                     "WHERE pa.idClient = ? ORDER BY a.ville, a.rue";
        List<Adresse> list = new ArrayList<>();
        try (Connection conn = DataSourceProvider.getValidConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idClient);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Adresse(
                        rs.getString("idAdresse"),
                        rs.getString("rue"),
                        rs.getString("ville"),
                        rs.getString("codePostal")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public Adresse creerAdresseClient(String idClient, String rue, String ville, String codePostal) {
        Connection conn = DataSourceProvider.getValidConnection();
        PreparedStatement psAdresse = null;
        PreparedStatement psLink = null;
        try {
            conn.setAutoCommit(false);
            String idAdresse = generateId("AD");
            String sqlAdr = "INSERT INTO Adresse (idAdresse, rue, ville, codePostal) VALUES (?, ?, ?, ?)";
            psAdresse = conn.prepareStatement(sqlAdr);
            psAdresse.setString(1, idAdresse);
            psAdresse.setString(2, rue);
            psAdresse.setString(3, ville);
            psAdresse.setString(4, codePostal);
            psAdresse.executeUpdate();

            String sqlLink = "INSERT INTO PossedeAdresse (idClient, idAdresse) VALUES (?, ?)";
            psLink = conn.prepareStatement(sqlLink);
            psLink.setString(1, idClient);
            psLink.setString(2, idAdresse);
            psLink.executeUpdate();

            conn.commit();
            return new Adresse(idAdresse, rue, ville, codePostal);
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignore) {}
            e.printStackTrace();
            return null;
        } finally {
            try { if (psAdresse != null) psAdresse.close(); } catch (SQLException ignore) {}
            try { if (psLink != null) psLink.close(); } catch (SQLException ignore) {}
        }
    }

    private String generateId(String prefix) {
        int n = (int)(Math.random() * 1_000_0000);
        return prefix + String.format("%07d", n);
    }
}
