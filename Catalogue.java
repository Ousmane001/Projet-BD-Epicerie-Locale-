import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.sql.*;
import java.awt.*;

public class Catalogue extends JFrame {

    public Catalogue() {

        setTitle("Catalogue des Produits");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));

        // ----------- TABLE ------------
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Nom du produit"}, 0
        );
        JTable table = new JTable(model);

        // ----------- CONNEXION SQL + REQUÊTE ------------
        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@oracle1:1521:XE",
                    "mengossn",
                    "mengossn"
            );

            String sql = "SELECT nomproduit FROM PRODUITS";

            PreparedStatement st = con.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                String nom = rs.getString("nomproduit");
                model.addRow(new Object[]{nom});
            }

            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur SQL : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        // ----------- STYLES TABLE ------------
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);

        // ----------- BOUTONS ------------
        JButton btnRetour = new JButton("Précédent");
        JButton btnQuitter = new JButton("Quitter");

        btnRetour.setBackground(new Color(70, 130, 180));
        btnRetour.setForeground(Color.WHITE);

        btnQuitter.setBackground(new Color(220, 80, 60));
        btnQuitter.setForeground(Color.WHITE);

        btnRetour.addActionListener(e -> {
            new BoutiqueWindow();
            dispose();
        });

        btnQuitter.addActionListener(e -> System.exit(0));

        // ----------- PANELS ------------
        JPanel panelTable = new JPanel(new BorderLayout());
        panelTable.setBorder(BorderFactory.createTitledBorder("Produits"));
        panelTable.add(new JScrollPane(table));

        JPanel panelBoutons = new JPanel();
        panelBoutons.add(btnRetour);
        panelBoutons.add(btnQuitter);

        add(panelTable, BorderLayout.CENTER);
        add(panelBoutons, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        new Catalogue();
    }
}
