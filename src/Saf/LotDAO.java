package dao;

import config.DataSourceProvider;
import model.AlertePeremption;

import java.time.LocalDate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LotDAO {

    public LotDAO(){
        // constructeur bidon ....
    }

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
            alertes.add(a);
        }

        rs.close();
        stmt.close();

        return alertes;
    }

    
    
    public String getConditionnementByIdLot(String idLot, Connection conn){
        String sqlPre = "SELECT 1 FROM LotPreconditionne WHERE idConditionnement = ?";
        String sqlVra = "SElECT 1 FROM LotVrac WHERE idConditionnement = ?";

        try{
            PreparedStatement pstmtPre = conn.prepareStatement(sqlPre);
            pstmtPre.setString(1, idLot);
            ResultSet rsPre = pstmtPre.executeQuery();
            if(rsPre.next()){
                return "Preconditionne"; ;
            }

            PreparedStatement pstmtVra = conn.prepareStatement(sqlVra);
            pstmtVra.setString(1, idLot);
            ResultSet rsVra = pstmtVra.executeQuery();
            if(rsVra.next()){
                return "Vrac";
            } 
        }   catch (java.sql.SQLException e){
            e.printStackTrace();
        }
       return "inconnu";
    }

    public LocalDate getDatePeremptionByIdLot(String idLot, Connection conn){
        String sql = "SELECT dateLimite FROM Lot WHERE idLot = ?";

        try (
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idLot);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Date dateLimite = rs.getDate("dateLimite");
                return dateLimite.toLocalDate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getQuantite(String idLot, Connection conn){
        String sql = "SELECT quantite FROM Lot L JOIN LotVrac LV ON L.idLot WHERE idLot = ?";

        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, idLot);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getInt("quantite");
            }
        } catch (java.sql.SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
}   
