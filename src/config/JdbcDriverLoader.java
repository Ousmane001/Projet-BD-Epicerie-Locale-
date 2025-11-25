package config;

import java.sql.DriverManager;
import java.sql.SQLException;
import oracle.jdbc.driver.OracleDriver;

public class JdbcDriverLoader{

    // Constructeur vide !!
    public JdbcDriverLoader() {

        // Enregistrement du pilote JDBC 
        try {
            // Enregistre le driver Oracle auprès de DriverManager
            //DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            System.out.println(">> Driver JDBC Oracle chargé avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement du driver Oracle :");
            e.printStackTrace();
        }

    }

    
}
