package dao;

import config.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConditionnementDAO {

    public void appliquerReduction(String idProduit, String idProducteur) throws SQLException {

        Connection conn = DataSourceProvider.getConnection();

        if (conn == null)
            throw new SQLException("Connexion null dans ConditionnementDAO");

        // Note: l'isolation doit être configurée au niveau du Service appelant.
        conn.setAutoCommit(false);

        double prixActuel;

        try (
            PreparedStatement ps1 = conn.prepareStatement("""
                SELECT prixVenteClient
                FROM Conditionnement
                WHERE idProduit = ? AND idProducteur = ?
                FOR UPDATE
            """)
        ) {
            ps1.setString(1, idProduit);
            ps1.setString(2, idProducteur);

            try (ResultSet rs = ps1.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException(
                        "Aucun conditionnement trouvé pour produit=" + idProduit +
                        " producteur=" + idProducteur
                    );
                }
                prixActuel = rs.getDouble("prixVenteClient");
            }
        }

        // Mise à jour après verrouillage
        try (
            PreparedStatement ps2 = conn.prepareStatement("""
                UPDATE Conditionnement
                SET prixVenteClient = ROUND(? * 0.7, 2)
                WHERE idProduit = ? AND idProducteur = ?
            """)
        ) {
            ps2.setDouble(1, prixActuel);
            ps2.setString(2, idProduit);
            ps2.setString(3, idProducteur);

            int updated = ps2.executeUpdate();

            if (updated == 0) {
                throw new SQLException("Échec mise à jour prix : ligne non trouvée");
            }

            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        }
    }

}
