package interfaceGraphique;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

import service.ClotureCommande;
import dao.CommandeDAO;

/**
 * Interface pour clôturer une commande (retrait ou livraison)
 * Fonctionnalité 3: Clôture d'une commande
 */
public class ClotureCommandeWindow extends JFrame {

    private JTextField txtIdCommande;
    private JTextArea txtInfos;
    private CommandeDAO commandeDAO;

    public ClotureCommandeWindow() {
        commandeDAO = new CommandeDAO();

        setTitle("Clôture de Commande - Épicerie Locale");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 250));

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 245, 250));

        // En-tête
        JLabel titre = new JLabel(" Clôture de Commande");
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setForeground(new Color(138, 43, 226));
        titre.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel sousTitre = new JLabel("Finaliser le retrait en boutique ou la livraison à domicile");
        sousTitre.setFont(new Font("Arial", Font.PLAIN, 14));
        sousTitre.setForeground(new Color(100, 100, 100));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(new Color(240, 245, 250));
        headerPanel.add(titre);
        headerPanel.add(sousTitre);

        // Formulaire de saisie
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Informations de la Commande"),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblId = new JLabel("ID Commande:");
        lblId.setFont(new Font("Arial", Font.BOLD, 14));

        txtIdCommande = new JTextField(20);
        txtIdCommande.setFont(new Font("Arial", Font.PLAIN, 14));
        txtIdCommande.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JButton btnConsulter = new JButton(" Consulter");
        styleButton(btnConsulter, new Color(70, 130, 180));
        btnConsulter.addActionListener(e -> consulterCommande());

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblId, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(txtIdCommande, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        formPanel.add(btnConsulter, gbc);

        // Zone d'informations
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Détails de la Commande"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        infoPanel.setBackground(Color.WHITE);

        txtInfos = new JTextArea(15, 50);
        txtInfos.setEditable(false);
        txtInfos.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtInfos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtInfos.setText("Veuillez saisir un ID de commande et cliquer sur 'Consulter'");

        JScrollPane scrollPane = new JScrollPane(txtInfos);
        infoPanel.add(scrollPane);

        // Panel des boutons d'action
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(240, 245, 250));

        JButton btnCloturer = new JButton(" Clôturer la Commande");
        styleButton(btnCloturer, new Color(60, 179, 113));
        btnCloturer.addActionListener(e -> cloturerCommande());

        JButton btnRetour = new JButton(" Retour");
        styleButton(btnRetour, new Color(120, 120, 120));
        btnRetour.addActionListener(e -> {
            new MenuPrincipal();
            dispose();
        });

        JButton btnQuitter = new JButton(" Quitter");
        styleButton(btnQuitter, new Color(220, 80, 60));
        btnQuitter.addActionListener(e -> System.exit(0));

        btnPanel.add(btnCloturer);
        btnPanel.add(btnRetour);
        btnPanel.add(btnQuitter);

        // Panel central
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(240, 245, 250));
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(infoPanel, BorderLayout.CENTER);

        // Assembler
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    /**
     * Consulte les informations d'une commande
     */
    private void consulterCommande() {
        String idCommande = txtIdCommande.getText().trim();

        if (idCommande.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez saisir un ID de commande",
                "Champ requis",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Récupérer les informations de la commande
            String modeRecup = commandeDAO.recupModeRecuperation(idCommande);
            String modePaiement = commandeDAO.recupModePayement(idCommande);
            String statut = commandeDAO.recupStatutCommande(idCommande);

            if (modeRecup == null) {
                JOptionPane.showMessageDialog(this,
                    "Aucune commande trouvée avec l'ID: " + idCommande,
                    "Commande introuvable",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Construire l'affichage
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════\n");
            sb.append("   INFORMATIONS COMMANDE #").append(idCommande).append("\n");
            sb.append("═══════════════════════════════════════════════\n\n");
            sb.append(" Statut actuel:     ").append(statut).append("\n");
            sb.append(" Mode de paiement:  ").append(modePaiement).append("\n");
            sb.append(" Mode récupération: ").append(modeRecup).append("\n\n");

            if ("Domicile".equals(modeRecup)) {
                String idModeLivraison = commandeDAO.recupIdInfoLivraison(idCommande);
                if (idModeLivraison != null) {
                    int fraisLivraison = commandeDAO.calculFraisDeLivraison(idModeLivraison);
                    LocalDate dateEstimee = commandeDAO.calculDateEstimeeDeLivraison(idModeLivraison);

                    sb.append(" INFORMATIONS DE LIVRAISON\n");
                    sb.append("───────────────────────────────────────────────\n");
                    sb.append(" Frais de livraison:  ").append(fraisLivraison).append(" eur\n");
                    sb.append(" Date estimée:        ").append(dateEstimee).append("\n\n");
                }
            }

            sb.append("═══════════════════════════════════════════════\n");
            sb.append("\n  NOTES:\n");
            
            if ("En préparation".equals(statut)) {
                sb.append(" La commande est en cours de préparation\n");
            } else if ("Prête".equals(statut)) {
                sb.append(" La commande est prête pour le retrait/livraison\n");
            } else if ("Récupérée/Livrée".equals(statut)) {
                sb.append(" Cette commande a déjà été clôturée\n");
            }

            if ("En Boutique".equals(modePaiement)) {
                sb.append(" Le paiement sera effectué en boutique\n");
            } else {
                sb.append(" Le paiement a été effectué en ligne\n");
            }

            txtInfos.setText(sb.toString());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la consultation:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Clôture la commande
     */
    private void cloturerCommande() {
        String idCommande = txtIdCommande.getText().trim();

        if (idCommande.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez d'abord consulter une commande",
                "Aucune commande sélectionnée",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choix = JOptionPane.showConfirmDialog(this,
            "Voulez-vous clôturer la commande #" + idCommande + " ?\n\n" +
            "Cette action va:\n" +
            "• Encaisser le paiement\n" +
            "• Mettre à jour le statut\n" +
            "• Enregistrer la date de récupération/livraison",
            "Confirmation de clôture",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (choix == JOptionPane.YES_OPTION) {
            try {
                // Exécuter la transaction de clôture
                new ClotureCommande(idCommande);

                JOptionPane.showMessageDialog(this,
                    "✓ Commande clôturée avec succès !\n\n" +
                    "La commande #" + idCommande + " a été finalisée.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

                // Rafraîchir les informations
                consulterCommande();

            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(this,
                    " Erreur lors de la clôture:\n" + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    " Erreur inattendue:\n" + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * Style un bouton
     */
    private void styleButton(JButton btn, Color couleur) {
        btn.setBackground(couleur);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClotureCommandeWindow());
    }
}
