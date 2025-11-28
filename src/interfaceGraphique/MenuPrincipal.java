package interfaceGraphique;

import javax.swing.*;
import java.awt.*;

/**
 * Menu Principal de l'application Épicerie Locale
 * Permet d'accéder à toutes les fonctionnalités:
 * - Consultation du catalogue
 * - Passage de commandes
 * - Alertes de péremption
 * - Clôture de commandes
 */
public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("Épicerie Locale - Menu Principal");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));

        // Panel principal avec bordure
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(240, 245, 250));

        // Titre
        JLabel titre = new JLabel("🛒 Épicerie Locale", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 32));
        titre.setForeground(new Color(60, 130, 180));
        titre.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel sousTitre = new JLabel("Système de Gestion", SwingConstants.CENTER);
        sousTitre.setFont(new Font("Arial", Font.PLAIN, 18));
        sousTitre.setForeground(new Color(100, 100, 100));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerPanel.setBackground(new Color(240, 245, 250));
        headerPanel.add(titre);
        headerPanel.add(sousTitre);

        // Panel central avec les boutons
        JPanel centerPanel = new JPanel(new GridLayout(5, 1, 15, 15));
        centerPanel.setBackground(new Color(240, 245, 250));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Créer les boutons avec icônes et style amélioré
        JButton btnCatalogue = creerBoutonMenu("📖 Consulter le Catalogue", 
            "Voir tous les produits disponibles", new Color(70, 130, 180));
        
        JButton btnCommander = creerBoutonMenu("🛒 Passer une Commande", 
            "Commander des produits", new Color(60, 179, 113));
        
        JButton btnAlertes = creerBoutonMenu("⚠️ Alertes de Péremption", 
            "Voir les produits proches de la péremption", new Color(255, 140, 0));
        
        JButton btnCloture = creerBoutonMenu("✅ Clôturer une Commande", 
            "Finaliser le retrait ou la livraison", new Color(138, 43, 226));
        
        JButton btnQuitter = creerBoutonMenu("❌ Quitter", 
            "Fermer l'application", new Color(220, 80, 60));

        // Actions des boutons
        btnCatalogue.addActionListener(e -> {
            new Catalogue();
            dispose();
        });

        btnCommander.addActionListener(e -> {
            new Login();
            dispose();
        });

        btnAlertes.addActionListener(e -> {
            new AlertesPeremptionWindow();
            dispose();
        });

        btnCloture.addActionListener(e -> {
            new ClotureCommandeWindow();
            dispose();
        });

        btnQuitter.addActionListener(e -> {
            int choix = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment quitter l'application ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (choix == JOptionPane.YES_OPTION) {
                // Fermer la connexion à la base de données
                config.DataSourceProvider.closeConnection();
                System.out.println("\n👋 Au revoir ! Merci d'avoir utilisé l'Épicerie Locale.");
                System.exit(0);
            }
        });

        // Ajouter les boutons au panel central
        centerPanel.add(btnCatalogue);
        centerPanel.add(btnCommander);
        centerPanel.add(btnAlertes);
        centerPanel.add(btnCloture);
        centerPanel.add(btnQuitter);

        // Panel du bas avec informations
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(240, 245, 250));
        JLabel infoLabel = new JLabel("v1.0 - Projet BD Épicerie Locale");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(120, 120, 120));
        footerPanel.add(infoLabel);

        // Assembler le tout
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    /**
     * Crée un bouton de menu stylisé avec texte principal et description
     */
    private JButton creerBoutonMenu(String texte, String description, Color couleur) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(10, 5));
        btn.setBackground(couleur);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true); // IMPORTANT: Force l'affichage de la couleur de fond
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true); // S'assurer que le fond est rempli
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(couleur.darker(), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Texte principal
        JLabel lblTexte = new JLabel(texte);
        lblTexte.setFont(new Font("Arial", Font.BOLD, 18));
        lblTexte.setForeground(Color.WHITE);

        // Description
        JLabel lblDesc = new JLabel(description);
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(255, 255, 255, 200));

        btn.add(lblTexte, BorderLayout.NORTH);
        btn.add(lblDesc, BorderLayout.CENTER);

        // Effet au survol
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(couleur.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(couleur);
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        // Pour tester l'interface seule
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MenuPrincipal();
        });
    }
}
