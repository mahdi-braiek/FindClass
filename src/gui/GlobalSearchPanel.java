package gui;

import dao.SearchDAO;
import gui.components.PlaceholderTextField;
import gui.components.RoundedButton;
import gui.components.RoundedPanel;
import model.User;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * GlobalSearchPanel — Recherche globale instantanée à travers
 * salles, réservations, utilisateurs et emplois du temps.
 */
public class GlobalSearchPanel extends JPanel {

    private User currentUser;
    private SearchDAO searchDAO = new SearchDAO();
    private PlaceholderTextField searchField;
    private JPanel resultsContainer;
    private JLabel resultCountLabel;

    // Couleurs par type de résultat
    private static final Color COLOR_ROOM = new Color(99, 102, 241); // Indigo
    private static final Color COLOR_RESERVATION = new Color(34, 197, 94); // Green
    private static final Color COLOR_USER = new Color(245, 158, 11); // Amber
    private static final Color COLOR_TIMETABLE = new Color(236, 72, 153); // Pink

    public GlobalSearchPanel(User user) {
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
        JLabel header = new JLabel("🔍  Recherche Globale");
        header.setFont(UIConstants.FONT_TITLE);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Cherchez des salles, réservations, utilisateurs ou cours");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(subtitle);
        container.add(Box.createVerticalStrut(25));

        // ── Search Bar ──────────────────────────────────────
        RoundedPanel searchCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_MEDIUM);
        searchCard.setLayout(new BorderLayout(12, 0));
        searchCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        searchCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        searchCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        searchField = new PlaceholderTextField("Tapez pour rechercher...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(0, 42));

        RoundedButton searchBtn = new RoundedButton("Rechercher", UIConstants.ACCENT_PRIMARY,
                UIConstants.ACCENT_PRIMARY.brighter());
        searchBtn.setPreferredSize(new Dimension(140, 42));
        searchBtn.addActionListener(e -> performSearch());

        // Search on Enter key
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });

        searchCard.add(searchIcon, BorderLayout.WEST);
        searchCard.add(searchField, BorderLayout.CENTER);
        searchCard.add(searchBtn, BorderLayout.EAST);

        container.add(searchCard);
        container.add(Box.createVerticalStrut(20));

        // ── Result Count ────────────────────────────────────
        resultCountLabel = new JLabel("");
        resultCountLabel.setFont(UIConstants.FONT_BODY);
        resultCountLabel.setForeground(UIConstants.TEXT_MUTED);
        resultCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(resultCountLabel);
        container.add(Box.createVerticalStrut(15));

        // ── Results Container ───────────────────────────────
        resultsContainer = new JPanel();
        resultsContainer.setBackground(UIConstants.BG_DARK);
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        resultsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(resultsContainer);

        // ── Main Scroll ─────────────────────────────────────
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setBackground(UIConstants.BG_DARK);
        mainScroll.getViewport().setBackground(UIConstants.BG_DARK);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty())
            return;

        resultsContainer.removeAll();

        List<Object[]> results = searchDAO.globalSearch(query);
        resultCountLabel.setText(results.size() + " résultat(s) trouvé(s) pour \"" + query + "\"");

        if (results.isEmpty()) {
            JLabel noResult = new JLabel("😕  Aucun résultat trouvé");
            noResult.setFont(UIConstants.FONT_SUBTITLE);
            noResult.setForeground(UIConstants.TEXT_MUTED);
            noResult.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultsContainer.add(noResult);
        } else {
            for (Object[] row : results) {
                resultsContainer.add(createResultCard(row));
                resultsContainer.add(Box.createVerticalStrut(8));
            }
        }

        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    /**
     * Crée une carte de résultat stylée.
     * row = [type, id, title, detail1, detail2, detail3]
     */
    private JPanel createResultCard(Object[] row) {
        String type = (String) row[0];
        String title = String.valueOf(row[2]);
        String detail1 = String.valueOf(row[3]);
        String detail2 = String.valueOf(row[4]);
        String detail3 = String.valueOf(row[5]);

        Color typeColor;
        String typeIcon;
        String typeLabel;
        switch (type) {
            case "ROOM":
                typeColor = COLOR_ROOM;
                typeIcon = "🏠";
                typeLabel = "Salle";
                break;
            case "RESERVATION":
                typeColor = COLOR_RESERVATION;
                typeIcon = "📅";
                typeLabel = "Réservation";
                break;
            case "USER":
                typeColor = COLOR_USER;
                typeIcon = "👤";
                typeLabel = "Utilisateur";
                break;
            case "TIMETABLE":
                typeColor = COLOR_TIMETABLE;
                typeIcon = "🗓";
                typeLabel = "Emploi du temps";
                break;
            default:
                typeColor = UIConstants.TEXT_MUTED;
                typeIcon = "📄";
                typeLabel = type;
        }

        RoundedPanel card = new RoundedPanel(12, UIConstants.BG_MEDIUM);
        card.setLayout(new BorderLayout(15, 0));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Left: Type indicator
        JPanel indicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(typeColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
        };
        indicator.setOpaque(false);
        indicator.setPreferredSize(new Dimension(4, 0));
        card.add(indicator, BorderLayout.WEST);

        // Center: Content
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(typeIcon + "  " + title);
        titleLabel.setFont(UIConstants.FONT_HEADING);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel detailLabel = new JLabel(detail1 + (detail2.isEmpty() ? "" : "  ·  " + detail2));
        detailLabel.setFont(UIConstants.FONT_SMALL);
        detailLabel.setForeground(UIConstants.TEXT_SECONDARY);
        detailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(detailLabel);

        card.add(content, BorderLayout.CENTER);

        // Right: Type badge
        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(typeColor.getRed(), typeColor.getGreen(), typeColor.getBlue(), 30));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        badge.setOpaque(false);
        badge.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 6));
        badge.setPreferredSize(new Dimension(120, 30));

        JLabel badgeText = new JLabel(typeLabel);
        badgeText.setFont(UIConstants.FONT_SMALL);
        badgeText.setForeground(typeColor);
        badge.add(badgeText);

        // Status/extra info
        if (!detail3.isEmpty()) {
            JLabel statusLabel = new JLabel(detail3);
            statusLabel.setFont(UIConstants.FONT_SMALL);
            statusLabel.setForeground(UIConstants.TEXT_MUTED);
            badge.add(statusLabel);
        }

        card.add(badge, BorderLayout.EAST);

        return card;
    }
}
