package dao;

import config.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientDAO {

    private final Connection conn;

    public ClientDAO() {
        this.conn = DataSourceProvider.getConnection();
    }

    /**
     * Retourne l'id du client associé à l'email, ou null si introuvable
     */
    public String getClientIdByEmail(String email) {
        String sql = "SELECT c.idClient FROM Client c JOIN Contact ct ON c.idContact = ct.idContact WHERE ct.email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
