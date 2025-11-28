import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import config.DataSourceProvider;
import config.JdbcDriverLoader;

public class PopulateDB {
    public static void main(String[] args) {
        System.out.println("=== PEUPLEMENT DE LA BASE ===\n");
        
        // Charger le driver
        try {
            new JdbcDriverLoader();
            System.out.println("✓ Driver chargé");
        } catch (Exception e) {
            System.err.println("✗ Erreur driver: " + e.getMessage());
            return;
        }
        
        // Connexion
        DataSourceProvider.initConnection();
        Connection conn = DataSourceProvider.getConnection();
        
        if (conn == null) {
            System.err.println("✗ Pas de connexion !");
            return;
        }
        System.out.println("✓ Connexion OK\n");
        
        // Lire et exécuter le script
        try {
            conn.setAutoCommit(false); // Mode transaction
            
            BufferedReader reader = new BufferedReader(new FileReader("db_population_sql.sql"));
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            int count = 0;
            
            System.out.println("Lecture du fichier db_population_sql.sql...");
            
            while ((line = reader.readLine()) != null) {
                // Ignorer les commentaires
                if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }
                
                sqlBuilder.append(line).append("\n");
                
                // Si la ligne contient "SELECT * FROM dual;", c'est la fin d'un bloc INSERT ALL
                if (line.trim().equals("SELECT * FROM dual;")) {
                    String sql = sqlBuilder.toString().trim();
                    
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sql);
                        count++;
                        System.out.println("✓ Bloc " + count + " exécuté");
                    } catch (SQLException e) {
                        System.err.println("✗ Erreur bloc " + count + ": " + e.getMessage());
                        // Afficher début du SQL pour debug
                        String preview = sql.length() > 150 ? sql.substring(0, 150) + "..." : sql;
                        System.err.println("  SQL: " + preview);
                    }
                    
                    sqlBuilder = new StringBuilder();
                }
                // Ou si c'est un INSERT simple qui se termine par ;
                else if (line.trim().endsWith(";") && !line.contains("INSERT ALL") && !line.contains("INTO ")) {
                    String sql = sqlBuilder.toString().trim();
                    sql = sql.substring(0, sql.length() - 1); // Enlever le ;
                    
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sql);
                        count++;
                        System.out.println("✓ Requête " + count + " exécutée");
                    } catch (SQLException e) {
                        System.err.println("✗ Erreur: " + e.getMessage());
                    }
                    
                    sqlBuilder = new StringBuilder();
                }
            }
            
            reader.close();
            conn.commit();
            
            System.out.println("\n\n✓ Script exécuté avec succès !");
            
            // Vérification
            String sqlCount = "SELECT COUNT(*) as nb FROM Produit";
            try (PreparedStatement stmt = conn.prepareStatement(sqlCount);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Nombre de produits : " + rs.getInt("nb"));
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
        DataSourceProvider.closeConnection();
        System.out.println("\n=== FIN ===");
    }
}
