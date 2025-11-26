package dao;

import config.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConditionnementDAO {

    public void appliquerReduction(String idProduit, String idProducteur) throws SQLException {

        String sql = """
            UPDATE Conditionnement
            SET prixVenteClient = ROUND(prixVenteClient * 0.7, 2)
            WHERE idProduit = ? AND idProducteur = ?
        """;

        Connection conn = DataSourceProvider.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, idProduit);
        ps.setString(2, idProducteur);
        ps.executeUpdate();

        ps.close();
    }
}
