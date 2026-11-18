package gui;

import gui.components.AppLogo;
import gui.components.RoundedButton;
import model.User;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MainFrame — The main application window with sidebar navigation.
 * Displays different panels based on the logged-in user's role.
 */
public class MainFrame extends JFrame {

    private User currentUser;
    private JPanel contentPanel; // Right side: switches between panels
    private JPanel sidebarContainer; // Left side: navigation
    private int activeNavIndex = 0;
    private String[] navTitles;
    private String[] navIcons;

    public MainFrame(User user) {
        this.currentUser = user;
        initializeFrame();
        buildUI();
    }

    private void initializeFrame() {
        setTitle("FindClass — University Room Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1100, 700));
        setIconImage(AppLogo.createAppIcon());
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.BG_DARK);

        // ── Build Sidebar ────────────────────────────────────
        setupNavigationItems();
        sidebarContainer = buildSidebar();
        mainPanel.add(sidebarContainer, BorderLayout.WEST);

        // ── Content Area ─────────────────────────────────────
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIConstants.BG_DARK);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Show default panel (Dashboard)
        switchToPanel(0);
    }

    /**
     * Configure navigation items based on user role.
     */
    private void setupNavigationItems() {
        if (currentUser.isAdmin()) {
            navTitles = new String[] { "Dashboard", "Rooms", "Reservations", "Emploi du Temps", "Gérer EDT",
                    "Statistics", "Users", "QR Validate", "Recherche", "Paramètres" };
            navIcons = new String[] { "📊", "🏠", "📅", "🗓", "✏️", "📈", "👥", "📱", "🔍", "⚙️" };
        } else if (currentUser.isTeacher()) {
            navTitles = new String[] { "Dashboard", "Reserve Room", "My Reservations", "Emploi du Temps",
                    "Statistics", "Recherche", "Paramètres" };
            navIcons = new String[] { "📊", "🔍", "📋", "🗓", "📈", "🔎", "⚙️" };
        } else {
            navTitles = new String[] { "Dashboard", "Reserve Room", "My Reservations", "Emploi du Temps", "Recherche",
                    "Paramètres" };
            navIcons = new String[] { "📊", "🔍", "📋", "🗓", "🔎", "⚙️" };
        }
    }

    /**
     * Build the sidebar panel with navigation items.
     */
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sidebar.setBackground(UIConstants.SIDEBAR_BG);
        sidebar.setLayout(new BorderLayout());

        // ── Top: Logo (Modern Custom) ───────────────────────
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        logoPanel.setBackground(UIConstants.SIDEBAR_BG);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(35, 20, 25, 20));

        // 1. Logo Vectoriel
        AppLogo logoIcon = new AppLogo(42, 42); // 42x42 px icon

        // 2. Textes (Titre + Tagline)
        JPanel textContainer = new JPanel();
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));
        textContainer.setOpaque(false);

        JLabel logoText = new JLabel("FindClass");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Plus gros et gras
        logoText.setForeground(Color.WHITE); // Blanc pur sur fond foncé

        JLabel tagline = new JLabel("Smart Campus");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tagline.setForeground(UIConstants.ACCENT_PRIMARY); // Cyan néon

        textContainer.add(logoText);
        textContainer.add(Box.createVerticalStrut(2)); // Espacement fin
        textContainer.add(tagline);

        logoPanel.add(logoIcon);
        logoPanel.add(textContainer);

        sidebar.add(logoPanel, BorderLayout.NORTH);

        // ── Center: Navigation items ────────────────────────
        JPanel navPanel = new JPanel();
        navPanel.setBackground(UIConstants.SIDEBAR_BG);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(15, 8, 15, 8));

        for (int i = 0; i < navTitles.length; i++) {
            navPanel.add(createNavItem(i, navIcons[i], navTitles[i]));
            navPanel.add(Box.createVerticalStrut(2));
        }

        sidebar.add(navPanel, BorderLayout.CENTER);

        sidebar.add(navPanel, BorderLayout.CENTER);

        // ── Bottom: User info + Logout (Glassmorphism effect) ──────────────────────
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(30, 41, 59, 200)); // Semi-transparent Slate 800
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 20)), // Top border light
                BorderFactory.createEmptyBorder(20, 24, 25, 24)));

        // User Avatar Circle (Gradient)
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UIConstants.ACCENT_SECONDARY, 36, 36,
                        UIConstants.ACCENT_PURPLE);
                g2d.setPaint(gp);
                g2d.fillOval(0, 0, 36, 36);

                // Initiale
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String initial = currentUser.getName().substring(0, 1).toUpperCase();
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(initial, 18 - fm.stringWidth(initial) / 2, 24);
            }
        };
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setMaximumSize(new Dimension(36, 36));
        avatar.setOpaque(false);
        avatar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLabel = new JLabel(currentUser.getName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel(currentUser.getRole());
        roleLabel.setFont(UIConstants.FONT_SMALL);
        roleLabel.setForeground(UIConstants.TEXT_MUTED);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Layout user info
        JPanel userRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        userRow.setOpaque(false);
        userRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setOpaque(false);
        textCol.add(userLabel);
        textCol.add(roleLabel);

        userRow.add(avatar);
        userRow.add(textCol);

        bottomPanel.add(userRow);
        bottomPanel.add(Box.createVerticalStrut(20));

        // Logout button (Outline style)
        RoundedButton logoutBtn = new RoundedButton("⏻  Se Déconnecter", UIConstants.ACCENT_DANGER,
                UIConstants.ACCENT_DANGER.darker());
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        bottomPanel.add(logoutBtn);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    /**
     * Create a single navigation item panel.
     */
    private JPanel createNavItem(int index, String icon, String title) {
        JPanel item = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.dispose();
            }
        };
        item.setOpaque(false);
        item.setBackground(index == activeNavIndex ? UIConstants.SIDEBAR_ACTIVE : UIConstants.SIDEBAR_BG);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        item.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 16, 46));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Active indicator
        if (index == activeNavIndex) {
            JPanel indicator = new JPanel();
            indicator.setOpaque(true);
            indicator.setBackground(UIConstants.ACCENT_PRIMARY);
            indicator.setPreferredSize(new Dimension(3, 46));
            item.add(indicator, BorderLayout.WEST);
        }

        JLabel label = new JLabel("   " + icon + "   " + title);
        label.setFont(UIConstants.FONT_SIDEBAR);
        label.setForeground(index == activeNavIndex ? UIConstants.ACCENT_PRIMARY : UIConstants.TEXT_SECONDARY);
        item.add(label, BorderLayout.CENTER);

        // Mouse events
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (index != activeNavIndex) {
                    item.setBackground(UIConstants.BG_HOVER);
                    item.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (index != activeNavIndex) {
                    item.setBackground(UIConstants.SIDEBAR_BG);
                    item.repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                switchToPanel(index);
            }
        });

        return item;
    }

    /**
     * Switch the main content area to the selected panel.
     */
    private void switchToPanel(int index) {
        activeNavIndex = index;

        // Rebuild sidebar to update active states
        sidebarContainer.removeAll();
        JPanel newSidebar = buildSidebar();
        // Copy children from new sidebar
        for (Component c : newSidebar.getComponents()) {
            sidebarContainer.add(c, ((BorderLayout) newSidebar.getLayout()).getConstraints(c));
        }
        sidebarContainer.revalidate();
        sidebarContainer.repaint();

        // Switch content panel
        contentPanel.removeAll();

        JPanel panel;
        String title = navTitles[index];

        switch (title) {
            case "Dashboard":
                panel = new DashboardPanel(currentUser);
                break;
            case "Reserve Room":
                panel = new ReservationPanel(currentUser);
                break;
            case "My Reservations":
                panel = new MyReservationsPanel(currentUser);
                break;
            case "Rooms":
                panel = new RoomManagementPanel(currentUser);
                break;
            case "Reservations":
                panel = new AdminReservationsPanel(currentUser);
                break;
            case "Statistics":
                panel = new StatisticsPanel(currentUser);
                break;
            case "Users":
                panel = new UserManagementPanel(currentUser);
                break;
            case "QR Validate":
                panel = new QRValidationPanel(currentUser);
                break;
            case "Emploi du Temps":
                panel = new TimetableViewPanel(currentUser);
                break;
            case "Gérer EDT":
                panel = new TimetableManagementPanel(currentUser);
                break;
            case "Recherche":
                panel = new GlobalSearchPanel(currentUser);
                break;
            case "Paramètres":
                panel = new SettingsPanel(currentUser);
                break;
            default:
                panel = new DashboardPanel(currentUser);
        }

        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
