package dao;

import config.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConditionnementDAO {

    public void appliquerReduction(String idProduit, String idProducteur) throws SQLException {

    Connection conn = DataSourceProvider.getConnection();
    conn.setAutoCommit(false);

    // 1. Lecture et verrouillage de la ligne
    PreparedStatement ps1 = conn.prepareStatement("""
        SELECT prixVenteClient
        FROM Conditionnement
        WHERE idProduit = ? AND idProducteur = ?
        FOR UPDATE
    """);

    ps1.setString(1, idProduit);
    ps1.setString(2, idProducteur);

    ResultSet rs = ps1.executeQuery();
    double prixActuel = 0;

    if (rs.next()) {
        prixActuel = rs.getDouble("prixVenteClient");
    }

    // 2. Mise à jour sécurisée
    PreparedStatement ps2 = conn.prepareStatement("""
        UPDATE Conditionnement
        SET prixVenteClient = ROUND(? * 0.7, 2)
        WHERE idProduit = ? AND idProducteur = ?
    """);

    ps2.setDouble(1, prixActuel);
    ps2.setString(2, idProduit);
    ps2.setString(3, idProducteur);
    ps2.executeUpdate();

    conn.commit();

    rs.close();
    ps1.close();
    ps2.close();
}
}
