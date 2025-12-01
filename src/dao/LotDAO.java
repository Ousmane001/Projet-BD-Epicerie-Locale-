// package dao;

// import config.DataSourceProvider;
// import model.AlertePeremption;

// import java.sql.*;
// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.List;

// public class LotDAO {

//     public List<AlertePeremption> findLotsPerissables() throws SQLException {
//         String sql = """
//             SELECT
//                 l.idLot,
//                 s.idProduit,
//                 s.idProducteur,
//                 p.nomProduit,
//                 l.dateLimite,
//                 (l.dateLimite - TRUNC(SYSDATE)) AS joursRestants
//             FROM Lot l
//             JOIN Stock s ON l.idStock = s.idStock
//             JOIN Produit p ON p.idProduit = s.idProduit AND p.idProducteur = s.idProducteur
//             WHERE l.dateLimite <= TRUNC(SYSDATE) + 7
//             AND l.dateLimite > TRUNC(SYSDATE)
//             ORDER BY l.dateLimite ASC
//         """;

//         Connection conn = DataSourceProvider.getConnection();
//         try (PreparedStatement stmt = conn.prepareStatement(sql);
//              ResultSet rs = stmt.executeQuery()) {
//             List<AlertePeremption> alertes = new ArrayList<>();
//             while (rs.next()) {
//                 AlertePeremption a = new AlertePeremption();
//                 a.setIdLot(rs.getString("idLot"));
//                 a.setIdProduit(rs.getString("idProduit"));
//                 a.setIdProducteur(rs.getString("idProducteur"));
//                 a.setNomProduit(rs.getString("nomProduit"));
//                 a.setJoursRestants(rs.getInt("joursRestants"));
//                 Date dl = rs.getDate("dateLimite");
//                 if (dl != null) a.setDateLimite(dl.toLocalDate());
//                 alertes.add(a);
//             }
//             return alertes;
//         }
//     }

//     /**
//      * Détermine le type de conditionnement du lot: "Preconditionne" si présent dans LotPreconditionne,
//      * "Vrac" si présent dans LotVrac, sinon null.
//      */
//     public String getConditionnementByIdLot(String idLot, Connection conn) throws SQLException {
//         if (idLot == null || conn == null) return null;
//         String sqlPre = "SELECT 1 FROM LotPreconditionne WHERE idLot = ?";
//         try (PreparedStatement ps = conn.prepareStatement(sqlPre)) {
//             ps.setString(1, idLot);
//             try (ResultSet rs = ps.executeQuery()) {
//                 if (rs.next()) return "Preconditionne";
//             }
//         }
//         String sqlVrac = "SELECT 1 FROM LotVrac WHERE idLot = ?";
//         try (PreparedStatement ps = conn.prepareStatement(sqlVrac)) {
//             ps.setString(1, idLot);
//             try (ResultSet rs = ps.executeQuery()) {
//                 if (rs.next()) return "Vrac";
//             }
//         }
//         return null;
//     }

//     /**
//      * Retourne la date de péremption (dateLimite) du lot ou null si introuvable.
//      */
//     public LocalDate getDatePeremptionByIdLot(String idLot, Connection conn) throws SQLException {
//         if (idLot == null || conn == null) return null;
//         String sql = "SELECT dateLimite FROM Lot WHERE idLot = ?";
//         try (PreparedStatement ps = conn.prepareStatement(sql)) {
//             ps.setString(1, idLot);
//             try (ResultSet rs = ps.executeQuery()) {
//                 if (rs.next()) {
//                     Date d = rs.getDate("dateLimite");
//                     return d != null ? d.toLocalDate() : null;
//                 }
//             }
//         }
//         return null;
//     }
// }


package dao;

import config.DataSourceProvider;
import model.AlertePeremption;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LotDAO {

    public List<AlertePeremption> findLotsPerissables() throws SQLException {
        String sql = """
            SELECT
                l.idLot,
                s.idProduit,
                s.idProducteur,
                p.nomProduit,
                l.dateLimite,
                (l.dateLimite - TRUNC(SYSDATE)) AS joursRestants
            FROM Lot l
            JOIN Stock s ON l.idStock = s.idStock
            JOIN Produit p ON p.idProduit = s.idProduit AND p.idProducteur = s.idProducteur
            WHERE l.dateLimite <= TRUNC(SYSDATE) + 7
            AND l.dateLimite > TRUNC(SYSDATE)
            ORDER BY l.dateLimite ASC
        """;

        Connection conn = DataSourceProvider.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<AlertePeremption> alertes = new ArrayList<>();
            while (rs.next()) {
                AlertePeremption a = new AlertePeremption();
                a.setIdLot(rs.getString("idLot"));
                a.setIdProduit(rs.getString("idProduit"));
                a.setIdProducteur(rs.getString("idProducteur"));
                a.setNomProduit(rs.getString("nomProduit"));
                a.setJoursRestants(rs.getInt("joursRestants"));
                Date dl = rs.getDate("dateLimite");
                if (dl != null) a.setDateLimite(dl.toLocalDate());
                alertes.add(a);
            }
            return alertes;
        }
    }

    /**
     * Détermine le type de conditionnement du lot: "Preconditionne" si présent dans LotPreconditionne,
     * "Vrac" si présent dans LotVrac, sinon null.
     */
    public String getConditionnementByIdLot(String idLot, Connection conn) throws SQLException {
        if (idLot == null || conn == null) return null;
        String sqlPre = "SELECT 1 FROM LotPreconditionne WHERE idLot = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlPre)) {
            ps.setString(1, idLot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return "Preconditionne";
            }
        }
        String sqlVrac = "SELECT 1 FROM LotVrac WHERE idLot = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlVrac)) {
            ps.setString(1, idLot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return "Vrac";
            }
        }
        return null;
    }

    /**
     * Retourne la date de péremption (dateLimite) du lot ou null si introuvable.
     */
    public LocalDate getDatePeremptionByIdLot(String idLot, Connection conn) throws SQLException {
        if (idLot == null || conn == null) return null;
        String sql = "SELECT dateLimite FROM Lot WHERE idLot = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idLot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date d = rs.getDate("dateLimite");
                    return d != null ? d.toLocalDate() : null;
                }
            }
        }
        return null;
    }
}
