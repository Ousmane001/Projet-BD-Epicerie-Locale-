package interfaceGraphique;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

import service.ConsulterCatalogue;
import model.ProduitCatalogue;

public class Catalogue extends JFrame {

    private ConsulterCatalogue catalogueService;
    private DefaultTableModel model;
    private JTable table;
    private JComboBox<String> comboCategories;
    private JTextField txtRecherche;

    public Catalogue() {
        // Initialisation du service
        catalogueService = new ConsulterCatalogue();

        setTitle("Catalogue des Produits - Épicerie Locale");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));

        // ----------- PANEL DE RECHERCHE ET FILTRES ------------
        JPanel panelFiltres = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltres.setBackground(new Color(240, 245, 250));
        panelFiltres.setBorder(BorderFactory.createTitledBorder("Filtres et Recherche"));

        // Filtre par catégorie
        JLabel lblCategorie = new JLabel("Catégorie:");
        comboCategories = new JComboBox<>();
        comboCategories.addItem("Toutes");
        
        // Charger les catégories
        List<String> categories = catalogueService.getCategories();
        for (String cat : categories) {
            comboCategories.addItem(cat);
        }
        
        comboCategories.addActionListener(e -> filtrerParCategorie());

        // Recherche par nom
        JLabel lblRecherche = new JLabel("Rechercher:");
        txtRecherche = new JTextField(20);
        JButton btnRechercher = new JButton("Rechercher");
        btnRechercher.setBackground(new Color(70, 130, 180));
        btnRechercher.setForeground(Color.WHITE);
        btnRechercher.addActionListener(e -> rechercherProduits());

        JButton btnReinitialiser = new JButton("Réinitialiser");
        btnReinitialiser.addActionListener(e -> reinitialiserAffichage());

        panelFiltres.add(lblCategorie);
        panelFiltres.add(comboCategories);
        panelFiltres.add(Box.createHorizontalStrut(20));
        panelFiltres.add(lblRecherche);
        panelFiltres.add(txtRecherche);
        panelFiltres.add(btnRechercher);
        panelFiltres.add(btnReinitialiser);

        // ----------- TABLE ------------
        model = new DefaultTableModel(
                new String[]{"Nom du produit", "Catégorie", "Prix (€)", "Type", "Poids/Unité", "Bio", "Origine", "Statut"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table non éditable
            }
        };
        
        table = new JTable(model);

        // Charger les produits via le service
        chargerTousLesProduits();

        // ----------- STYLES TABLE ------------
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 13));

        // Centrer certaines colonnes
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Prix
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Type
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Bio
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Statut

        // Ajuster la largeur des colonnes
        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Nom
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Catégorie
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Prix
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Type
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Poids
        table.getColumnModel().getColumn(5).setPreferredWidth(60);  // Bio
        table.getColumnModel().getColumn(6).setPreferredWidth(120); // Origine
        table.getColumnModel().getColumn(7).setPreferredWidth(100); // Statut

        // Double-clic pour voir les détails
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    afficherDetailsProduit();
                }
            }
        });

        // ----------- BOUTONS ------------
        JButton btnDetails = new JButton("Voir Détails");
        JButton btnRetour = new JButton("Retour");
        JButton btnQuitter = new JButton("Quitter");

        btnDetails.setBackground(new Color(34, 139, 34));
        btnDetails.setForeground(Color.WHITE);
        btnDetails.addActionListener(e -> afficherDetailsProduit());

        btnRetour.setBackground(new Color(70, 130, 180));
        btnRetour.setForeground(Color.WHITE);
        btnRetour.addActionListener(e -> {
            new BoutiqueWindow();
            dispose();
        });

        btnQuitter.setBackground(new Color(220, 80, 60));
        btnQuitter.setForeground(Color.WHITE);
        btnQuitter.addActionListener(e -> System.exit(0));

        // ----------- PANELS ------------
        JPanel panelTable = new JPanel(new BorderLayout());
        panelTable.setBorder(BorderFactory.createTitledBorder("Produits Disponibles"));
        panelTable.add(new JScrollPane(table));

        JPanel panelBoutons = new JPanel();
        panelBoutons.add(btnDetails);
        panelBoutons.add(btnRetour);
        panelBoutons.add(btnQuitter);

        // ----------- LAYOUT PRINCIPAL ------------
        setLayout(new BorderLayout(10, 10));
        add(panelFiltres, BorderLayout.NORTH);
        add(panelTable, BorderLayout.CENTER);
        add(panelBoutons, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Charge tous les produits du catalogue
     */
    private void chargerTousLesProduits() {
        model.setRowCount(0); // Vider la table
        
        List<ProduitCatalogue> produits = catalogueService.afficherCatalogue();
        
        for (ProduitCatalogue p : produits) {
            model.addRow(new Object[]{
                p.getNomProduit(),
                p.getCategorie(),
                String.format("%.2f", p.getPrixVenteClient()),
                p.getTypeConditionnement(),
                p.getPoidsSachet() != null ? String.format("%.2f kg", p.getPoidsSachet()) : "N/A",
                p.getBio() != null ? p.getBio() : "Non",
                p.getOrigineGeographique() != null ? p.getOrigineGeographique() : "N/A",
                p.getStatutProduit() != null ? p.getStatutProduit() : "Disponible"
            });
        }
    }

    /**
     * Filtre les produits par catégorie sélectionnée
     */
    private void filtrerParCategorie() {
        String categorie = (String) comboCategories.getSelectedItem();
        model.setRowCount(0);
        
        List<ProduitCatalogue> produits;
        
        if ("Toutes".equals(categorie)) {
            produits = catalogueService.afficherCatalogue();
        } else {
            produits = catalogueService.filtrerParCategorie(categorie);
        }
        
        for (ProduitCatalogue p : produits) {
            model.addRow(new Object[]{
                p.getNomProduit(),
                p.getCategorie(),
                String.format("%.2f", p.getPrixVenteClient()),
                p.getTypeConditionnement(),
                p.getPoidsSachet() != null ? String.format("%.2f kg", p.getPoidsSachet()) : "N/A",
                p.getBio() != null ? p.getBio() : "Non",
                p.getOrigineGeographique() != null ? p.getOrigineGeographique() : "N/A",
                p.getStatutProduit() != null ? p.getStatutProduit() : "Disponible"
            });
        }
    }

    /**
     * Recherche des produits par mot-clé
     */
    private void rechercherProduits() {
        String motCle = txtRecherche.getText().trim();
        
        if (motCle.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez saisir un mot-clé de recherche", 
                "Recherche", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        model.setRowCount(0);
        List<ProduitCatalogue> produits = catalogueService.rechercherProduits(motCle);
        
        if (produits.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun produit trouvé pour: " + motCle, 
                "Résultat de recherche", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        for (ProduitCatalogue p : produits) {
            model.addRow(new Object[]{
                p.getNomProduit(),
                p.getCategorie(),
                String.format("%.2f", p.getPrixVenteClient()),
                p.getTypeConditionnement(),
                p.getPoidsSachet() != null ? String.format("%.2f kg", p.getPoidsSachet()) : "N/A",
                p.getBio() != null ? p.getBio() : "Non",
                p.getOrigineGeographique() != null ? p.getOrigineGeographique() : "N/A",
                p.getStatutProduit() != null ? p.getStatutProduit() : "Disponible"
            });
        }
    }

    /**
     * Réinitialise l'affichage
     */
    private void reinitialiserAffichage() {
        comboCategories.setSelectedIndex(0);
        txtRecherche.setText("");
        chargerTousLesProduits();
    }

    /**
     * Affiche les détails du produit sélectionné
     */
    private void afficherDetailsProduit() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un produit dans la liste", 
                "Sélection requise", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String nomProduit = (String) model.getValueAt(selectedRow, 0);
        String categorie = (String) model.getValueAt(selectedRow, 1);
        String prix = (String) model.getValueAt(selectedRow, 2);
        String type = (String) model.getValueAt(selectedRow, 3);
        String poids = (String) model.getValueAt(selectedRow, 4);
        String bio = (String) model.getValueAt(selectedRow, 5);
        String origine = (String) model.getValueAt(selectedRow, 6);
        String statut = (String) model.getValueAt(selectedRow, 7);
        
        String details = String.format(
            "<html><body style='width: 300px; padding: 10px;'>" +
            "<h2 style='color: #4682B4;'>%s</h2>" +
            "<p><b>Catégorie:</b> %s</p>" +
            "<p><b>Prix:</b> %s €</p>" +
            "<p><b>Type:</b> %s</p>" +
            "<p><b>Poids/Unité:</b> %s</p>" +
            "<p><b>Bio:</b> %s</p>" +
            "<p><b>Origine:</b> %s</p>" +
            "<p><b>Statut:</b> <span style='color: green;'><b>%s</b></span></p>" +
            "</body></html>",
            nomProduit, categorie, prix, type, poids, bio, origine, statut
        );
        
        JOptionPane.showMessageDialog(this, 
            details, 
            "Détails du Produit", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Initialisation de la connexion à la base de données
        try {
            new config.JdbcDriverLoader();
            config.DataSourceProvider.initConnection();
            
            if (config.DataSourceProvider.getConnection() == null) {
                javax.swing.JOptionPane.showMessageDialog(null,
                    "Erreur: Impossible de se connecter à la base de données",
                    "Erreur de connexion",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            
            SwingUtilities.invokeLater(() -> new Catalogue());
            
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                "Erreur lors de l'initialisation: " + e.getMessage(),
                "Erreur",
                javax.swing.JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
