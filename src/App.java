import config.JdbcDriverLoader;
import config.DataSourceProvider;

public class App {
    public static void main(String[] args) {

        // on charge le driver JDBC une bonne fois pour toute
        new JdbcDriverLoader();

        // le client tape ses id 
        // ..... (ici Yasser met le code connexion client)

        // on se connecte après les id du client 
        DataSourceProvider.initConnection();

        // ici (les gars on doit gerer les eveneemnts user pour interragir avec nos services (transactions))

        
        // on ferme la connexion avant de partir :)
        DataSourceProvider.closeConnection();
    }
}
