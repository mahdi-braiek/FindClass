package gui;

import dao.ReservationDAO;
import dao.RoomDAO;
import dao.UserDAO;
import gui.components.RoundedPanel;
import model.Reservation;
import model.User;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * DashboardPanel — Shows overview statistics with beautiful cards
 * and recent activity. Content adapts to the user's role.
 */
public class DashboardPanel extends JPanel {

    private User currentUser;
    private RoomDAO roomDAO = new RoomDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();
    private UserDAO userDAO = new UserDAO();

    public DashboardPanel(User user) {
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
        JLabel header = new JLabel("Welcome back, " + currentUser.getName() + " 👋");
        header.setFont(UIConstants.FONT_TITLE);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Here's what's happening today");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        container.add(header);
        container.add(Box.createVerticalStrut(4));
        container.add(subtitle);
        container.add(Box.createVerticalStrut(30));

        // ── Statistics Cards ────────────────────────────────
        JPanel cardsRow = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsRow.setBackground(UIConstants.BG_DARK);
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        cardsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (currentUser.isAdmin()) {
            cardsRow.add(createStatCard("Total Rooms", String.valueOf(roomDAO.countAll()),
                    "🏠", UIConstants.GRAD_BLUE_START, UIConstants.GRAD_BLUE_END));
            cardsRow.add(createStatCard("Available", String.valueOf(roomDAO.countAvailable()),
                    "✅", UIConstants.GRAD_GREEN_START, UIConstants.GRAD_GREEN_END));
            cardsRow.add(createStatCard("Today's Bookings", String.valueOf(reservationDAO.countToday()),
                    "📅", UIConstants.GRAD_PURPLE_START, UIConstants.GRAD_PURPLE_END));
            cardsRow.add(createStatCard("Total Users", String.valueOf(
                    userDAO.countByRole("STUDENT") + userDAO.countByRole("TEACHER") + userDAO.countByRole("ADMIN")),
                    "👥", UIConstants.GRAD_ORANGE_START, UIConstants.GRAD_ORANGE_END));
        } else {
            cardsRow.setLayout(new GridLayout(1, 3, 20, 0));
            cardsRow.add(
                    createStatCard("My Reservations", String.valueOf(reservationDAO.countByUser(currentUser.getId())),
                            "📋", UIConstants.GRAD_BLUE_START, UIConstants.GRAD_BLUE_END));
            cardsRow.add(createStatCard("Available Rooms", String.valueOf(roomDAO.countAvailable()),
                    "🏠", UIConstants.GRAD_GREEN_START, UIConstants.GRAD_GREEN_END));
            cardsRow.add(createStatCard("Today's Bookings", String.valueOf(reservationDAO.countToday()),
                    "📅", UIConstants.GRAD_PURPLE_START, UIConstants.GRAD_PURPLE_END));
        }

        container.add(cardsRow);
        container.add(Box.createVerticalStrut(30));

        // ── Recent Reservations Table ───────────────────────
        JLabel recentLabel = new JLabel("📋  Recent Reservations");
        recentLabel.setFont(UIConstants.FONT_SUBTITLE);
        recentLabel.setForeground(UIConstants.TEXT_PRIMARY);
        recentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(recentLabel);
        container.add(Box.createVerticalStrut(15));

        // Build table
        JPanel tableCard = createRecentReservationsTable();
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(tableCard);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBackground(UIConstants.BG_DARK);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIConstants.BG_DARK);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Create a gradient statistics card with icon, value, and label.
     */
    private JPanel createStatCard(String label, String value, String icon,
            Color gradStart, Color gradEnd) {
        RoundedPanel card = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_LIGHT);
        card.setGradient(gradStart, gradEnd);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(UIConstants.FONT_BODY);
        nameLabel.setForeground(new Color(255, 255, 255, 200));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(iconLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(nameLabel);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Create the recent reservations table inside a styled card.
     */
    private JPanel createRecentReservationsTable() {
        RoundedPanel tableCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_MEDIUM);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        String[] columns = { "ID", "Room", "User", "Date", "Time", "Status" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load data
        List<Reservation> reservations;
        if (currentUser.isAdmin()) {
            reservations = reservationDAO.findAll();
        } else {
            reservations = reservationDAO.findByUserId(currentUser.getId());
        }

        // Show max 10 recent
        int count = Math.min(reservations.size(), 10);
        for (int i = 0; i < count; i++) {
            Reservation r = reservations.get(i);
            model.addRow(new Object[] {
                    r.getId(),
                    r.getRoomName(),
                    r.getUserName(),
                    r.getReservationDate(),
                    r.getStartTime() + " - " + r.getEndTime(),
                    r.getStatus()
            });
        }

        JTable table = new JTable(model);
        styleTable(table);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(null);
        tableScroll.getViewport().setBackground(UIConstants.BG_MEDIUM);

        tableCard.add(tableScroll, BorderLayout.CENTER);
        return tableCard;
    }

    /**
     * Apply dark theme styling to a JTable.
     */
    public static void styleTable(JTable table) {
        table.setBackground(UIConstants.BG_MEDIUM);
        table.setForeground(UIConstants.TEXT_PRIMARY);
        table.setFont(UIConstants.FONT_TABLE);
        table.setRowHeight(42);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(UIConstants.ACCENT_PRIMARY.darker());
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(UIConstants.BG_LIGHT);
        header.setForeground(UIConstants.TEXT_SECONDARY);
        header.setFont(UIConstants.FONT_TABLE_HEAD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 44));

        // Custom cell renderer for alternating rows + status colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? UIConstants.BG_MEDIUM : UIConstants.BG_TABLE_ROW);

                    // Color status column
                    if (value != null) {
                        String val = value.toString();
                        if (val.equals("CONFIRMED")) {
                            c.setForeground(UIConstants.ACCENT_SUCCESS);
                        } else if (val.equals("CANCELLED")) {
                            c.setForeground(UIConstants.ACCENT_DANGER);
                        } else if (val.equals("COMPLETED")) {
                            c.setForeground(UIConstants.ACCENT_INFO);
                        } else if (val.equals("PENDING")) {
                            c.setForeground(UIConstants.ACCENT_WARNING);
                        } else if (val.equals("AVAILABLE")) {
                            c.setForeground(UIConstants.ACCENT_SUCCESS);
                        } else if (val.equals("MAINTENANCE")) {
                            c.setForeground(UIConstants.ACCENT_WARNING);
                        } else if (val.equals("CLOSED")) {
                            c.setForeground(UIConstants.ACCENT_DANGER);
                        } else {
                            c.setForeground(UIConstants.TEXT_PRIMARY);
                        }
                    }
                }

                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });
    }
}
