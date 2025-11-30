package interfaceGraphique;

import dao.ContenantDAO;
import dao.ProduitDAO;
import model.CommandeItem;
import dao.AdresseDAO;
import dao.ConditionnementDAO;
import model.Adresse;
import model.Contenant;
import model.ContenantItem;
import model.ProduitDisponible;
import model.Session;
import service.CommandeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class CommandeClient extends JFrame {
    private JComboBox<ProduitDisponible> comboProduit;
    private JComboBox<Contenant> comboContenant;
    private JSpinner spinnerQuantiteContenant;
    private JComboBox<String> comboModePaiement, comboModeRecuperation;
    private List<ProduitDisponible> produitsDisponibles;
    private List<Contenant> contenantsDisponibles;
    private DefaultTableModel modelePanier;
    private JTable tablePanier;
    private JLabel lblTotal;
    private List<CommandeItem> panier;
    private List<ContenantItem> panierContenants;

    public CommandeClient() {
        setTitle("Passer une commande");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));

        panier = new ArrayList<>();
        panierContenants = new ArrayList<>();
        
        // Charger les produits disponibles depuis la BD
        ProduitDAO produitDAO = new ProduitDAO();
        produitsDisponibles = produitDAO.getProduitsDisponibles();
        
        // Charger les contenants disponibles
        ContenantDAO contenantDAO = new ContenantDAO();
        contenantsDisponibles = contenantDAO.getTousLesContenants();

        if (produitsDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun produit disponible actuellement.", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
        }

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 245, 250));

        // Panel de sélection de produit (haut)
        JPanel selectionPanel = createSelectionPanel();
        mainPanel.add(selectionPanel, BorderLayout.NORTH);

        // Panel du panier (centre)
        JPanel panierPanel = createPanierPanel();
        mainPanel.add(panierPanel, BorderLayout.CENTER);

        // Panel des boutons (bas)
        JPanel actionPanel = createActionPanel();
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Sélection du produit et contenants"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Produit
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Produit :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        comboProduit = new JComboBox<>();
        for (ProduitDisponible p : produitsDisponibles) {
            comboProduit.addItem(p);
        }
        comboProduit.addActionListener(e -> updatePrixAffiche());
        panel.add(comboProduit, gbc);

        // Bouton Ajouter produit au panier
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JButton btnAjouter = new JButton("Ajouter au panier");
        styleButton(btnAjouter, new Color(34, 139, 34), Color.WHITE);
        btnAjouter.addActionListener(e -> {
            try {
                ajouterAuPanier();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout du produit au panier : " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        panel.add(btnAjouter, gbc);
        
        // Séparateur
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JSeparator separator = new JSeparator();
        panel.add(separator, gbc);
        
        // Contenant
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.weightx = 0;
        panel.add(new JLabel("Contenant :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        comboContenant = new JComboBox<>();
        for (Contenant c : contenantsDisponibles) {
            comboContenant.addItem(c);
        }
        panel.add(comboContenant, gbc);
        
        // Quantité contenant
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("Quantité :"), gbc);
        gbc.gridx = 1;
        spinnerQuantiteContenant = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        panel.add(spinnerQuantiteContenant, gbc);
        
        // Bouton Ajouter contenant
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JButton btnAjouterContenant = new JButton("Ajouter contenant au panier");
        styleButton(btnAjouterContenant, new Color(70, 130, 180), Color.WHITE);
        btnAjouterContenant.addActionListener(e -> ajouterContenantAuPanier());
        panel.add(btnAjouterContenant, gbc);

        return panel;
    }

    private JPanel createPanierPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Votre panier"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(Color.WHITE);

        // Table du panier
        String[] colonnes = {"Produit", "Type", "Prix unitaire", "Quantité", "Sous-total"};
        modelePanier = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePanier = new JTable(modelePanier);
        JScrollPane scrollPane = new JScrollPane(tablePanier);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel total + bouton vider
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        
        lblTotal = new JLabel("Total : 0.00 EUR");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(lblTotal, BorderLayout.WEST);

        JButton btnVider = new JButton("Vider le panier");
        styleButton(btnVider, new Color(220, 80, 60), Color.WHITE);
        btnVider.addActionListener(e -> viderPanier());
        bottomPanel.add(btnVider, BorderLayout.EAST);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 250));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mode de paiement
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mode de paiement :"), gbc);
        gbc.gridx = 1;
        comboModePaiement = new JComboBox<>(new String[]{"En ligne", "En Boutique"});
        panel.add(comboModePaiement, gbc);

        // Mode de récupération
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mode de récupération :"), gbc);
        gbc.gridx = 1;
        comboModeRecuperation = new JComboBox<>(new String[]{"Boutique", "Domicile"});
        panel.add(comboModeRecuperation, gbc);

        // Boutons
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(new Color(240, 245, 250));

        JButton btnValider = new JButton("Valider la commande");
        styleButton(btnValider, new Color(70, 130, 180), Color.WHITE);
        btnValider.addActionListener(e -> validerCommande());
        btnPanel.add(btnValider);

        JButton btnRetour = new JButton("Retour");
        styleButton(btnRetour, new Color(100, 149, 237), Color.WHITE);
        btnRetour.addActionListener(e -> {
            new MenuPrincipal();
            dispose();
        });
        btnPanel.add(btnRetour);

        JButton btnQuitter = new JButton("Quitter");
        styleButton(btnQuitter, new Color(220, 80, 60), Color.WHITE);
        btnQuitter.addActionListener(e -> System.exit(0));
        btnPanel.add(btnQuitter);

        panel.add(btnPanel, gbc);

        return panel;
    }

    private void updatePrixAffiche() {
        // Optionnel: afficher info produit sélectionné
    }

    private void ajouterAuPanier() throws SQLException{
        ProduitDisponible produit = (ProduitDisponible) comboProduit.getSelectedItem();
        if (produit == null) return;

        // Demander le type de conditionnement
        String[] options = {"Préconditionné (sachets)", "Vrac (kg/L)"};
        int choix = JOptionPane.showOptionDialog(this,
            "Quel type de conditionnement souhaitez-vous ?",
            "Type de conditionnement",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choix == -1) return; // Annulé
        
        String typeConditionnement;
        int quantite = 0;
        
        // if (choix == 0) {
        //     // Préconditionné
        //     typeConditionnement = "Preconditionne";
        //     String qteStr = JOptionPane.showInputDialog(this,
        //         "Quantité (nombre de sachets) :",
        //         "Quantité",
        //         JOptionPane.QUESTION_MESSAGE);
        //     if (qteStr == null || qteStr.trim().isEmpty()) return;
        //     try {
        //         quantite = Integer.parseInt(qteStr.trim());
        //         if (quantite <= 0) {
        //             JOptionPane.showMessageDialog(this, "Quantité invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        //             return;
        //         }
        //     } catch (NumberFormatException e) {
        //         JOptionPane.showMessageDialog(this, "Quantité invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
        //         return;
        //     }
        if (choix == 0) {
            typeConditionnement = "Preconditionne";
            // 2️⃣ Récupérer les poids disponibles depuis la DB
            ProduitDAO produitDAO = new ProduitDAO();
            ConditionnementDAO conditionnementDAO = new ConditionnementDAO();
            String idConditionnement = produitDAO.getIdConditionnement(produit.getIdProduit(), produit.getIdProducteur());
            List<Float> poidsDisponibles = conditionnementDAO.getPoidsSachets(idConditionnement); // List<Float>

            if (poidsDisponibles.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun poids disponible pour ce produit.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3️⃣ Proposer une liste de choix
            String[] optionsPoids = poidsDisponibles.stream()
                                .map(p -> p + " kg")
                                .toArray(String[]::new);

            int choixPoids = JOptionPane.showOptionDialog(this,
                    "Choisissez le poids du sachet :",
                    "Poids du sachet",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    optionsPoids,
                    optionsPoids[0]);

            if (choixPoids < 0) return; // annulation

            float poidsSachet = poidsDisponibles.get(choixPoids);

            // 1️⃣ Choix de la quantité
            String qteStr = JOptionPane.showInputDialog(this,
                    "Quantité (nombre de sachets) :",
                    "Quantité",
                    JOptionPane.QUESTION_MESSAGE);
            if (qteStr == null || qteStr.trim().isEmpty()) return;

            //int quantite;
            try {
                quantite = Integer.parseInt(qteStr.trim());
                if (quantite <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantité invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            
            // it.setPoidsSachet(poidsSachet);
            // it.setQuantite(quantite);
        
        } else {
            // Vrac
            typeConditionnement = "Vrac";
            String qteStr = JOptionPane.showInputDialog(this,
                "Quantité (en kg ou litres) :",
                "Quantité",
                JOptionPane.QUESTION_MESSAGE);
            if (qteStr == null || qteStr.trim().isEmpty()) return;
            try {
                double qteVrac = Double.parseDouble(qteStr.trim());
                if (qteVrac <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantité invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Pour Vrac, on stocke dans quantite comme int * 1000 pour garder les décimales
                quantite = (int)(qteVrac * 1000);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantité invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Ajouter au panier
        CommandeItem item = new CommandeItem(
            produit.getIdProduit(),
            produit.getIdProducteur(),
            typeConditionnement,
            quantite
        );
        panier.add(item);

        // Ajouter à la table
        float prixUnitaire = produit.getPrixVenteClient();
        float qteAffichee = typeConditionnement.equals("Vrac") ? quantite / 1000.0f : quantite;
        float sousTotal = typeConditionnement.equals("Vrac") ? prixUnitaire * (quantite / 1000.0f) : prixUnitaire * quantite;
        Object[] row = {
            produit.getNomProduit(),
            typeConditionnement,
            String.format("%.2f EUR", prixUnitaire),
            typeConditionnement.equals("Vrac") ? String.format("%.3f kg/L", qteAffichee) : String.valueOf(quantite),
            String.format("%.2f EUR", sousTotal)
        };
        modelePanier.addRow(row);

        // Mettre à jour le total
        updateTotal();

        JOptionPane.showMessageDialog(this, 
            "Produit ajouté au panier", 
            "Succès", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void viderPanier() {
        panier.clear();
        panierContenants.clear();
        modelePanier.setRowCount(0);
        updateTotal();
    }

    private void ajouterContenantAuPanier() {
        Contenant contenant = (Contenant) comboContenant.getSelectedItem();
        if (contenant == null) return;
        
        int quantite = (int) spinnerQuantiteContenant.getValue();
        
        // Ajouter au panier contenants
        ContenantItem item = new ContenantItem(contenant.getReferenceContenant(), quantite);
        panierContenants.add(item);
        
        // Ajouter à la table
        float sousTotal = contenant.getPrixContenant() * quantite;
        Object[] row = {
            contenant.getTypeContenant() + " [CONTENANT]",
            contenant.getCapaciteContenant() + "L",
            String.format("%.2f EUR", contenant.getPrixContenant()),
            quantite,
            String.format("%.2f EUR", sousTotal)
        };
        modelePanier.addRow(row);
        
        // Mettre à jour le total
        updateTotal();
        
        JOptionPane.showMessageDialog(this,
            "Contenant ajouté au panier",
            "Succès",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateTotal() {
        float total = 0;
        for (int i = 0; i < modelePanier.getRowCount(); i++) {
            String sousTotal = (String) modelePanier.getValueAt(i, 4);
            sousTotal = sousTotal.replace(" EUR", "").replace(",", ".");
            total += Float.parseFloat(sousTotal);
        }
        lblTotal.setText(String.format("Total : %.2f EUR", total));
    }

    private void validerCommande() {
        if (panier.isEmpty() && panierContenants.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Votre panier est vide.", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idClient = Session.getClientId();
        if (idClient == null) {
            JOptionPane.showMessageDialog(this, 
                "Vous devez être connecté pour passer commande.", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String modePaiement = (String) comboModePaiement.getSelectedItem();
        String modeRecuperation = (String) comboModeRecuperation.getSelectedItem();

        // Validation paiement en ligne pour livraison domicile
        if ("Domicile".equals(modeRecuperation) && !"En ligne".equals(modePaiement)) {
            JOptionPane.showMessageDialog(this,
                "Le paiement en ligne est obligatoire pour une livraison à domicile.",
                "Mode de paiement invalide",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Si domicile, demander infos livraison
        String idAdresse = null;
        Float distanceLivraison = null;
        String typePaysLivraison = null;
        
        if ("Domicile".equals(modeRecuperation)) {
            // Sélection d'adresse du client via PossedeAdresse
            AdresseDAO adresseDAO = new AdresseDAO();
            List<Adresse> adresses = adresseDAO.getAdressesClient(idClient);
            if (adresses == null || adresses.isEmpty()) {
                int create = JOptionPane.showConfirmDialog(this,
                    "Aucune adresse enregistrée. Voulez-vous en créer une maintenant ?",
                    "Adresse manquante",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
                if (create == JOptionPane.YES_OPTION) {
                    String rue = JOptionPane.showInputDialog(this, "Rue :", "Création adresse", JOptionPane.QUESTION_MESSAGE);
                    String ville = JOptionPane.showInputDialog(this, "Ville :", "Création adresse", JOptionPane.QUESTION_MESSAGE);
                    String cp = JOptionPane.showInputDialog(this, "Code postal :", "Création adresse", JOptionPane.QUESTION_MESSAGE);
                    if (rue == null || ville == null || cp == null || rue.isBlank() || ville.isBlank() || cp.isBlank()) {
                        JOptionPane.showMessageDialog(this, "Création annulée ou champs invalides.", "Info", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    AdresseDAO adresseDAO2 = new AdresseDAO();
                    Adresse newAdr = adresseDAO2.creerAdresseClient(idClient, rue.trim(), ville.trim(), cp.trim());
                    if (newAdr == null) {
                        JOptionPane.showMessageDialog(this, "Échec création adresse.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    adresses = new ArrayList<>();
                    adresses.add(newAdr);
                } else {
                    return;
                }
            }
            Adresse choix = (Adresse) JOptionPane.showInputDialog(
                this,
                "Choisissez une adresse de livraison:",
                "Adresse",
                JOptionPane.QUESTION_MESSAGE,
                null,
                adresses.toArray(new Adresse[0]),
                adresses.get(0)
            );
            if (choix == null) return;
            idAdresse = choix.getIdAdresse();
            
            // Demander distance
            String distanceStr = JOptionPane.showInputDialog(this, 
                "Distance de livraison (en km) :", 
                "Informations de livraison", 
                JOptionPane.QUESTION_MESSAGE);
            if (distanceStr == null || distanceStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Distance requise pour livraison.", 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                distanceLivraison = Float.parseFloat(distanceStr.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Distance invalide.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Demander type pays
            String[] optionsPays = {"France Métropolitaine", "DOM-TOM", "International"};
            typePaysLivraison = (String) JOptionPane.showInputDialog(this, 
                "Type de destination :", 
                "Informations de livraison", 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                optionsPays, 
                optionsPays[0]);
            if (typePaysLivraison == null) {
                JOptionPane.showMessageDialog(this, 
                    "Type de destination requis.", 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            CommandeService service = new CommandeService();
            String idCommande = service.passerCommande(
                idClient,
                modeRecuperation,
                modePaiement,
                panier,
                panierContenants,
                idAdresse,
                distanceLivraison,
                typePaysLivraison
            );
            
            JOptionPane.showMessageDialog(this, 
                "Commande validée avec succès !\nNuméro de commande : " + idCommande, 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
            
            viderPanier();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la création de la commande :\n" + ex.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CommandeClient());
    }
}
