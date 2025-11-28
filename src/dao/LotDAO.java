package dao;

import config.DataSourceProvider;
import model.AlertePeremption;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LotDAO {

    public List<AlertePeremption> findLotsPerissables() throws SQLException {

        String sql = """
            SELECT
                l.idLot,
                s.idProduit,
                s.idProducteur,
                p.nomProduit,
                l.dateLimite,
                (l.dateLimite - TRUNC(SYSDATE)) AS joursRestants
            FROM Lot l
            JOIN Stock s ON l.idStock = s.idStock
            JOIN Produit p ON p.idProduit = s.idProduit AND p.idProducteur = s.idProducteur
            WHERE l.dateLimite <= TRUNC(SYSDATE) + 7
            AND l.dateLimite > TRUNC(SYSDATE)
            ORDER BY l.dateLimite ASC
        """;

        Connection conn = DataSourceProvider.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            List<AlertePeremption> alertes = new ArrayList<>();

            while (rs.next()) {
                AlertePeremption a = new AlertePeremption();

                a.setIdLot(rs.getString("idLot"));
                a.setIdProduit(rs.getString("idProduit"));
                a.setIdProducteur(rs.getString("idProducteur"));
                a.setNomProduit(rs.getString("nomProduit"));
                a.setJoursRestants(rs.getInt("joursRestants"));

                Date dl = rs.getDate("dateLimite");
                if (dl != null) a.setDateLimite(dl.toLocalDate());

                alertes.add(a);
            }

            return alertes;
        }
    }


}
