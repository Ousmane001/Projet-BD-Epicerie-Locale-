import config.JdbcDriverLoader;
import config.DataSourceProvider;
import interfaceGraphique.MenuPrincipal;

import javax.swing.*;

/**
 * Point d'entrée de l'application Épicerie Locale
 * Initialise la connexion à la base de données et lance l'interface graphique
 */
public class App {
    public static void main(String[] args) {

        try {
            // 1. Charger le driver JDBC
            System.out.println(" Chargement du driver JDBC...");
            new JdbcDriverLoader();
            System.out.println(" Driver JDBC chargé avec succès\n");

            // 2. Initialiser la connexion à la base de données
            System.out.println(" Connexion à la base de données Oracle...");
            DataSourceProvider.initConnection();
            
            if (DataSourceProvider.getConnection() == null) {
                throw new Exception("La connexion à la base de données a échoué");
            }
            System.out.println(" Connexion établie avec succès\n");

            // 3. Désactiver l'autocommit pour gérer les transactions manuellement
            DataSourceProvider.getConnection().setAutoCommit(false);
            System.out.println(" Mode transaction activé (autocommit=false)\n");

            System.out.println(" Lancement de l'interface graphique...\n");
            
            // 4. Lancer l'interface graphique dans le thread EDT (Event Dispatch Thread)
            SwingUtilities.invokeLater(() -> {
                try {
                    // Définir le Look and Feel du système pour une meilleure intégration
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    System.err.println(" Impossible de définir le Look and Feel système");
                }

                // Choix du rôle au démarrage : Client ou Gestion
                String[] options = {"Client", "Gestion"};
                int choix = JOptionPane.showOptionDialog(
                        null,
                        "Vous êtes :",
                        "Choix du rôle",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (choix == 0) { // Client
                    new interfaceGraphique.Login();
                } else { // Gestion ou annulation -> ouvrir menu principal de gestion
                    new MenuPrincipal();
                }
            });

        } catch (Exception e) {
            System.err.println("\n ERREUR CRITIQUE lors de l'initialisation:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
            
            // Afficher un message d'erreur graphique si possible
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                    "Erreur lors de l'initialisation de l'application:\n" + e.getMessage() +
                    "\n\nVérifiez votre connexion à la base de données.",
                    "Erreur d'initialisation",
                    JOptionPane.ERROR_MESSAGE);
            });
            
            System.exit(1);
        }
    }
}
