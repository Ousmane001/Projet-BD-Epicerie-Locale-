package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSourceProvider {
    private static final String URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    private static final String USER = "diakitao";
    private static final String PASS = "diakitao";
    
    // Connexion unique partagée pour toute l'application
    private static Connection connection = null;

    // Crée une nouvelle connexion (pour les cas spéciaux)
    public static Connection getValidConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("[DB] Impossible d'obtenir une connexion");
            e.printStackTrace();
            return null;
        }
    }

    // Retourne la connexion unique (créée au démarrage)
    public static Connection getConnection() {
        if (connection == null) {
            connection = getValidConnection();
        } else {
            // Vérifier si la connexion est toujours valide
            try {
                if (connection.isClosed() || !connection.isValid(2)) {
                    // Connexion invalide, en créer une nouvelle
                    try { connection.close(); } catch (SQLException ignore) {}
                    connection = getValidConnection();
                }
            } catch (SQLException e) {
                // Erreur lors de la vérification, recréer la connexion
                try { connection.close(); } catch (SQLException ignore) {}
                connection = getValidConnection();
            }
        }
        return connection;
    }

    // Initialise la connexion unique au démarrage
    public static void initConnection() {
        if (connection == null) {
            connection = getValidConnection();
        }
    }

    public static void closeConnection(Connection c) {
        if (c != null) {
            try { c.close(); } catch (SQLException ignore) {}
        }
    }
    
    // Ferme la connexion unique de l'application
    public static void closeConnection() {
        if (connection != null) {
            try { 
                connection.close(); 
                connection = null;
            } catch (SQLException ignore) {}
        }
    }
}
