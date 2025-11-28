import java.io.*;
import java.sql.*;
import config.DataSourceProvider;
import config.JdbcDriverLoader;

public class InsertProduits {
    public static void main(String[] args) {
        System.out.println("=== INSERTION DES PRODUITS ===\n");
        
        try {
            new JdbcDriverLoader();
            DataSourceProvider.initConnection();
            Connection conn = DataSourceProvider.getConnection();
            
            if (conn == null) {
                System.err.println("✗ Pas de connexion !");
                return;
            }
            
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            
            BufferedReader reader = new BufferedReader(new FileReader("insert_produits_simple.sql"));
            String line;
            int count = 0;
            int errors = 0;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Ignorer commentaires et lignes vides
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                
                // Ignorer COMMIT
                if (line.equalsIgnoreCase("COMMIT;")) {
                    continue;
                }
                
                // Enlever le ; final
                if (line.endsWith(";")) {
                    line = line.substring(0, line.length() - 1);
                }
                
                try {
                    stmt.execute(line);
                    count++;
                    if (count % 10 == 0) {
                        System.out.println("✓ " + count + " insertions réussies...");
                    }
                } catch (SQLException e) {
                    errors++;
                    if (e.getMessage().contains("unique constraint")) {
                        // Ignorer les doublons (données déjà présentes)
                        System.out.println("  → Donnée déjà présente (ignorée)");
                    } else {
                        System.err.println("✗ Erreur: " + e.getMessage());
                        System.err.println("  SQL: " + line.substring(0, Math.min(80, line.length())));
                    }
                }
            }
            
            reader.close();
            conn.commit();
            stmt.close();
            
            System.out.println("\n=== RÉSULTATS ===");
            System.out.println("✓ Insertions réussies : " + count);
            System.out.println("✗ Erreurs : " + errors);
            
            // Vérification finale
            System.out.println("\n=== VÉRIFICATION ===");
            String[] tables = {"Produit", "Conditionnement", "ConditionnementVrac", "ConditionnementPreconditionne"};
            for (String table : tables) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
                if (rs.next()) {
                    System.out.println(table + " : " + rs.getInt(1) + " lignes");
                }
                rs.close();
            }
            
            DataSourceProvider.closeConnection();
            
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
