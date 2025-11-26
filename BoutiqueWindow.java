import javax.swing.*;
import java.awt.*;

public class BoutiqueWindow extends JFrame {

    public BoutiqueWindow() {
        setTitle("Boutique");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));

        JButton catButton = new JButton("Catalogue");
        JButton commanderButton = new JButton("Commander");
        JButton btnQuitter = new JButton("Quitter");

        // STYLE BOUTONS
        catButton.setBackground(new Color(70, 130, 180));
        catButton.setForeground(Color.WHITE);
        catButton.setFont(new Font("Arial", Font.BOLD, 14));
        catButton.setFocusPainted(false);
        catButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        commanderButton.setBackground(new Color(60, 179, 113));
        commanderButton.setForeground(Color.WHITE);
        commanderButton.setFont(new Font("Arial", Font.BOLD, 14));
        commanderButton.setFocusPainted(false);

        btnQuitter.setBackground(new Color(220, 80, 60));
        btnQuitter.setForeground(Color.WHITE);
        btnQuitter.setFont(new Font("Arial", Font.BOLD, 12));
        btnQuitter.setFocusPainted(false);
        btnQuitter.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // ---- ACTIONS ----

        // Catalogue → OK
        catButton.addActionListener(e -> {
            new Catalogue();
            dispose();
        });

        // Commander → Login d'abord
        commanderButton.addActionListener(e -> {
            new Login();
            dispose();
        });

        btnQuitter.addActionListener(e -> System.exit(0));

        // ---- PANELS ----

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(240, 245, 250));

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180)),
                        "Options Boutique"
                ),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(catButton);
        centerPanel.add(commanderButton);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(240, 245, 250));
        bottomPanel.add(btnQuitter); // <-- seulement Quitter maintenant

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new BoutiqueWindow();
    }
}
