package interfaceGraphique;
import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {
    private JTextField txtLogin;

    public Login() {
        setTitle("Espace Client");
        setSize(400, 180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));

        JLabel lblLogin = new JLabel("Nom :");
        lblLogin.setFont(new Font("Arial", Font.BOLD, 14));
        lblLogin.setForeground(new Color(60, 60, 60));

        // ---- Champ de texte agrandi ----
        txtLogin = new JTextField();
        txtLogin.setFont(new Font("Arial", Font.PLAIN, 16));  // texte visible
        txtLogin.setColumns(20);                               // largeur
        txtLogin.setPreferredSize(new Dimension(250, 35));     // hauteur suffisante
        txtLogin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton btnSuivant = new JButton("Suivant");
        JButton btnQuitter = new JButton("Quitter");

        btnSuivant.setBackground(new Color(70, 130, 180));
        btnSuivant.setForeground(Color.WHITE);
        btnSuivant.setFont(new Font("Arial", Font.BOLD, 12));
        btnSuivant.setFocusPainted(false);
        btnSuivant.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        btnQuitter.setBackground(new Color(220, 80, 60));
        btnQuitter.setForeground(Color.WHITE);
        btnQuitter.setFont(new Font("Arial", Font.BOLD, 12));
        btnQuitter.setFocusPainted(false);
        btnQuitter.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // ---- Panel principal ----
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                        "Connexion"
                ),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Ligne pour label + champ
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.add(lblLogin);
        inputPanel.add(txtLogin);

        // Ligne pour boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnSuivant);
        btnPanel.add(btnQuitter);

        panel.add(inputPanel);
        panel.add(btnPanel);

        // ---- Actions ----
        btnSuivant.addActionListener(e -> {
            String login = txtLogin.getText().trim();
            if (!login.isEmpty()) {
                new Commande();  // ouvrir Commande
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        Login.this,
                        "Veuillez entrer un nom.",
                        "Erreur",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        btnQuitter.addActionListener(e -> System.exit(0));

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Login();
    }
}
