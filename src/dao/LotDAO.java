package dao;

import config.DataSourceProvider;
import model.AlertePeremption;

import java.sql.*;
import java.time.LocalDate;
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
            WHERE l.dateLimite <= TRUNC(CURRENT_DATE) + 7
              AND l.dateLimite > TRUNC(CURRENT_DATE)
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
            a.setJoursRestants(rs.getInt("joursRestants"));
            a.setReductionProposee(0.30);
            a.setDateAlerte(LocalDate.now());
            a.setStatutAlerte("proposee");
            alertes.add(a);
        }

        rs.close();
        stmt.close();
        return alertes;
    }
}
