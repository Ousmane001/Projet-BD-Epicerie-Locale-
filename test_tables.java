import java.sql.*;
import config.DataSourceProvider;
import config.JdbcDriverLoader;

public class TestTables {
    public static void main(String[] args) {
        try {
            new JdbcDriverLoader();
            DataSourceProvider.initConnection();
            Connection conn = DataSourceProvider.getConnection();
            
            String[] tables = {"TypeActivite", "Contact", "Adresse", "Client", "Producteur", "Produit"};
            for (String table : tables) {
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
                    if (rs.next()) {
                        System.out.println(table + " existe : " + rs.getInt(1) + " lignes");
                    }
                } catch (SQLException e) {
                    System.out.println(table + " : ERREUR - " + e.getMessage());
                }
            }
            
            DataSourceProvider.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
