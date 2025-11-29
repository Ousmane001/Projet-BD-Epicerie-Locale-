package interfaceGraphique;

import dao.ClientDAO;
import model.Session;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {
    private JTextField txtEmail;

    public Login() {
        setTitle("Espace Client");
        setSize(420, 180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));

        JLabel lblEmail = new JLabel("Email :");
        lblEmail.setFont(new Font("Arial", Font.BOLD, 14));
        lblEmail.setForeground(new Color(60, 60, 60));

        // Champ email
        txtEmail = new JTextField();
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        txtEmail.setColumns(20);
        txtEmail.setPreferredSize(new Dimension(250, 35));
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton btnSuivant = new JButton("Suivant");
        JButton btnQuitter = new JButton("Quitter");

        // Panel principal
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

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.add(lblEmail);
        inputPanel.add(txtEmail);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnSuivant);
        btnPanel.add(btnQuitter);

        panel.add(inputPanel);
        panel.add(btnPanel);

        // Actions
        btnSuivant.addActionListener(e -> {
            String email = txtEmail.getText().trim();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(Login.this, "Veuillez entrer votre email.", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ClientDAO clientDAO = new ClientDAO();
            String idClient = clientDAO.getClientIdByEmail(email);
            if (idClient != null) {
                Session.setClientId(idClient);
                Session.setRole("CLIENT");
                // ouvrir l'interface commande
                new CommandeClient();
                dispose();
            } else {
                JOptionPane.showMessageDialog(Login.this, "Email inconnu dans la base. Veuillez vérifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
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
