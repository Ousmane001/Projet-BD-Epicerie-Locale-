package interfaceGraphique;
import javax.swing.*;
import java.awt.*;

public class Commande extends JFrame {
    private JComboBox<String> comboProduit;
    private JTextField txtPrix, txtQuantite;
    private JComboBox<String> comboType;
    private JTextField txtAdresse;

    public Commande() {
        setTitle("Commande");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        getContentPane().setBackground(new Color(240, 245, 250));

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 245, 250));

        // Panel du formulaire
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "Nouvelle Commande"
            ),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setBackground(Color.WHITE);

        // Labels
        JLabel lblProduit = new JLabel("Produit :");
        JLabel lblPrix = new JLabel("Prix unitaire :");
        JLabel lblQuantite = new JLabel("Quantité :");
        JLabel lblType = new JLabel("Type de livraison :");
        JLabel lblAdresse = new JLabel("Adresse :");

        Font labelFont = new Font("Arial", Font.BOLD, 12);
        lblProduit.setFont(labelFont);
        lblPrix.setFont(labelFont);
        lblQuantite.setFont(labelFont);
        lblType.setFont(labelFont);
        lblAdresse.setFont(labelFont);



        // à supp- ------------------------------------------------------------------------------------------------------------
        // Liste des produits
        String[][] produits = {
            {"Tomates", "2.50"},
            {"Pommes", "3.00"},
            {"Lait", "1.20"},
            {"Pain", "1.50"},
            {"Fromage", "4.50"},
            {"Jus d'orange", "3.20"},
            {"Yaourts", "2.80"},
            {"Pâtes", "1.80"}
        };

        comboProduit = new JComboBox<>();
        for (String[] produit : produits) {
            comboProduit.addItem(produit[0]);
        }

        txtPrix = new JTextField();
        txtPrix.setEditable(false);
        txtPrix.setText(produits[0][1] + " eur");

        txtQuantite = new JTextField("1");

        String[] typesLivraison = {"Retrait en boutique", "Livraison à domicile"};
        comboType = new JComboBox<>(typesLivraison);
        txtAdresse = new JTextField();

        styleComboBox(comboProduit);
        styleTextField(txtPrix);
        styleTextField(txtQuantite);
        styleTextField(txtAdresse);

        comboType.setFont(new Font("Arial", Font.PLAIN, 12));

        // Mise à jour du prix produit
        comboProduit.addActionListener(e -> {
            int selectedIndex = comboProduit.getSelectedIndex();
            if (selectedIndex >= 0) {
                txtPrix.setText(produits[selectedIndex][1] + " €");
            }
        });

        // AJOUT IMPORTANT : si livraison à domicile → ouvrir directement LivraisonWindow
        comboType.addActionListener(e -> {
            boolean isLivraison = comboType.getSelectedItem().equals("Livraison à domicile");

            lblAdresse.setVisible(isLivraison);
            txtAdresse.setVisible(isLivraison);

            if (isLivraison) {
                new LivraisonWindow();  // 👉 OUVERTURE DIRECTE
                dispose();
            }
        });

        // Initialisation visibilité adresse
        lblAdresse.setVisible(false);
        txtAdresse.setVisible(false);

        // Ajout au formulaire
        formPanel.add(lblProduit);
        formPanel.add(comboProduit);
        formPanel.add(lblPrix);
        formPanel.add(txtPrix);
        formPanel.add(lblQuantite);
        formPanel.add(txtQuantite);
        formPanel.add(lblType);
        formPanel.add(comboType);
        formPanel.add(lblAdresse);
        formPanel.add(txtAdresse);

        formPanel.add(new JLabel());
        formPanel.add(new JLabel());

        // Boutons
        JButton btnConfirmer = new JButton("Confirmer la Commande");
        JButton btnRetour = new JButton("Retour");
        JButton btnQuitter = new JButton("Quitter");

        // Styling des boutons pour lisibilité
        styleButton(btnConfirmer, new Color(70, 130, 180), Color.WHITE);
        styleButton(btnRetour, new Color(100, 149, 237), Color.WHITE);
        styleButton(btnQuitter, new Color(220, 80, 60), Color.WHITE);

        btnConfirmer.addActionListener(e -> confirmerCommande(produits));
        btnRetour.addActionListener(e -> {
            new BoutiqueWindow();
            dispose();
        });
        btnQuitter.addActionListener(e -> System.exit(0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnConfirmer);

        JPanel navPanel = new JPanel();
        navPanel.add(btnRetour);
        navPanel.add(btnQuitter);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(navPanel, BorderLayout.NORTH);

        add(mainPanel);
        setVisible(true);
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void confirmerCommande(String[][] produits) {
        try {
            String produit = (String) comboProduit.getSelectedItem();
            int selectedIndex = comboProduit.getSelectedIndex();
            double prix = Double.parseDouble(produits[selectedIndex][1]);
            int quantite = Integer.parseInt(txtQuantite.getText());
            String typeLivraison = (String) comboType.getSelectedItem();

            if (typeLivraison.equals("Livraison à domicile")) {
                new LivraisonWindow();
                dispose();
                return;
            }

            double total = prix * quantite;

            String message = "Commande confirmée !\n\n" +
                             "Produit: " + produit + "\n" +
                             "Prix unitaire: " + prix + " €\n" +
                             "Quantité: " + quantite + "\n" +
                             "Total: " + String.format("%.2f", total) + " €\n" +
                             "Type: " + typeLivraison;

            JOptionPane.showMessageDialog(
                this, message, "Confirmation", JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this, "Erreur : quantité invalide", "Erreur", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Applique un style uniforme aux boutons pour meilleure lisibilité
     */
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 2),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        new Commande();
    }
}
