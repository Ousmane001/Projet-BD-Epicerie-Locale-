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
                (l.dateLimite - TRUNC(CURRENT_DATE)) AS joursRestants
            FROM Lot l
            JOIN Stock s ON l.idStock = s.idStock
            WHERE l.dateLimite BETWEEN DATE '2025-11-20' AND DATE '2025-11-30'
        """;

        Connection conn = DataSourceProvider.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        List<AlertePeremption> alertes = new ArrayList<>();

        while (rs.next()) {
            AlertePeremption a = new AlertePeremption();
            a.setIdLot(rs.getString("idLot"));
            a.setIdProduit(rs.getString("idProduit"));
            a.setIdProducteur(rs.getString("idProducteur"));
            Date dl = rs.getDate("dateLimite");
            if(dl != null) a.setDateLimite(dl.toLocalDate());
            a.setJoursRestants(rs.getInt("joursRestants"));
            alertes.add(a);
        }

        rs.close();
        stmt.close();

        return alertes;
    }
}
