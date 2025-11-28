package interfaceGraphique;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;


public class LivraisonWindow extends JFrame {

    public LivraisonWindow() {
        setTitle("Livraison");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 245, 250));

        // FORMULAIRE
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "Formulaire de Livraison"
            ),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setBackground(Color.WHITE);

        // LABELS
        JLabel lblProduit = new JLabel("Produit :");
        JLabel lblPrix = new JLabel("Prix :");
        JLabel lblAdresse = new JLabel("Adresse de livraison :");
        JLabel lblDistance = new JLabel("Distance (km) :");
        JLabel lblCout = new JLabel("Coût total :");

        Font f = new Font("Arial", Font.BOLD, 12);
        lblProduit.setFont(f);
        lblPrix.setFont(f);
        lblAdresse.setFont(f);
        lblDistance.setFont(f);
        lblCout.setFont(f);

        // CHAMPS
        JTextField txtProduit = new JTextField();
        txtProduit.setBorder(style());

        JTextField txtPrix = new JTextField();
        txtPrix.setBorder(style());

        JTextField txtAdresse = new JTextField();
        txtAdresse.setBorder(style());

        JTextField txtDistance = new JTextField();
        txtDistance.setBorder(style());

        JTextField txtCout = new JTextField();
        txtCout.setEditable(false);
        txtCout.setBorder(style());

        // AJOUT AU FORMULAIRE
        formPanel.add(lblProduit);
        formPanel.add(txtProduit);

        formPanel.add(lblPrix);
        formPanel.add(txtPrix);

        formPanel.add(lblAdresse);
        formPanel.add(txtAdresse);

        formPanel.add(lblDistance);
        formPanel.add(txtDistance);

        formPanel.add(lblCout);
        formPanel.add(txtCout);

        formPanel.add(new JLabel());
        formPanel.add(new JLabel());

        // CALCUL AUTO : à chaque changement de distance
        txtDistance.addCaretListener(e -> {
            try {
                double prix = Double.parseDouble(txtPrix.getText());
                double distance = Double.parseDouble(txtDistance.getText());
                double cout = prix + distance * 0.50;

                txtCout.setText(String.format("%.2f €", cout));
            } catch (Exception ex) {
                txtCout.setText("");
            }
        });

        // BOUTONS
        JButton btnConfirmer = new JButton("Confirmer la Commande");
        JButton btnRetour = new JButton("Retour");
        JButton btnQuitter = new JButton("Quitter");

        designButton(btnConfirmer, new Color(60, 179, 113));
        designButton(btnRetour, new Color(70, 130, 180));
        designButton(btnQuitter, new Color(220, 80, 60));

        // ACTION CONFIRMATION
        btnConfirmer.addActionListener(e -> {
            if (txtProduit.getText().trim().isEmpty() ||
                txtPrix.getText().trim().isEmpty() ||
                txtAdresse.getText().trim().isEmpty() ||
                txtDistance.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                    this,
                    "Veuillez remplir tous les champs",
                    "Champs manquants",
                    JOptionPane.WARNING_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Commande livrée !\n\n" +
                    "Produit : " + txtProduit.getText() + "\n" +
                    "Prix du produit : " + txtPrix.getText() + "\n" +
                    "Distance : " + txtDistance.getText() + " km\n" +
                    "Coût total : " + txtCout.getText() + "\n" +
                    "Adresse : " + txtAdresse.getText(),
                    "Confirmation de Commande",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        btnRetour.addActionListener(e -> {
            new BoutiqueWindow();
            dispose();
        });

        btnQuitter.addActionListener(e -> System.exit(0));

        // PANELS BOUTONS
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(240, 245, 250));
        buttonPanel.add(btnConfirmer);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        navPanel.setBackground(new Color(240, 245, 250));
        navPanel.add(btnRetour);
        navPanel.add(btnQuitter);

        // ASSEMBLAGE
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(navPanel, BorderLayout.NORTH);

        add(mainPanel);
        setVisible(true);
    }

    // STYLE INPUT
    private Border style() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        );
    }

    // STYLE BOUTON - amélioré pour meilleure lisibilité
    private void designButton(JButton btn, Color c) {
        btn.setBackground(c);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(c.darker(), 2),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        new LivraisonWindow();
    }
}
