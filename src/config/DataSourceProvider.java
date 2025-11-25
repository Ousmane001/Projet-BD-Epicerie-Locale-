package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSourceProvider {
    private static Connection connection;  // on met static car la connexion a notre BD est censé etre la meme pour n'importe quelle instance de DatasourceProvider

    // methode nous permettant d'initialiser la connexion au démarrage de l'app
    public static void initConnection() {
        if (connection == null) {
            try {
                // Vous devez avoir fait new JdbcDriverLoader() AVANT
                connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@oracle1.ensimag.fr:",  
                    "mengossn",        // si on a le temps, on mettra les id dans un fichier .env pour rendre faciliter nos tests     
                    "mengossn"                             
                );
                System.out.println(">> Connexion à Oracle établie !");
            } catch (SQLException e) {
                System.err.println("Erreur de connexion :");
                e.printStackTrace();
            }
        }
    }

    // Fournit l'unique connexion pour toute l'application de notre epicerie
    public static Connection getConnection() {
        return connection;
    }

    // Fermeture de notre connexion quand le client se deconnecte !
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println(">> Connexion Oracle fermée.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
