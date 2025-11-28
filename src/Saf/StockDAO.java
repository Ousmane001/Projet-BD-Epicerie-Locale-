package dao;

import java.sql.Connection;
import java.sql.ResultSet;

public class StockDAO {
    public StockDAO() {
        // constructeur bidon ....
    }

    public ResultSet getLotsOrdonnesByIdStock(String idStock, Connection conn) {
        String query = "SELECT * FROM Lot WHERE idStock = ? ORDER BY dateLimite ASC";
        try {
            java.sql.PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, idStock);
            ResultSet rs = pstmt.executeQuery();
            return rs;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getIdStock(String idProduit, String idProducteur, Connection conn){
        String query = "SELECT idStock FROM Stock WHERE idProduit=? AND id_Producteur=?";
        try {
            java.sql.PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, idProduit);
            pstmt.setString(2, idProducteur);
            ResultSet rs = pstmt.executeQuery();
            return rs.getString("idStock");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;

    }
}
