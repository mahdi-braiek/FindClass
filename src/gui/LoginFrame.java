package gui;

import dao.UserDAO;
import gui.components.AppLogo;
import gui.components.PlaceholderPasswordField;
import gui.components.PlaceholderTextField;
import gui.components.RoundedButton;
import gui.components.RoundedPanel;
import model.User;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * LoginFrame — Beautiful login screen with gradient background,
 * glass-morphism card, and smooth animations.
 */
public class LoginFrame extends JFrame {

    private PlaceholderTextField emailField;
    private PlaceholderPasswordField passwordField;
    private JLabel statusLabel;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        initializeFrame();
        buildUI();
    }

    private void initializeFrame() {
        setTitle("FindClass — Login");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);
        setIconImage(AppLogo.createAppIcon());
    }

    private void buildUI() {
        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create(); // Create a copy to protect component state

                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background: Deep Ocean Theme
                GradientPaint gradient = new GradientPaint(
                        0, 0, UIConstants.BG_DARK,
                        getWidth(), getHeight(), new Color(10, 15, 30));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Decorative circles (Neon/Glow effects)
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                g2d.setColor(UIConstants.ACCENT_PRIMARY);
                g2d.fillOval(-100, -100, 500, 500);

                g2d.setColor(UIConstants.ACCENT_PURPLE);
                g2d.fillOval(getWidth() - 300, getHeight() - 300, 600, 600);

                g2d.setColor(UIConstants.ACCENT_SECONDARY);
                g2d.fillOval(getWidth() / 2 - 100, getHeight() / 2 - 100, 200, 200);

                g2d.dispose(); // Clean up the copy
            }
        };

        // ── Login Card ──────────────────────────────────────
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Glass-morphism background (Dark Slate with opacity)
                g2d.setColor(new Color(30, 41, 59, 240)); // Match BG_MEDIUM but transparent
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));

                // Subtle border
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 24, 24));

                g2d.dispose();
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(420, 520));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // ── Logo & Title ────────────────────────────────────
        AppLogo logo = new AppLogo(80, 80); // Bigger vector logo
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("FindClass");
        titleLabel.setFont(UIConstants.FONT_TITLE);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Smart University Room Reservation");
        subtitleLabel.setFont(UIConstants.FONT_SMALL);
        subtitleLabel.setForeground(UIConstants.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Form Fields ─────────────────────────────────────
        emailField = new PlaceholderTextField("Email address");
        emailField.setMaximumSize(new Dimension(340, 42));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new PlaceholderPasswordField("Password");
        passwordField.setMaximumSize(new Dimension(340, 42));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Login Button ────────────────────────────────────
        RoundedButton loginButton = new RoundedButton("Sign In");
        loginButton.setGradient(UIConstants.GRAD_BLUE_START, UIConstants.GRAD_PURPLE_END);
        loginButton.setMaximumSize(new Dimension(340, 44));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(this::performLogin);

        // Enter key shortcut
        passwordField.addActionListener(this::performLogin);

        // ── Status Label ────────────────────────────────────
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIConstants.FONT_SMALL);
        statusLabel.setForeground(UIConstants.ACCENT_DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Demo Credentials Info ───────────────────────────
        JLabel demoLabel = new JLabel("<html><center>Demo accounts:<br>" +
                "mehdi / mehdi (Admin)<br>" +
                "prof2@fst.com / teacher123 (Prof)<br>" +
                "mehdi@fst.com / student123 (Etudiant)</center></html>");
        demoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        demoLabel.setForeground(UIConstants.TEXT_MUTED);
        demoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Assemble Card ───────────────────────────────────
        // ── Assemble Card ───────────────────────────────────
        cardPanel.add(logo);
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(subtitleLabel);
        cardPanel.add(Box.createVerticalStrut(35));
        cardPanel.add(emailField);
        cardPanel.add(Box.createVerticalStrut(16));
        cardPanel.add(passwordField);
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(statusLabel);
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(loginButton);
        cardPanel.add(Box.createVerticalStrut(30));
        cardPanel.add(demoLabel);

        mainPanel.add(cardPanel);
        setContentPane(mainPanel);
    }

    /**
     * Handle login button click.
     */
    private void performLogin(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter email and password.");
            statusLabel.setForeground(UIConstants.ACCENT_WARNING);
            return;
        }

        // Authenticate with database
        User user = userDAO.authenticate(email, password);

        if (user != null) {
            statusLabel.setText("Welcome, " + user.getName() + "!");
            statusLabel.setForeground(UIConstants.ACCENT_SUCCESS);

            // Small delay for visual feedback, then open main frame
            Timer timer = new Timer(500, evt -> {
                dispose(); // Close login
                new MainFrame(user).setVisible(true);
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            statusLabel.setText("Invalid credentials. Please try again.");
            statusLabel.setForeground(UIConstants.ACCENT_DANGER);
            passwordField.setText("");
        }
    }
}
