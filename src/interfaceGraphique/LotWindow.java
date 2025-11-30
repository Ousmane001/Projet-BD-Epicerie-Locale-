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
 * Fenêtre affichant la liste des lots par produit
 */
public class LotWindow extends JFrame {

    private JComboBox<ProductItem> comboProduits;
    private DefaultTableModel model;
    private JTable table;
    private ProduitDAO produitDAO = new ProduitDAO();
    private StockDAO stockDAO = new StockDAO();
    private LotDAO lotDAO = new LotDAO();

    public LotWindow() {
        setTitle("Consulter les Lots - Épicerie Locale");
        setSize(900, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(240, 245, 250));
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 0, 12));

        JLabel lblProduit = new JLabel("Produit:");
        lblProduit.setFont(new Font("Arial", Font.BOLD, 14));
        comboProduits = new JComboBox<>();
        comboProduits.setPreferredSize(new Dimension(420, 26));
        header.add(lblProduit);
        header.add(comboProduits);

        JButton btnCharger = creerBouton(" Charger", new Color(70, 130, 180));
        JButton btnRetour = creerBouton(" Retour", new Color(120, 120, 120));
        JButton btnQuitter = creerBouton(" Quitter", new Color(220, 80, 60));

        header.add(btnCharger);
        header.add(btnRetour);
        header.add(btnQuitter);

        btnRetour.addActionListener(e -> { new MenuPrincipal(); dispose(); });
        btnQuitter.addActionListener(e -> System.exit(0));
        btnCharger.addActionListener(e -> chargerLots());

        model = new DefaultTableModel(
            new String[]{"ID Lot", "Type", "Quantité Disponible", "Date Limite", "ID Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));

        JTableHeader headerTable = table.getTableHeader();
        headerTable.setBackground(new Color(70, 130, 180));
        headerTable.setForeground(Color.WHITE);
        headerTable.setFont(new Font("Arial", Font.BOLD, 13));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);

        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Liste des Lots pour le produit sélectionné"));

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        chargerProduits();
        setVisible(true);
    }

    public LotWindow(String idProduit, String idProducteur) {
        this();
        // After UI is created and produits loaded, select the one that matches and load lots
        for (int i = 0; i < comboProduits.getItemCount(); i++) {
            ProductItem pi = comboProduits.getItemAt(i);
            if (pi.idProduit.equals(idProduit) && pi.idProducteur.equals(idProducteur)) {
                comboProduits.setSelectedIndex(i);
                break;
            }
        }
        chargerLots();
    }

    private void chargerProduits() {
        comboProduits.removeAllItems();
        try {
            List<ProduitDisponible> produits = produitDAO.getProduitsDisponibles();
            for (ProduitDisponible p : produits) {
                comboProduits.addItem(new ProductItem(p.getIdProduit(), p.getIdProducteur(), p.getNomProduit()));
            }
            if (comboProduits.getItemCount() > 0) comboProduits.setSelectedIndex(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des produits: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
        }
    }

    private void chargerLots() {
        model.setRowCount(0);
        ProductItem it = (ProductItem) comboProduits.getSelectedItem();
        if (it == null) return;

        Connection conn = null;
        try {
            conn = DataSourceProvider.getConnection();
            String idStock = stockDAO.getIdStock(it.idProduit, it.idProducteur, conn);
            if (idStock == null) {
                JOptionPane.showMessageDialog(this, "Aucun stock trouvé pour ce produit.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            ResultSet rs = stockDAO.getLotsOrdonnesByIdStock(idStock, conn);
            while (rs != null && rs.next()) {
                String idLot = rs.getString("idLot");
                java.sql.Date dl = rs.getDate("dateLimite");
                LocalDate dateLimite = dl != null ? dl.toLocalDate() : null;
                String type = lotDAO.getConditionnementByIdLot(idLot, conn);
                String qteDisp;
                if ("Preconditionne".equalsIgnoreCase(type)) {
                    Integer q = stockDAO.getQuantitePreconditionneLot(idLot, conn);
                    qteDisp = q != null ? q.toString() + " pcs" : "0 pcs";
                } else if ("Vrac".equalsIgnoreCase(type)) {
                    Double q = stockDAO.getQuantiteVracLot(idLot, conn);
                    qteDisp = q != null ? String.format("%.3f kg", q) : "0.000 kg";
                } else {
                    qteDisp = "-";
                }

                model.addRow(new Object[]{idLot, type != null ? type : "Inconnu", qteDisp, dateLimite != null ? dateLimite.toString() : "-", idStock});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            //ex.printStackTrace();
        } finally {
            DataSourceProvider.closeConnection(conn);
        }
    }

    private JButton creerBouton(String texte, Color couleur) {
        JButton btn = new JButton(texte);
        btn.setBackground(couleur);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(couleur.darker(), 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(couleur.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(couleur); }
        });
        return btn;
    }

    private static class ProductItem {
        final String idProduit;
        final String idProducteur;
        final String name;

        ProductItem(String idProduit, String idProducteur, String name) {
            this.idProduit = idProduit;
            this.idProducteur = idProducteur;
            this.name = name;
        }

        @Override public String toString() { return name + " (" + idProducteur + ")"; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LotWindow());
    }
}
