import config.JdbcDriverLoader;
import model.AlertePeremption;
import service.AlertePeremptionService;

import java.util.List;

import config.DataSourceProvider;

public class App {
    public static void main(String[] args) {

        // on charge le driver JDBC une bonne fois pour toute
        new JdbcDriverLoader();

        // le client tape ses id 
        // ..... (ici Yasser met le code connexion client)

        // on se connecte après les id du client 
        DataSourceProvider.initConnection();

        // on enleve l'autocommit : 
        try {
            DataSourceProvider.getConnection().setAutoCommit(false);
        } catch (Exception e) {
            System.err.println("Erreur de setting a false de l'autocommit" + e);
        }

        
        // ici (les gars on doit gerer les eveneemnts user pour interragir avec nos services (transactions))
        try {
            AlertePeremptionService service = new AlertePeremptionService();

            // 1. Générer les alertes (LISTE)
            List<AlertePeremption> alertes = service.genererAlertes();

            System.out.println("Alertes trouvées :");
            for (AlertePeremption a : alertes) {
                System.out.println("Lot " + a.getIdLot()
                    + " (Produit " + a.getIdProduit()
                    + ") périme dans " + a.getJoursRestants() + " jours.");
            }

            // 2. Appliquer la réduction (exemple : sur la première alerte)
            if (!alertes.isEmpty()) {
                service.appliquerReduction(alertes.get(0));
                System.out.println("Réduction appliquée !");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        
        // on ferme la connexion avant de partir :)
        DataSourceProvider.closeConnection();
    }
}
