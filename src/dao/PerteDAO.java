package dao;

import config.DataSourceProvider;
import model.AlertePeremption;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerteDAO {

    public List<AlertePeremption> getPertesAvecProduit() throws SQLException {

        Connection conn = DataSourceProvider.getConnection();
        if (conn == null) throw new SQLException("Connexion null dans PerteDAO");

        String sql = """
            SELECT 
                p.idPerte,
                p.datePerte,
                pp.idProduit,
                pp.idProducteur,
                prod.nomProduit
            FROM Perte p
            JOIN PerteProduit pp ON p.idPerte = pp.idPerte
            JOIN Produit prod 
                ON prod.idProduit = pp.idProduit 
               AND prod.idProducteur = pp.idProducteur
        """;

        List<AlertePeremption> pertes = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                AlertePeremption a = new AlertePeremption();
                a.setTypeAlerte("PERTE");

                a.setIdPerte(rs.getString("idPerte"));
                a.setDatePerte(rs.getDate("datePerte").toLocalDate());

                a.setIdProduit(rs.getString("idProduit"));
                a.setIdProducteur(rs.getString("idProducteur"));
                a.setNomProduit(rs.getString("nomProduit"));

                // valeurs neutres pour les pertes
                a.setIdLot("—");
                a.setJoursRestants(0);
                a.setReductionProposee(0);
                a.setDateLimite(null);

                pertes.add(a);
            }
        }

        return pertes;
    }
}
