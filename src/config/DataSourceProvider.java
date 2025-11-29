package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSourceProvider {
    private static final String URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    private static final String USER = "diakitao";
    private static final String PASS = "diakitao";

    // Chaque appel retourne une nouvelle connexion (plus sûr que partage statique dans notre contexte Swing)
    public static Connection getValidConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("[DB] Impossible d'obtenir une connexion");
            e.printStackTrace();
            return null;
        }
    }

    public static Connection getConnection() { return getValidConnection(); }

    public static void initConnection() { /* plus nécessaire avec connexions à la demande */ }

    public static void closeConnection(Connection c) {
        if (c != null) {
            try { c.close(); } catch (SQLException ignore) {}
        }
    }
}
