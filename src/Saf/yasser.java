·package interfaceGraphique;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.sql.Connection;

import config.DataSourceProvider;

public class Commande extends JFrame {

    private JComboBox<String> typeChoix;
    private JComboBox<String> modefield; // mode Boutique / Livraison
    private JTextField idProduitField, typeCondField, quantiteField;
    private JTextField refContenantField;
    private JButton validerButton;

    public Commande() {
        setTitle("Nouvelle Commande");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ---------------------------
        // CHOIX DU TYPE DE COMMANDE
        // ---------------------------
        JPanel topPanel = new JPanel(new GridLayout(2,1));
        topPanel.add(new JLabel("Choisissez le type de commande :"));

        typeChoix = new JComboBox<>(new String[]{"Produit", "Contenant"});
        topPanel.add(typeChoix);
        add(topPanel, BorderLayout.NORTH);

        // ---------------------------
        // PANNEAU CENTRAL DYNAMIQUE
        // ---------------------------
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        add(centerPanel, BorderLayout.CENTER);

        // Champs PRODUIT
        idProduitField = new JTextField();
        typeCondField = new JTextField();
        quantiteField = new JTextField();

        // Champs CONTENANT
        refContenantField = new JTextField();

        // Champs MODE
        modefield = new JComboBox<>(new String[]{"Boutique", "Livraison"});

        // Gestion dynamique affichage
        typeChoix.addActionListener(e -> {
            centerPanel.removeAll();

            if (typeChoix.getSelectedItem().equals("Produit")) {
                centerPanel.add(new JLabel("ID Produit :"));
                centerPanel.add(idProduitField);

                centerPanel.add(new JLabel("Type conditionnement :"));
                centerPanel.add(typeCondField);

                centerPanel.add(new JLabel("Quantité :"));
                centerPanel.add(quantiteField);

            } else {
                centerPanel.add(new JLabel("Référence Contenant :"));
                centerPanel.add(refContenantField);

                centerPanel.add(new JLabel("Quantité :"));
                centerPanel.add(quantiteField);
            }

            // Ajout du modefield
            centerPanel.add(new JLabel("Mode de récupération :"));
            centerPanel.add(modefield);

            // Listener pour ouvrir la fenêtre Livraison si besoin
            modefield.addActionListener(ev -> {
                String mode = (String) modefield.getSelectedItem();
                if ("Livraison".equals(mode)) {
                    LivraisonWindow livraisonWindow = new LivraisonWindow();
                    livraisonWindow.setVisible(true);
                }
            });

            centerPanel.revalidate();
            centerPanel.repaint();
        });

        typeChoix.setSelectedIndex(0); // pour déclencher l'affichage initial

        // ---------------------------
        // VALIDATION
        // ---------------------------
        validerButton = new JButton("Valider la commande");
        validerButton.addActionListener(e -> traiterCommande());
        add(validerButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Choisit automatiquement le backend correct :
     * - CommandeProduit
     * - CommandeContenant
     */
    private void traiterCommande() {

        try {
            Connection conn = DataSourceProvider.getConnection();

            String type = (String) typeChoix.getSelectedItem();
            int quantite = Integer.parseInt(quantiteField.getText().trim());

            if (type.equals("Produit")) {

                String idProduit = idProduitField.getText().trim();
                String cond = typeCondField.getText().trim();

                // Backend PRODUIT
                CommandeProduit cp = new CommandeProduit(
                        idProduit,
                        cond,
                        quantite,
                        LocalDate.now()
                );

                cp.executeTransaction();

                JOptionPane.showMessageDialog(this,
                        "Commande PRODUIT enregistrée !");

            } else {

                String ref = refContenantField.getText().trim();

                // Backend CONTENANT
                CommandeContenant cc = new CommandeContenant(
                        "CMD-CLIENT", // remplacer par ton ID commande réel
                        ref,
                        quantite
                );

                // Les valeurs prixUnitaire, sousTotal viennent de DAO
                // Exemple d’enregistrement :
                // cc.saveToDatabase(conn);

                JOptionPane.showMessageDialog(this,
                        "Commande CONTENANT enregistrée !");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur : " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
