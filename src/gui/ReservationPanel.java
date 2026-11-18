package gui;

import dao.ReservationDAO;
import dao.RoomDAO;
import gui.components.PlaceholderTextField;
import gui.components.RoundedButton;
import gui.components.RoundedPanel;
import model.Reservation;
import model.Room;
import model.User;
import util.QRCodeGenerator;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ReservationPanel — Search available rooms and make reservations.
 * Includes smart room suggestion and alternative slot recommendations.
 */
public class ReservationPanel extends JPanel {

    private User currentUser;
    private RoomDAO roomDAO = new RoomDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();

    // Search fields
    private PlaceholderTextField dateField;
    private JComboBox<String> startTimeCombo;
    private JComboBox<String> endTimeCombo;
    private JSpinner capacitySpinner;
    private PlaceholderTextField equipmentField;

    // Results
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private List<Room> searchResults;

    // Suggestion panel
    private JPanel suggestionPanel;

    public ReservationPanel(User user) {
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
        JLabel header = new JLabel("🔍  Reserve a Room");
        header.setFont(UIConstants.FONT_TITLE);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Search for available rooms and make a reservation");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(subtitle);
        container.add(Box.createVerticalStrut(25));

        // ── Search Card ─────────────────────────────────────
        RoundedPanel searchCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_MEDIUM);
        searchCard.setLayout(new BorderLayout());
        searchCard.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        searchCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        searchCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel searchForm = new JPanel(new GridBagLayout());
        searchForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date field
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        dateField = new PlaceholderTextField("Date (yyyy-MM-dd)");
        dateField.setText(today);
        dateField.setPreferredSize(new Dimension(150, 38));
        addFormField(searchForm, gbc, 0, "📅 Date:", dateField);

        // Start time
        String[] times = { "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00" };
        startTimeCombo = createStyledCombo(times);
        addFormField(searchForm, gbc, 1, "🕐 Start:", startTimeCombo);

        // End time
        endTimeCombo = createStyledCombo(times);
        endTimeCombo.setSelectedIndex(2); // Default 10:00
        addFormField(searchForm, gbc, 2, "🕐 End:", endTimeCombo);

        // Capacity
        capacitySpinner = new JSpinner(new SpinnerNumberModel(10, 1, 500, 5));
        capacitySpinner.setPreferredSize(new Dimension(100, 38));
        capacitySpinner.setFont(UIConstants.FONT_BODY);
        addFormField(searchForm, gbc, 3, "👥 Min Seats:", capacitySpinner);

        // Equipment
        equipmentField = new PlaceholderTextField("e.g. Projector");
        equipmentField.setPreferredSize(new Dimension(150, 38));
        addFormField(searchForm, gbc, 4, "🔧 Equipment:", equipmentField);

        // Search button
        RoundedButton searchBtn = new RoundedButton("🔍  Search Rooms");
        searchBtn.setGradient(UIConstants.GRAD_BLUE_START, UIConstants.GRAD_BLUE_END);
        searchBtn.setPreferredSize(new Dimension(160, 42));
        gbc.gridx = 5;
        gbc.gridy = 1;
        searchForm.add(searchBtn, gbc);

        // Smart Suggest button
        RoundedButton suggestBtn = new RoundedButton("🤖  Smart Suggest", UIConstants.ACCENT_PURPLE,
                UIConstants.ACCENT_PURPLE.brighter());
        suggestBtn.setPreferredSize(new Dimension(160, 42));
        gbc.gridy = 2;
        searchForm.add(suggestBtn, gbc);

        searchCard.add(searchForm, BorderLayout.CENTER);
        container.add(searchCard);
        container.add(Box.createVerticalStrut(20));

        // ── Suggestion Panel (hidden by default) ────────────
        suggestionPanel = new JPanel();
        suggestionPanel.setBackground(UIConstants.BG_DARK);
        suggestionPanel.setLayout(new BoxLayout(suggestionPanel, BoxLayout.Y_AXIS));
        suggestionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        suggestionPanel.setVisible(false);
        container.add(suggestionPanel);

        // ── Results Table ───────────────────────────────────
        JLabel resultsLabel = new JLabel("📋  Available Rooms");
        resultsLabel.setFont(UIConstants.FONT_SUBTITLE);
        resultsLabel.setForeground(UIConstants.TEXT_PRIMARY);
        resultsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(resultsLabel);
        container.add(Box.createVerticalStrut(10));

        String[] columns = { "ID", "Room Name", "Capacity", "Equipment", "Building", "Floor", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(tableModel);
        DashboardPanel.styleTable(resultsTable);

        JScrollPane tableScroll = new JScrollPane(resultsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1));
        tableScroll.getViewport().setBackground(UIConstants.BG_MEDIUM);
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableScroll.setPreferredSize(new Dimension(0, 250));
        container.add(tableScroll);
        container.add(Box.createVerticalStrut(15));

        // ── Reserve Button ──────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(UIConstants.BG_DARK);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton reserveBtn = new RoundedButton("📅  Reserve Selected Room", UIConstants.ACCENT_SUCCESS,
                UIConstants.ACCENT_SUCCESS.brighter());
        reserveBtn.setPreferredSize(new Dimension(240, 44));
        btnPanel.add(reserveBtn);

        container.add(btnPanel);

        // ── Event Handlers ──────────────────────────────────
        searchBtn.addActionListener(e -> performSearch());
        suggestBtn.addActionListener(e -> performSmartSuggest());
        reserveBtn.addActionListener(e -> reserveSelectedRoom());

        // Load all available rooms initially
        loadAllRooms();

        // Scroll pane
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setBackground(UIConstants.BG_DARK);
        mainScroll.getViewport().setBackground(UIConstants.BG_DARK);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    /**
     * Add a label + component pair to the search form grid.
     */
    private void addFormField(JPanel panel, GridBagConstraints gbc, int col, String label, JComponent field) {
        gbc.gridx = col;
        gbc.gridy = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(UIConstants.TEXT_SECONDARY);
        panel.add(lbl, gbc);

        gbc.gridy = 1;
        panel.add(field, gbc);
    }

    /**
     * Create a styled combo box matching the dark theme.
     */
    private JComboBox<String> createStyledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(UIConstants.FONT_BODY);
        combo.setBackground(UIConstants.BG_INPUT);
        combo.setForeground(UIConstants.TEXT_PRIMARY);
        combo.setPreferredSize(new Dimension(100, 38));
        combo.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        return combo;
    }

    /**
     * Load all rooms into the table.
     */
    private void loadAllRooms() {
        tableModel.setRowCount(0);
        searchResults = roomDAO.findAll();
        for (Room room : searchResults) {
            tableModel.addRow(new Object[] {
                    room.getId(), room.getName(), room.getCapacity(),
                    room.getEquipment(), room.getBuilding(), room.getFloorNumber(), room.getStatus()
            });
        }
    }

    /**
     * Search for available rooms based on the form criteria.
     */
    private void performSearch() {
        String date = dateField.getText().trim();
        String start = (String) startTimeCombo.getSelectedItem();
        String end = (String) endTimeCombo.getSelectedItem();
        int capacity = (Integer) capacitySpinner.getValue();
        String equipment = equipmentField.getText().trim();

        if (start.compareTo(end) >= 0) {
            showMessage("End time must be after start time.", UIConstants.ACCENT_WARNING);
            return;
        }

        tableModel.setRowCount(0);
        searchResults = roomDAO.searchAvailable(date, start, end, capacity, equipment);

        if (searchResults.isEmpty()) {
            showMessage("No rooms available for the selected criteria.", UIConstants.ACCENT_WARNING);
            // Show alternative slots
            showAlternativeSlots(date);
        } else {
            suggestionPanel.setVisible(false);
            for (Room room : searchResults) {
                tableModel.addRow(new Object[] {
                        room.getId(), room.getName(), room.getCapacity(),
                        room.getEquipment(), room.getBuilding(), room.getFloorNumber(), room.getStatus()
                });
            }
        }
    }

    /**
     * Smart room suggestion — finds the best room for the requirements.
     */
    private void performSmartSuggest() {
        String date = dateField.getText().trim();
        String start = (String) startTimeCombo.getSelectedItem();
        String end = (String) endTimeCombo.getSelectedItem();
        int capacity = (Integer) capacitySpinner.getValue();
        String equipment = equipmentField.getText().trim();

        Room suggested = roomDAO.suggestBestRoom(date, start, end, capacity, equipment);

        suggestionPanel.removeAll();
        suggestionPanel.setVisible(true);

        if (suggested != null) {
            RoundedPanel suggestCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_LIGHT);
            suggestCard.setGradient(new Color(34, 197, 94, 30), new Color(16, 185, 129, 30));
            suggestCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
            suggestCard.setLayout(new BoxLayout(suggestCard, BoxLayout.Y_AXIS));
            suggestCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            suggestCard.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel sugLabel = new JLabel("🤖  Smart Suggestion: " + suggested.getName());
            sugLabel.setFont(UIConstants.FONT_HEADING);
            sugLabel.setForeground(UIConstants.ACCENT_SUCCESS);

            JLabel detailLabel = new JLabel("Capacity: " + suggested.getCapacity()
                    + "  |  Equipment: " + suggested.getEquipment()
                    + "  |  " + suggested.getBuilding() + ", Floor " + suggested.getFloorNumber());
            detailLabel.setFont(UIConstants.FONT_BODY);
            detailLabel.setForeground(UIConstants.TEXT_SECONDARY);

            suggestCard.add(sugLabel);
            suggestCard.add(Box.createVerticalStrut(6));
            suggestCard.add(detailLabel);

            suggestionPanel.add(suggestCard);
            suggestionPanel.add(Box.createVerticalStrut(15));
        } else {
            JLabel noSuggest = new JLabel("🤖  No room matches all your requirements. Try adjusting your search.");
            noSuggest.setFont(UIConstants.FONT_BODY);
            noSuggest.setForeground(UIConstants.ACCENT_WARNING);
            suggestionPanel.add(noSuggest);
            suggestionPanel.add(Box.createVerticalStrut(15));

            // Show alternative slots
            showAlternativeSlots(dateField.getText().trim());
        }

        suggestionPanel.revalidate();
        suggestionPanel.repaint();
    }

    /**
     * Show alternative time slots when requested time is busy.
     */
    private void showAlternativeSlots(String date) {
        // Get all rooms and check their available slots
        List<Room> allRooms = roomDAO.findAll();
        suggestionPanel.removeAll();
        suggestionPanel.setVisible(true);

        JLabel altLabel = new JLabel("🧠  Suggested Alternative Time Slots:");
        altLabel.setFont(UIConstants.FONT_HEADING);
        altLabel.setForeground(UIConstants.ACCENT_INFO);
        altLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        suggestionPanel.add(altLabel);
        suggestionPanel.add(Box.createVerticalStrut(10));

        boolean found = false;
        for (Room room : allRooms) {
            if (!room.isAvailable())
                continue;
            List<String[]> slots = reservationDAO.suggestAlternativeSlots(room.getId(), date);
            if (!slots.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append(room.getName()).append(": ");
                for (String[] slot : slots) {
                    sb.append(slot[0]).append("-").append(slot[1]).append("  ");
                }
                JLabel slotLabel = new JLabel("   🕐  " + sb.toString());
                slotLabel.setFont(UIConstants.FONT_BODY);
                slotLabel.setForeground(UIConstants.TEXT_SECONDARY);
                slotLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                suggestionPanel.add(slotLabel);
                found = true;

                // Only show first 5 rooms
                if (suggestionPanel.getComponentCount() > 7)
                    break;
            }
        }

        if (!found) {
            JLabel noSlots = new JLabel("   No alternative slots available on this date.");
            noSlots.setFont(UIConstants.FONT_BODY);
            noSlots.setForeground(UIConstants.TEXT_MUTED);
            suggestionPanel.add(noSlots);
        }

        suggestionPanel.add(Box.createVerticalStrut(15));
        suggestionPanel.revalidate();
        suggestionPanel.repaint();
    }

    /**
     * Reserve the selected room from the table.
     */
    private void reserveSelectedRoom() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow < 0) {
            showMessage("Please select a room from the table first.", UIConstants.ACCENT_WARNING);
            return;
        }

        int roomId = (int) tableModel.getValueAt(selectedRow, 0);
        String roomName = (String) tableModel.getValueAt(selectedRow, 1);
        String date = dateField.getText().trim();
        String start = (String) startTimeCombo.getSelectedItem();
        String end = (String) endTimeCombo.getSelectedItem();

        // Get purpose
        String purpose = JOptionPane.showInputDialog(this,
                "Purpose for this reservation:",
                "Reserve " + roomName,
                JOptionPane.PLAIN_MESSAGE);

        if (purpose == null)
            return; // Cancelled
        if (purpose.trim().isEmpty())
            purpose = "General use";

        // Create reservation
        Reservation reservation = new Reservation(currentUser.getId(), roomId, date, start, end, purpose);
        int resId = reservationDAO.insert(reservation);

        if (resId > 0) {
            // Generate QR code
            String qrData = QRCodeGenerator.generateReservationCode(resId, currentUser.getId(), roomId, date);
            reservationDAO.updateQRCode(resId, qrData);

            // Show QR code
            BufferedImage qrImage = QRCodeGenerator.generateQRCode(qrData);
            showQRCodeDialog(qrImage, qrData, roomName, date, start, end);

            // Refresh search results
            performSearch();
        } else {
            showMessage("Reservation failed! Time slot conflict or database error.", UIConstants.ACCENT_DANGER);
        }
    }

    /**
     * Display the QR code in a dialog after successful reservation.
     */
    private void showQRCodeDialog(BufferedImage qrImage, String qrCode,
            String room, String date, String start, String end) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Reservation Confirmed", true);
        dialog.setSize(400, 520);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UIConstants.BG_MEDIUM);

        JPanel content = new JPanel();
        content.setBackground(UIConstants.BG_MEDIUM);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("✅  Reservation Confirmed!");
        title.setFont(UIConstants.FONT_SUBTITLE);
        title.setForeground(UIConstants.ACCENT_SUCCESS);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel details = new JLabel("<html><center>Room: " + room + "<br>"
                + date + " | " + start + " - " + end + "</center></html>");
        details.setFont(UIConstants.FONT_BODY);
        details.setForeground(UIConstants.TEXT_SECONDARY);
        details.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel qrLabel = new JLabel(new ImageIcon(qrImage));
        qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel codeLabel = new JLabel(qrCode);
        codeLabel.setFont(UIConstants.FONT_SMALL);
        codeLabel.setForeground(UIConstants.TEXT_MUTED);
        codeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundedButton closeBtn = new RoundedButton("Close");
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> dialog.dispose());

        content.add(title);
        content.add(Box.createVerticalStrut(10));
        content.add(details);
        content.add(Box.createVerticalStrut(20));
        content.add(qrLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(codeLabel);
        content.add(Box.createVerticalStrut(20));
        content.add(closeBtn);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    /**
     * Show a styled message using JOptionPane.
     */
    private void showMessage(String message, Color color) {
        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(UIConstants.FONT_BODY);
        msgLabel.setForeground(color);
        JOptionPane.showMessageDialog(this, msgLabel, "UniRoom",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
