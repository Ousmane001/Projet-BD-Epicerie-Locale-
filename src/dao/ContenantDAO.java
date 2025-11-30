package dao;

import config.DataSourceProvider;
import model.Contenant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContenantDAO {
    
    public List<Contenant> getTousLesContenants() {
        List<Contenant> contenants = new ArrayList<>();
        String sql = "SELECT referenceContenant, typeContenant, capaciteContenant, stockContenant, caractereContenant, prixContenant " +
                     "FROM Contenant " +
                     "WHERE stockContenant > 0 " +
                     "ORDER BY typeContenant, capaciteContenant";
        
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Contenant c = new Contenant(
                    rs.getString("referenceContenant"),
                    rs.getString("typeContenant"),
                    rs.getFloat("capaciteContenant"),
                    rs.getInt("stockContenant"),
                    rs.getString("caractereContenant"),
                    rs.getFloat("prixContenant")
                );
                contenants.add(c);
            }
        } catch (SQLException e) {
        }
        
        return contenants;
    }
}
