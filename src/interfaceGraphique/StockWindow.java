package interfaceGraphique;

import dao.LotDAO;
import dao.ProduitDAO;
import dao.StockDAO;
import model.ProduitDisponible;
import config.DataSourceProvider;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Fenêtre de consultation du stock
 */
public class StockWindow extends JFrame {

    private DefaultTableModel model;
    private JTable table;
    private ProduitDAO produitDAO = new ProduitDAO();
    private StockDAO stockDAO = new StockDAO();
    private LotDAO lotDAO = new LotDAO();

    public StockWindow() {
        setTitle("Consultation de Stock - Épicerie Locale");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));

        model = new DefaultTableModel(
            new String[]{"IDProduit", "Produit", "Producteur", "Type", "Quantité", "Prochaine péremption", "Statut"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 13));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        // columns are now IDProduit(0 hidden), Produit(1), Producteur(2), Type(3), Quantité(4), Prochaine péremption(5), Statut(6)
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // display the ID column
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(140);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);

        // Double clique : afficher détails lots
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    afficherDetailsLots();
                }
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnPanel.setBackground(new Color(240, 245, 250));

        JButton btnActualiser = creerBouton(" Actualiser", new Color(70, 130, 180));
        JButton btnVoirLots = creerBouton(" Voir Lots", new Color(60, 179, 113));
        JButton btnRetour = creerBouton(" Retour", new Color(120, 120, 120));
        JButton btnQuitter = creerBouton(" Quitter", new Color(220, 80, 60));

        btnActualiser.addActionListener(e -> chargerStock());
        btnVoirLots.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit pour voir ses lots.", "Sélection requise", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String idProduit = (String) model.getValueAt(row, 0);
            String idProducteur = (String) model.getValueAt(row, 2);
            new LotWindow(idProduit, idProducteur);
        });
        btnRetour.addActionListener(e -> {
            new MenuPrincipal();
            dispose();
        });
        btnQuitter.addActionListener(e -> System.exit(0));

        btnPanel.add(btnActualiser);
        btnPanel.add(btnVoirLots);
        btnPanel.add(btnRetour);
        btnPanel.add(btnQuitter);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        mainPanel.setBackground(new Color(240, 245, 250));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Stock par produit"));

        mainPanel.add(scroll, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        chargerStock();

        setVisible(true);
    }

    private void chargerStock() {
        model.setRowCount(0);
        Connection conn = null;
        try {
            conn = DataSourceProvider.getConnection();
            List<ProduitDisponible> produits = produitDAO.getProduitsDisponibles();

            for (ProduitDisponible p : produits) {
                String idProduit = p.getIdProduit();
                String idProducteur = p.getIdProducteur();
                String type = p.getTypeConditionnement();

                String idStock = stockDAO.getIdStock(idProduit, idProducteur, conn);
                if (idStock == null) {
                    // pas de stock associé
                    model.addRow(new Object[]{p.getNomProduit(), idProducteur, type, "0", "-", "Rupture"});
                    continue;
                }

                ResultSet lots = stockDAO.getLotsOrdonnesByIdStock(idStock, conn);
                double totalVrac = 0.0;
                int totalPre = 0;
                LocalDate plusProchePeremption = null;
                List<String> lotInfo = new ArrayList<>();

                while (lots != null && lots.next()) {
                    String idLot = lots.getString("idLot");
                    java.sql.Date dl = lots.getDate("dateLimite");
                    LocalDate dateLimite = dl != null ? dl.toLocalDate() : null;
                    if (dateLimite != null) {
                        if (plusProchePeremption == null || dateLimite.isBefore(plusProchePeremption)) {
                            plusProchePeremption = dateLimite;
                        }
                    }

                    String typeLot = lotDAO.getConditionnementByIdLot(idLot, conn);
                    if (typeLot == null) typeLot = "Inconnu";

                    if ("Preconditionne".equalsIgnoreCase(typeLot)) {
                        Integer qte = stockDAO.getQuantitePreconditionneLot(idLot, conn);
                        int q = qte != null ? qte : 0;
                        totalPre += q;
                        lotInfo.add(String.format("Lot %s - %d pcs - %s", idLot, q, dateLimite));
                    } else if ("Vrac".equalsIgnoreCase(typeLot)) {
                        Double qte = stockDAO.getQuantiteVracLot(idLot, conn);
                        double q = qte != null ? qte : 0.0;
                        totalVrac += q;
                        lotInfo.add(String.format("Lot %s - %.3f kg - %s", idLot, q, dateLimite));
                    }
                }

                String qteAffiche;
                if (p.getTypeConditionnement() != null && p.getTypeConditionnement().equalsIgnoreCase("Vrac")) {
                    qteAffiche = String.format("%.3f kg", totalVrac);
                } else {
                    qteAffiche = String.valueOf(totalPre);
                }

                String statut = "Disponible";
                if ((p.getTypeConditionnement() != null && p.getTypeConditionnement().equalsIgnoreCase("Vrac") && totalVrac == 0.0) ||
                    (p.getTypeConditionnement() == null || (!p.getTypeConditionnement().equalsIgnoreCase("Vrac") && totalPre == 0))) {
                    statut = "Rupture";
                }

                String peremption = plusProchePeremption != null ? plusProchePeremption.toString() : "-";

                model.addRow(new Object[]{idProduit, p.getNomProduit(), idProducteur, p.getTypeConditionnement(), qteAffiche, peremption, statut});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement du stock:\n" + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DataSourceProvider.closeConnection(conn);
        }
    }

    private void afficherDetailsLots() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit.", "Sélection requise", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String idProduit = (String) model.getValueAt(row, 0);
        String nomProduit = (String) model.getValueAt(row, 1);
        String idProducteur = (String) model.getValueAt(row, 2);
        // Récupérer idProduit en cherchant dans DAO par nom (note: il serait préférable de garder l'ID en tant que hidden column)
        // Pour simplicité, on repère dans la liste
        Connection conn = null;
        try {
            conn = DataSourceProvider.getConnection();
            // idProduit is available directly from the table model
            String idStock = stockDAO.getIdStock(idProduit, idProducteur, conn);
            if (idStock == null) {
                JOptionPane.showMessageDialog(this, "Aucun stock pour ce produit.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            ResultSet lots = stockDAO.getLotsOrdonnesByIdStock(idStock, conn);
            StringBuilder sb = new StringBuilder();
            sb.append("Lots pour : ").append(nomProduit).append("\n\n");

            while (lots != null && lots.next()) {
                String idLot = lots.getString("idLot");
                java.sql.Date dl = lots.getDate("dateLimite");
                LocalDate dateLimite = dl != null ? dl.toLocalDate() : null;
                String typeLot = lotDAO.getConditionnementByIdLot(idLot, conn);
                if ("Preconditionne".equalsIgnoreCase(typeLot)) {
                    Integer qte = stockDAO.getQuantitePreconditionneLot(idLot, conn);
                    sb.append(String.format("- %s : %d pcs - peremption: %s\n", idLot, qte != null ? qte : 0, dateLimite));
                } else if ("Vrac".equalsIgnoreCase(typeLot)) {
                    Double qte = stockDAO.getQuantiteVracLot(idLot, conn);
                    sb.append(String.format("- %s : %.3f kg - peremption: %s\n", idLot, qte != null ? qte : 0.0, dateLimite));
                } else {
                    sb.append(String.format("- %s : type=%s - peremption: %s\n", idLot, typeLot, dateLimite));
                }
            }

            JTextArea ta = new JTextArea(sb.toString());
            ta.setEditable(false);
            ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scroll = new JScrollPane(ta);
            scroll.setPreferredSize(new Dimension(600, 300));

            JOptionPane.showMessageDialog(this, scroll, "Détails des Lots", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            DataSourceProvider.closeConnection(conn);
        }
    }

    private JButton creerBouton(String texte, Color couleur) {
        JButton btn = new JButton(texte);
        btn.setBackground(couleur);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(couleur.darker(), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
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
        SwingUtilities.invokeLater(() -> new StockWindow());
    }
}
