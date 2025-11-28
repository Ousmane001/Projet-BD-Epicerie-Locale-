import java.sql.*;
import config.DataSourceProvider;
import config.JdbcDriverLoader;

public class TestCatalogue {
    public static void main(String[] args) {
        System.out.println("=== TEST CATALOGUE ===\n");
        
        // 1. Charger le driver
        try {
            new JdbcDriverLoader();
            System.out.println("✓ Driver chargé\n");
        } catch (Exception e) {
            System.err.println("✗ Erreur chargement driver: " + e.getMessage());
            return;
        }
        
        // 2. Connexion
        DataSourceProvider.initConnection();
        Connection conn = DataSourceProvider.getConnection();
        
        if (conn == null) {
            System.err.println("✗ Pas de connexion !");
            return;
        }
        System.out.println("✓ Connexion OK\n");
        
        // 3. Test simple : nombre de produits
        String sql1 = "SELECT COUNT(*) as nb FROM Produit";
        try (PreparedStatement stmt = conn.prepareStatement(sql1);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int nb = rs.getInt("nb");
                System.out.println("Nombre de produits dans Produit : " + nb);
            }
        } catch (SQLException e) {
            System.err.println("Erreur requête Produit: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 4. Test de quelques produits
        String sql2 = "SELECT idProduit, nomProduit, categorie FROM Produit WHERE ROWNUM <= 5";
        System.out.println("\nPremiers produits :");
        try (PreparedStatement stmt = conn.prepareStatement(sql2);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("  - " + rs.getString("idProduit") + " : " + 
                                   rs.getString("nomProduit") + " (" + 
                                   rs.getString("categorie") + ")");
            }
        } catch (SQLException e) {
            System.err.println("Erreur requête liste: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 5. Test de la requête complète du catalogue
        String sql3 = "SELECT p.idProduit, p.nomProduit, p.categorie, " +
             "c.prixVenteClient, " +
             "CASE " +
                 "WHEN cv.idConditionnement IS NOT NULL THEN 'Vrac' " +
                 "WHEN cp.idConditionnement IS NOT NULL THEN 'Préconditionné' " +
                 "ELSE 'N/A' " +
             "END AS typeConditionnement " +
             "FROM Produit p " +
             "JOIN Conditionnement c ON p.idProduit = c.idProduit AND p.idProducteur = c.idProducteur " +
             "LEFT JOIN ConditionnementVrac cv ON c.idConditionnement = cv.idConditionnement " +
             "LEFT JOIN ConditionnementPreconditionne cp ON c.idConditionnement = cp.idConditionnement " +
             "WHERE ROWNUM <= 5";
        
        System.out.println("\nTest requête catalogue complète :");
        try (PreparedStatement stmt = conn.prepareStatement(sql3);
             ResultSet rs = stmt.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("  " + count + ". " + rs.getString("nomProduit") + 
                                   " - " + rs.getFloat("prixVenteClient") + "€ - " + 
                                   rs.getString("typeConditionnement"));
            }
            System.out.println("Total trouvé : " + count);
        } catch (SQLException e) {
            System.err.println("Erreur requête catalogue: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 6. Vérifier les tables liées
        System.out.println("\n=== Vérification tables ===");
        String[] tables = {"Conditionnement", "ConditionnementVrac", "ConditionnementPreconditionne", 
                           "ProduitEstDisponible", "Disponibilite"};
        for (String table : tables) {
            String sqlCount = "SELECT COUNT(*) as nb FROM " + table;
            try (PreparedStatement stmt = conn.prepareStatement(sqlCount);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println(table + " : " + rs.getInt("nb") + " lignes");
                }
            } catch (SQLException e) {
                System.err.println("Erreur table " + table + ": " + e.getMessage());
            }
        }
        
        DataSourceProvider.closeConnection();
        System.out.println("\n=== FIN TEST ===");
    }
}
