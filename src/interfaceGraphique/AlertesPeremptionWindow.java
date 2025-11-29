package interfaceGraphique;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

import service.AlertePeremptionService;
import model.AlertePeremption;

public class AlertesPeremptionWindow extends JFrame {

    private AlertePeremptionService alerteService;
    private DefaultTableModel model;
    private JTable table;
    private JLabel lblNbAlertes;

    public AlertesPeremptionWindow() {
        alerteService = new AlertePeremptionService();

        setTitle("Alertes de Péremption - Épicerie Locale");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 245, 250));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 245, 250));

        JLabel titre = new JLabel(" Alertes de Péremption");
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setForeground(new Color(255, 140, 0));

        lblNbAlertes = new JLabel();
        lblNbAlertes.setFont(new Font("Arial", Font.PLAIN, 14));
        lblNbAlertes.setForeground(new Color(100, 100, 100));

        headerPanel.add(titre, BorderLayout.NORTH);
        headerPanel.add(lblNbAlertes, BorderLayout.SOUTH);

        // ----- TABLE -----
        model = new DefaultTableModel(
            new String[]{"ID Lot", "ID Produit", "ID Producteur", "Produit", "Jours Restants", "Date Limite", "Réduction", "Statut"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(255, 140, 0));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        // ----- CACHER idProduit et idProducteur -----
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setWidth(0);

        table.getColumnModel().getColumn(2).setMinWidth(0);
        table.getColumnModel().getColumn(2).setMaxWidth(0);
        table.getColumnModel().getColumn(2).setWidth(0);

        // ----- Largeurs des colonnes visibles -----
        table.getColumnModel().getColumn(0).setPreferredWidth(80);   // idLot
        table.getColumnModel().getColumn(3).setPreferredWidth(200);  // nom produit
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // jours
        table.getColumnModel().getColumn(5).setPreferredWidth(120);  // date limite
        table.getColumnModel().getColumn(6).setPreferredWidth(80);   // réduction
        table.getColumnModel().getColumn(7).setPreferredWidth(150);  // statut

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Produits proches de la péremption (< 7 jours)"));

        // ----- BOUTONS -----
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(240, 245, 250));

        JButton btnActualiser = creerBouton(" Actualiser", new Color(70, 130, 180));
        JButton btnAppliquerReduction = creerBouton(" Appliquer Réduction", new Color(60, 179, 113));
        JButton btnRetour = creerBouton(" Retour", new Color(120, 120, 120));
        JButton btnQuitter = creerBouton(" Quitter", new Color(220, 80, 60));

        btnActualiser.addActionListener(e -> chargerAlertes());
        btnAppliquerReduction.addActionListener(e -> appliquerReductionSelectionnee());

        btnRetour.addActionListener(e -> {
            new MenuPrincipal();
            dispose();
        });

        btnQuitter.addActionListener(e -> System.exit(0));

        btnPanel.add(btnActualiser);
        btnPanel.add(btnAppliquerReduction);
        btnPanel.add(btnRetour);
        btnPanel.add(btnQuitter);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        chargerAlertes();
        setVisible(true);
    }


    // ======= CHARGEMENT ALERTES =======
    private void chargerAlertes() {
        model.setRowCount(0);

        try {
            List<AlertePeremption> alertes = alerteService.genererAlertes();

            lblNbAlertes.setText(alertes.size() + " alerte(s) trouvée(s)");

            for (AlertePeremption alerte : alertes) {

                String reduction = String.format("%.0f%%", alerte.getReductionProposee() * 100);
                String statut = "En attente";

                model.addRow(new Object[]{
                    alerte.getIdLot(),
                    alerte.getIdProduit(),     // caché
                    alerte.getIdProducteur(),  // caché
                    alerte.getNomProduit(),
                    alerte.getJoursRestants() + " jour(s)",
                    alerte.getDateLimite(),
                    reduction,
                    statut
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des alertes:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }


    // ======= APPLIQUER RÉDUCTION =======
    private void appliquerReductionSelectionnee() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une alerte.",
                "Sélection requise",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idLot = (String) model.getValueAt(row, 0);
        String idProduit = (String) model.getValueAt(row, 1);
        String idProducteur = (String) model.getValueAt(row, 2);

        int choix = JOptionPane.showConfirmDialog(this,
            "Voulez-vous appliquer une réduction de 30 % ?\n\n" +
            "Lot : " + idLot + "\n" +
            "Produit : " + idProduit,
            "Confirmation",
            JOptionPane.YES_NO_OPTION);

        if (choix == JOptionPane.YES_OPTION) {
            try {
                AlertePeremption alerte = new AlertePeremption();
                alerte.setIdLot(idLot);
                alerte.setIdProduit(idProduit);
                alerte.setIdProducteur(idProducteur);

                alerteService.appliquerReduction(alerte);

                JOptionPane.showMessageDialog(this,
                    "Réduction appliquée avec succès !");

                // 🔥 Changer statut visuel
                model.setValueAt("Réduction appliquée", row, 7);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur réduction : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private JButton creerBouton(String texte, Color couleur) {
        JButton btn = new JButton(texte);
        btn.setBackground(couleur);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setOpaque(true);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(couleur.darker(), 2),
            BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
        SwingUtilities.invokeLater(() -> new AlertesPeremptionWindow());
    }
}
