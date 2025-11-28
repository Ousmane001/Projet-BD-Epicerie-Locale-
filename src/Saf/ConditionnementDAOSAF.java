package dao;

import config.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConditionnementDAOSAF {

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

    public String getTypeConditionnementById(String idConditionnement, Connection conn){
        String sqlPre = "SELECT 1 FROM ConditionnementPreconditionne WHERE idConditionnement = ?";
        String sqlVra = "SElECT 1 FROM ConditionnementVrac WHERE idConditionnement = ?";

        try{
            PreparedStatement pstmtPre = conn.prepareStatement(sqlPre);
            pstmtPre.setString(1, idConditionnement);
            ResultSet rsPre = pstmtPre.executeQuery();
            if(rsPre.next()){
                return "Preconditionne"; ;
            }

            PreparedStatement pstmtVra = conn.prepareStatement(sqlVra);
            pstmtVra.setString(1, idConditionnement);
            ResultSet rsVra = pstmtVra.executeQuery();
            if(rsVra.next()){
                return "Vrac";
            }
        } catch (java.sql.SQLException e){
            e.printStackTrace();
        }
            return "inconnu";
    }
}
