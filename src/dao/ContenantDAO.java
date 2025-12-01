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

    public boolean stocksuffisantContenant(String refContenant, double quantiteDemande, Connection conn) {
    String sql = "SELECT stockContenant FROM Contenant WHERE referenceContenant = ? FOR UPDATE";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, refContenant);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                double stock = rs.getDouble("stockContenant");
                return stock >= quantiteDemande;
            } else {
                // Le contenant n’existe même pas ? Bah là c’est la merde → insuffisant par défaut
                return false;
            }
        }

    } catch (Exception e) {
        // On va pas crasher toute l’app juste parce qu’une requête a décidé de faire sa diva
        return false;
    }
}

}
