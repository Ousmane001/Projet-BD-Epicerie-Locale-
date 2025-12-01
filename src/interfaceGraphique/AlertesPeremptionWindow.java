package interfaceGraphique;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

        // Table
        model = new DefaultTableModel(
                new String[]{"Type", "ID Lot", "ID Produit", "ID Producteur", "Produit",
                        "Jours Restants", "Date Limite", "Réduction", "Statut"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(255, 140, 0));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnActualiser = new JButton("Actualiser");
        JButton btnReduction = new JButton("Appliquer réduction");
        JButton btnRetour = new JButton("Retour");

        btnActualiser.addActionListener(e -> chargerAlertes());
        btnReduction.addActionListener(e -> appliquerReductionSelectionnee());
        btnRetour.addActionListener(e -> {
            new MenuPrincipal();
            dispose();
        });

        btnPanel.add(btnActualiser);
        btnPanel.add(btnReduction);
        btnPanel.add(btnRetour);


        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        chargerAlertes();
        setVisible(true);
    }

    private void chargerAlertes() {
    model.setRowCount(0);

    try {
        List<AlertePeremption> alertes = alerteService.getAlertes();
        lblNbAlertes.setText(alertes.size() + " alerte(s)");

        for (AlertePeremption a : alertes) {

            String type = a.getTypeAlerte();
            String statut = type.equals("PERTE") ? "PERTE détectée" : "En attente";

            String reduction = type.equals("PERTE")
                    ? "—"
                    : String.format("%.0f%%", a.getReductionProposee() * 100);

            model.addRow(new Object[]{
                    type,
                    a.getIdLot(),
                    a.getIdProduit(),
                    a.getIdProducteur(),
                    a.getNomProduit(),
                    type.equals("PERTE") ? "—" : a.getJoursRestants() + " j",
                    type.equals("PERTE") ? "—" : a.getDateLimite(),
                    reduction,
                    statut
            });
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Erreur chargement alertes : " + e);
    }
}


    private void appliquerReductionSelectionnee() {

    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Sélectionnez une alerte.");
        return;
    }

    String type = (String) model.getValueAt(row, 0);
    if ("PERTE".equals(type)) {
        JOptionPane.showMessageDialog(this, "Impossible : ceci est une PERTE.");
        return;
    }

    String idLot = (String) model.getValueAt(row, 1);
    String idProduit = (String) model.getValueAt(row, 2);
    String idProducteur = (String) model.getValueAt(row, 3);

    int choix = JOptionPane.showConfirmDialog(this,
            "Appliquer réduction 30% ?\nLot : " + idLot,
            "Confirmation", JOptionPane.YES_NO_OPTION);

    if (choix != JOptionPane.YES_OPTION) return;

    try {
        AlertePeremption a = new AlertePeremption();
        a.setIdLot(idLot);
        a.setIdProduit(idProduit);
        a.setIdProducteur(idProducteur);

        alerteService.appliquerReduction(a);

        // Met à jour le statut dans la table
        model.setValueAt("Réduction appliquée", row, 8);  // Colonne "Statut"

        JOptionPane.showMessageDialog(this, "Réduction appliquée.");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Erreur réduction : " + e);
    }
}


    public static void main(String[] args) {
        new AlertesPeremptionWindow();
    }
}