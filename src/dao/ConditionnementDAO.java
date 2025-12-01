// package dao;

// import config.DataSourceProvider;

// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.util.ArrayList;
// import java.util.List;

// public class ConditionnementDAO {
//     private final Connection conn = DataSourceProvider.getConnection();

//     public void appliquerReduction(String idProduit, String idProducteur) throws SQLException {

        

//         if (conn == null)
//             throw new SQLException("Connexion null dans ConditionnementDAO");

//         conn.setAutoCommit(false);

//         double prixActuel;

//         try (
//             PreparedStatement ps1 = conn.prepareStatement("""
//                 SELECT prixVenteClient
//                 FROM Conditionnement
//                 WHERE idProduit = ? AND idProducteur = ?
//                 FOR UPDATE
//             """)
//         ) {
//             ps1.setString(1, idProduit);
//             ps1.setString(2, idProducteur);

//             try (ResultSet rs = ps1.executeQuery()) {
//                 if (!rs.next()) {
//                     throw new SQLException(
//                         "Aucun conditionnement trouvé pour produit=" + idProduit +
//                         " producteur=" + idProducteur
//                     );
//                 }
//                 prixActuel = rs.getDouble("prixVenteClient");
//             }
//         }

//         // Mise à jour après verrouillage
//         try (
//             PreparedStatement ps2 = conn.prepareStatement("""
//                 UPDATE Conditionnement
//                 SET prixVenteClient = ROUND(? * 0.7, 2)
//                 WHERE idProduit = ? AND idProducteur = ?
//             """)
//         ) {
//             ps2.setDouble(1, prixActuel);
//             ps2.setString(2, idProduit);
//             ps2.setString(3, idProducteur);

//             int updated = ps2.executeUpdate();

//             if (updated == 0) {
//                 throw new SQLException("Échec mise à jour prix : ligne non trouvée");
//             }

//             conn.commit();
//         } catch (SQLException ex) {
//             conn.rollback();
//             throw ex;
//         }
//     }

//     // Récupère la liste des poids disponibles pour un conditionnement préconditionné
//     public List<Float> getPoidsSachets(String idConditionnement) throws SQLException {
//         List<Float> poidsSachets = new ArrayList<>();
//         String sql = "SELECT poidsSachet FROM ConditionnementPreconditionne WHERE idConditionnement = ? ORDER BY poidsSachet ASC";
//         try (PreparedStatement ps = conn.prepareStatement(sql)) {
//             ps.setString(1, idConditionnement);
//             try (ResultSet rs = ps.executeQuery()) {
//                 while (rs.next()) {
//                     poidsSachets.add(rs.getFloat("poidsSachet"));
//                 }
//             }
//         }
//         return poidsSachets;
//     }

// }


package dao;

import config.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConditionnementDAO {
    private final Connection conn = DataSourceProvider.getConnection();

    public void appliquerReduction(String idProduit, String idProducteur) throws SQLException {

        

        if (conn == null)
            throw new SQLException("Connexion null dans ConditionnementDAO");

        boolean oldAutoCommit = conn.getAutoCommit();
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
        } finally {
            conn.setAutoCommit(oldAutoCommit);
        }
    }

    // Récupère la liste des poids disponibles pour un conditionnement préconditionné
    public List<Float> getPoidsSachets(String idConditionnement) throws SQLException {
        List<Float> poidsSachets = new ArrayList<>();
        String sql = "SELECT poidsSachet FROM ConditionnementPreconditionne WHERE idConditionnement = ? ORDER BY poidsSachet ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idConditionnement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    poidsSachets.add(rs.getFloat("poidsSachet"));
                }
            }
        }
        return poidsSachets;
    }

}