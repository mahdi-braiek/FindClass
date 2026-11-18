package gui;

import dao.UserDAO;
import gui.components.PlaceholderPasswordField;
import gui.components.PlaceholderTextField;
import gui.components.RoundedButton;
import gui.components.RoundedPanel;
import model.User;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * SettingsPanel — Allows users to view and update their profile information,
 * change their password, and manage account preferences.
 */
public class SettingsPanel extends JPanel {

    private User currentUser;
    private UserDAO userDAO = new UserDAO();

    // Profile fields
    private PlaceholderTextField nameField, emailField;
    private JLabel roleDisplay;

    // Password fields
    private PlaceholderPasswordField currentPassField, newPassField, confirmPassField;

    public SettingsPanel(User user) {
        this.currentUser = user;
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        buildUI();
    }

    private void buildUI() {
        JPanel container = new JPanel();
        container.setBackground(UIConstants.BG_DARK);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        // ── Header ──────────────────────────────────────────
        JLabel header = new JLabel("⚙️  Paramètres du Compte");
        header.setFont(UIConstants.FONT_TITLE);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Gérez votre profil et vos préférences");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(subtitle);
        container.add(Box.createVerticalStrut(30));

        // ══════════════════════════════════════════════════════
        // SECTION 1: Profile Avatar & Info Card
        // ══════════════════════════════════════════════════════
        RoundedPanel profileCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_MEDIUM);
        profileCard.setLayout(new BorderLayout(25, 0));
        profileCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        profileCard.setMaximumSize(new Dimension(700, 160));
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Avatar circle
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient circle
                GradientPaint gradient = new GradientPaint(0, 0, UIConstants.ACCENT_PRIMARY,
                        getWidth(), getHeight(), UIConstants.ACCENT_PURPLE);
                g2d.setPaint(gradient);
                g2d.fillOval(5, 5, 80, 80);

                // Initials
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 30));
                String initials = getInitials(currentUser.getName());
                FontMetrics fm = g2d.getFontMetrics();
                int textW = fm.stringWidth(initials);
                g2d.drawString(initials, 45 - textW / 2, 52 + fm.getAscent() / 3);
            }
        };
        avatarPanel.setOpaque(false);
        avatarPanel.setPreferredSize(new Dimension(90, 90));

        // User info next to avatar
        JPanel userInfo = new JPanel();
        userInfo.setOpaque(false);
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));

        JLabel nameDisplay = new JLabel(currentUser.getName());
        nameDisplay.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameDisplay.setForeground(UIConstants.TEXT_PRIMARY);
        nameDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emailDisplay = new JLabel(currentUser.getEmail());
        emailDisplay.setFont(UIConstants.FONT_BODY);
        emailDisplay.setForeground(UIConstants.TEXT_SECONDARY);
        emailDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Role badge
        JPanel roleBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(UIConstants.ACCENT_PRIMARY.getRed(),
                        UIConstants.ACCENT_PRIMARY.getGreen(),
                        UIConstants.ACCENT_PRIMARY.getBlue(), 30));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
        };
        roleBadge.setOpaque(false);
        roleBadge.setMaximumSize(new Dimension(120, 28));
        roleBadge.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 4));

        JLabel roleText = new JLabel(currentUser.getRole());
        roleText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleText.setForeground(UIConstants.ACCENT_PRIMARY);
        roleBadge.add(roleText);
        roleBadge.setAlignmentX(Component.LEFT_ALIGNMENT);

        userInfo.add(nameDisplay);
        userInfo.add(Box.createVerticalStrut(4));
        userInfo.add(emailDisplay);
        userInfo.add(Box.createVerticalStrut(8));
        userInfo.add(roleBadge);

        profileCard.add(avatarPanel, BorderLayout.WEST);
        profileCard.add(userInfo, BorderLayout.CENTER);

        container.add(profileCard);
        container.add(Box.createVerticalStrut(30));

        // ══════════════════════════════════════════════════════
        // SECTION 2: Edit Profile
        // ══════════════════════════════════════════════════════
        JLabel editTitle = new JLabel("✏️  Modifier le Profil");
        editTitle.setFont(UIConstants.FONT_SUBTITLE);
        editTitle.setForeground(UIConstants.TEXT_PRIMARY);
        editTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(editTitle);
        container.add(Box.createVerticalStrut(15));

        RoundedPanel editCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_MEDIUM);
        editCard.setLayout(new GridBagLayout());
        editCard.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        editCard.setMaximumSize(new Dimension(700, 200));
        editCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel nameLbl = new JLabel("Nom complet :");
        nameLbl.setFont(UIConstants.FONT_BODY);
        nameLbl.setForeground(UIConstants.TEXT_SECONDARY);
        editCard.add(nameLbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new PlaceholderTextField("Votre nom");
        nameField.setText(currentUser.getName());
        nameField.setPreferredSize(new Dimension(300, 40));
        editCard.add(nameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel emailLbl = new JLabel("Email :");
        emailLbl.setFont(UIConstants.FONT_BODY);
        emailLbl.setForeground(UIConstants.TEXT_SECONDARY);
        editCard.add(emailLbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new PlaceholderTextField("email@example.com");
        emailField.setText(currentUser.getEmail());
        emailField.setPreferredSize(new Dimension(300, 40));
        editCard.add(emailField, gbc);

        // Role (read-only)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel roleLbl = new JLabel("Rôle :");
        roleLbl.setFont(UIConstants.FONT_BODY);
        roleLbl.setForeground(UIConstants.TEXT_SECONDARY);
        editCard.add(roleLbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        roleDisplay = new JLabel(currentUser.getRole() + "  (non modifiable)");
        roleDisplay.setFont(UIConstants.FONT_BODY);
        roleDisplay.setForeground(UIConstants.TEXT_MUTED);
        editCard.add(roleDisplay, gbc);

        // Save button
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        RoundedButton saveProfileBtn = new RoundedButton("💾  Sauvegarder", UIConstants.ACCENT_PRIMARY,
                UIConstants.ACCENT_PRIMARY.brighter());
        saveProfileBtn.setPreferredSize(new Dimension(170, 42));
        saveProfileBtn.addActionListener(e -> saveProfile());
        editCard.add(saveProfileBtn, gbc);

        container.add(editCard);
        container.add(Box.createVerticalStrut(30));

        // ══════════════════════════════════════════════════════
        // SECTION 3: Change Password
        // ══════════════════════════════════════════════════════
        JLabel passTitle = new JLabel("🔒  Changer le Mot de Passe");
        passTitle.setFont(UIConstants.FONT_SUBTITLE);
        passTitle.setForeground(UIConstants.TEXT_PRIMARY);
        passTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(passTitle);
        container.add(Box.createVerticalStrut(15));

        RoundedPanel passCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_MEDIUM);
        passCard.setLayout(new GridBagLayout());
        passCard.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        passCard.setMaximumSize(new Dimension(700, 250));
        passCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(6, 8, 6, 8);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        // Current password
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.weightx = 0;
        JLabel curPassLbl = new JLabel("Mot de passe actuel :");
        curPassLbl.setFont(UIConstants.FONT_BODY);
        curPassLbl.setForeground(UIConstants.TEXT_SECONDARY);
        passCard.add(curPassLbl, gbc2);

        gbc2.gridx = 1;
        gbc2.weightx = 1.0;
        currentPassField = new PlaceholderPasswordField("••••••••");
        currentPassField.setPreferredSize(new Dimension(300, 40));
        passCard.add(currentPassField, gbc2);

        // New password
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 0;
        JLabel newPassLbl = new JLabel("Nouveau mot de passe :");
        newPassLbl.setFont(UIConstants.FONT_BODY);
        newPassLbl.setForeground(UIConstants.TEXT_SECONDARY);
        passCard.add(newPassLbl, gbc2);

        gbc2.gridx = 1;
        gbc2.weightx = 1.0;
        newPassField = new PlaceholderPasswordField("Minimum 6 caractères");
        newPassField.setPreferredSize(new Dimension(300, 40));
        passCard.add(newPassField, gbc2);

        // Confirm password
        gbc2.gridx = 0;
        gbc2.gridy = 2;
        gbc2.weightx = 0;
        JLabel confPassLbl = new JLabel("Confirmer :");
        confPassLbl.setFont(UIConstants.FONT_BODY);
        confPassLbl.setForeground(UIConstants.TEXT_SECONDARY);
        passCard.add(confPassLbl, gbc2);

        gbc2.gridx = 1;
        gbc2.weightx = 1.0;
        confirmPassField = new PlaceholderPasswordField("Retapez le mot de passe");
        confirmPassField.setPreferredSize(new Dimension(300, 40));
        passCard.add(confirmPassField, gbc2);

        // Change password button
        gbc2.gridx = 1;
        gbc2.gridy = 3;
        gbc2.weightx = 0;
        gbc2.anchor = GridBagConstraints.EAST;
        RoundedButton changePassBtn = new RoundedButton("🔐  Changer", UIConstants.ACCENT_WARNING,
                UIConstants.ACCENT_WARNING.brighter());
        changePassBtn.setPreferredSize(new Dimension(170, 42));
        changePassBtn.addActionListener(e -> changePassword());
        passCard.add(changePassBtn, gbc2);

        container.add(passCard);
        container.add(Box.createVerticalStrut(30));

        // ══════════════════════════════════════════════════════
        // SECTION 4: Account Info
        // ══════════════════════════════════════════════════════
        JLabel infoTitle = new JLabel("ℹ️  Informations");
        infoTitle.setFont(UIConstants.FONT_SUBTITLE);
        infoTitle.setForeground(UIConstants.TEXT_PRIMARY);
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(infoTitle);
        container.add(Box.createVerticalStrut(15));

        RoundedPanel infoCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_MEDIUM);
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        infoCard.setMaximumSize(new Dimension(700, 120));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoCard.add(createInfoLine("🆔", "ID Utilisateur", String.valueOf(currentUser.getId())));
        infoCard.add(Box.createVerticalStrut(8));
        infoCard.add(createInfoLine("🏷", "Rôle", currentUser.getRole()));
        infoCard.add(Box.createVerticalStrut(8));
        infoCard.add(createInfoLine("📧", "Email", currentUser.getEmail()));

        container.add(infoCard);

        // ── Main Scroll ─────────────────────────────────────
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setBackground(UIConstants.BG_DARK);
        mainScroll.getViewport().setBackground(UIConstants.BG_DARK);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    private JPanel createInfoLine(String icon, String label, String value) {
        JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        line.setOpaque(false);
        line.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        JLabel labelLbl = new JLabel(label + " :");
        labelLbl.setFont(UIConstants.FONT_BODY);
        labelLbl.setForeground(UIConstants.TEXT_SECONDARY);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLbl.setForeground(UIConstants.TEXT_PRIMARY);

        line.add(iconLbl);
        line.add(labelLbl);
        line.add(valueLbl);
        return line;
    }

    // ── Save Profile ────────────────────────────────────────
    private void saveProfile() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom et l'email sont requis.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Format d'email invalide.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentUser.setName(name);
        currentUser.setEmail(email);

        if (userDAO.update(currentUser)) {
            JOptionPane.showMessageDialog(this, "✅ Profil mis à jour avec succès !", "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de la mise à jour.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Change Password ─────────────────────────────────────
    private void changePassword() {
        String currentPass = new String(currentPassField.getPassword());
        String newPass = new String(newPassField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont requis.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verify current password
        if (!currentPass.equals(currentUser.getPassword())) {
            JOptionPane.showMessageDialog(this, "Mot de passe actuel incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Le nouveau mot de passe doit faire minimum 6 caractères.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentUser.setPassword(newPass);
        if (userDAO.update(currentUser)) {
            JOptionPane.showMessageDialog(this, "✅ Mot de passe changé avec succès !", "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            currentPassField.setText("");
            newPassField.setText("");
            confirmPassField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "❌ Erreur lors du changement.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty())
            return "?";
        String[] parts = name.split("\\s+");
        if (parts.length >= 2) {
            return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
        return ("" + name.charAt(0)).toUpperCase();
    }
}
