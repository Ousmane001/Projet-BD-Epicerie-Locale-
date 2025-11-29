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
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        List<AlertePeremption> alertes = new ArrayList<>();

        while (rs.next()) {
            AlertePeremption a = new AlertePeremption();
            a.setIdLot(rs.getString("idLot"));
            a.setIdProduit(rs.getString("idProduit"));
            a.setIdProducteur(rs.getString("idProducteur"));
            a.setNomProduit(rs.getString("nomProduit"));

            Date dl = rs.getDate("dateLimite");
            if (dl != null) a.setDateLimite(dl.toLocalDate());

            a.setJoursRestants(rs.getInt("joursRestants"));

            alertes.add(a);
        }

        rs.close();
        stmt.close();
        return alertes;
    }

    public String getConditionnementByIdLot(String idLot, Connection conn){
        String sqlPre = "SELECT 1 FROM LotPreconditionne WHERE idLot = ?";
        String sqlVra = "SELECT 1 FROM LotVrac WHERE idLot = ?";
        try{
            PreparedStatement pstmtPre = conn.prepareStatement(sqlPre);
            pstmtPre.setString(1, idLot);
            ResultSet rsPre = pstmtPre.executeQuery();
            if(rsPre.next()){
                return "Preconditionne";
            }
            PreparedStatement pstmtVra = conn.prepareStatement(sqlVra);
            pstmtVra.setString(1, idLot);
            ResultSet rsVra = pstmtVra.executeQuery();
            if(rsVra.next()){
                return "Vrac";
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return "inconnu";
    }

    public LocalDate getDatePeremptionByIdLot(String idLot, Connection conn){
        String sql = "SELECT dateLimite FROM Lot WHERE idLot = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idLot);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Date dateLimite = rs.getDate("dateLimite");
                return dateLimite != null ? dateLimite.toLocalDate() : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
