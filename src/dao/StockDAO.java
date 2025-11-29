package dao;

import java.sql.*;

public class StockDAO {

    public ResultSet getLotsOrdonnesByIdStock(String idStock, Connection conn) {
        String query = "SELECT l.idLot, l.dateLimite FROM Lot l WHERE l.idStock = ? ORDER BY l.dateLimite ASC";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, idStock);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getIdStock(String idProduit, String idProducteur, Connection conn) {
        String query = "SELECT idStock FROM Stock WHERE idProduit = ? AND idProducteur = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, idProduit);
            pstmt.setString(2, idProducteur);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("idStock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getQuantitePreconditionneLot(String idLot, Connection conn) {
        String sql = "SELECT quantiteDisponiblePreconditionne FROM LotPreconditionne WHERE idLot = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idLot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("quantiteDisponiblePreconditionne");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Double getQuantiteVracLot(String idLot, Connection conn) {
        String sql = "SELECT quantiteDisponibleVrac FROM LotVrac WHERE idLot = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idLot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("quantiteDisponibleVrac");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void decrementPreconditionneLot(String idLot, int qte, Connection conn) throws SQLException {
        String sql = "UPDATE LotPreconditionne SET quantiteDisponiblePreconditionne = quantiteDisponiblePreconditionne - ? WHERE idLot = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qte);
            ps.setString(2, idLot);
            ps.executeUpdate();
        }
    }

    public void decrementVracLot(String idLot, double qte, Connection conn) throws SQLException {
        String sql = "UPDATE LotVrac SET quantiteDisponibleVrac = quantiteDisponibleVrac - ? WHERE idLot = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, qte);
            ps.setString(2, idLot);
            ps.executeUpdate();
        }
    }
}
