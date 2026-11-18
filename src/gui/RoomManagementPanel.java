package gui;

import dao.RoomDAO;
import gui.components.PlaceholderTextField;
import gui.components.RoundedButton;
import gui.components.RoundedPanel;
import model.Room;
import model.User;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * RoomManagementPanel — Admin panel for CRUD operations on rooms.
 */
public class RoomManagementPanel extends JPanel {

    private User currentUser;
    private RoomDAO roomDAO = new RoomDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    // Form fields
    private PlaceholderTextField nameField, equipmentField, buildingField;
    private JSpinner capacitySpinner, floorSpinner;
    private JComboBox<String> statusCombo;

    public RoomManagementPanel(User user) {
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

        // Header
        JLabel header = new JLabel("🏠  Room Management");
        header.setFont(UIConstants.FONT_TITLE);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Add, edit, and manage university rooms");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(subtitle);
        container.add(Box.createVerticalStrut(25));

        // ── Form Card ───────────────────────────────────────
        RoundedPanel formCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_MEDIUM);
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        formCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Labels
        nameField = new PlaceholderTextField("Room name");
        nameField.setPreferredSize(new Dimension(150, 36));
        addField(form, gbc, 0, "Name:", nameField);

        capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 1000, 5));
        capacitySpinner.setPreferredSize(new Dimension(80, 36));
        addField(form, gbc, 1, "Capacity:", capacitySpinner);

        equipmentField = new PlaceholderTextField("Projector, Computers...");
        equipmentField.setPreferredSize(new Dimension(200, 36));
        addField(form, gbc, 2, "Equipment:", equipmentField);

        statusCombo = new JComboBox<>(new String[] { "AVAILABLE", "MAINTENANCE", "CLOSED" });
        statusCombo.setFont(UIConstants.FONT_BODY);
        statusCombo.setPreferredSize(new Dimension(120, 36));
        addField(form, gbc, 3, "Status:", statusCombo);

        buildingField = new PlaceholderTextField("Building A");
        buildingField.setPreferredSize(new Dimension(120, 36));
        addField(form, gbc, 4, "Building:", buildingField);

        floorSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));
        floorSpinner.setPreferredSize(new Dimension(60, 36));
        addField(form, gbc, 5, "Floor:", floorSpinner);

        // Buttons
        gbc.gridx = 6;
        gbc.gridy = 1;
        RoundedButton addBtn = new RoundedButton("➕  Add", UIConstants.ACCENT_SUCCESS,
                UIConstants.ACCENT_SUCCESS.brighter());
        addBtn.setPreferredSize(new Dimension(100, 38));
        form.add(addBtn, gbc);

        gbc.gridy = 2;
        RoundedButton updateBtn = new RoundedButton("✏️  Update", UIConstants.ACCENT_WARNING,
                UIConstants.ACCENT_WARNING.brighter());
        updateBtn.setPreferredSize(new Dimension(100, 38));
        form.add(updateBtn, gbc);

        formCard.add(form, BorderLayout.CENTER);
        container.add(formCard);
        container.add(Box.createVerticalStrut(20));

        // ── Table ───────────────────────────────────────────
        String[] columns = { "ID", "Name", "Capacity", "Equipment", "Status", "Building", "Floor" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        DashboardPanel.styleTable(table);

        loadRooms();

        // Select row → fill form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                fillFormFromTable();
        });

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1));
        tableScroll.getViewport().setBackground(UIConstants.BG_MEDIUM);
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(tableScroll);
        container.add(Box.createVerticalStrut(15));

        // ── Bottom Buttons ──────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(UIConstants.BG_DARK);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton deleteBtn = new RoundedButton("🗑  Delete", UIConstants.ACCENT_DANGER,
                UIConstants.ACCENT_DANGER.brighter());
        deleteBtn.setPreferredSize(new Dimension(130, 40));

        RoundedButton refreshBtn = new RoundedButton("🔄  Refresh");
        refreshBtn.setPreferredSize(new Dimension(130, 40));

        RoundedButton clearBtn = new RoundedButton("🧹  Clear Form", UIConstants.BG_HOVER, UIConstants.BG_LIGHT);
        clearBtn.setPreferredSize(new Dimension(130, 40));

        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(clearBtn);
        container.add(btnPanel);

        // ── Event Handlers ──────────────────────────────────
        addBtn.addActionListener(e -> addRoom());
        updateBtn.addActionListener(e -> updateRoom());
        deleteBtn.addActionListener(e -> deleteRoom());
        refreshBtn.addActionListener(e -> loadRooms());
        clearBtn.addActionListener(e -> clearForm());

        // Scroll
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setBackground(UIConstants.BG_DARK);
        mainScroll.getViewport().setBackground(UIConstants.BG_DARK);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int col, String label, JComponent field) {
        gbc.gridx = col;
        gbc.gridy = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(UIConstants.TEXT_SECONDARY);
        panel.add(lbl, gbc);
        gbc.gridy = 1;
        panel.add(field, gbc);
    }

    private void loadRooms() {
        tableModel.setRowCount(0);
        List<Room> rooms = roomDAO.findAll();
        for (Room r : rooms) {
            tableModel.addRow(new Object[] {
                    r.getId(), r.getName(), r.getCapacity(),
                    r.getEquipment(), r.getStatus(), r.getBuilding(), r.getFloorNumber()
            });
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            nameField.setText((String) tableModel.getValueAt(row, 1));
            capacitySpinner.setValue(tableModel.getValueAt(row, 2));
            equipmentField.setText((String) tableModel.getValueAt(row, 3));
            statusCombo.setSelectedItem(tableModel.getValueAt(row, 4));
            buildingField.setText((String) tableModel.getValueAt(row, 5));
            floorSpinner.setValue(tableModel.getValueAt(row, 6));
        }
    }

    private void addRoom() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Room room = new Room(name, (Integer) capacitySpinner.getValue(),
                equipmentField.getText().trim(), (String) statusCombo.getSelectedItem(),
                buildingField.getText().trim(), (Integer) floorSpinner.getValue());

        if (roomDAO.insert(room)) {
            JOptionPane.showMessageDialog(this, "Room added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRooms();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add room.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRoom() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a room to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        Room room = new Room(id, nameField.getText().trim(), (Integer) capacitySpinner.getValue(),
                equipmentField.getText().trim(), (String) statusCombo.getSelectedItem(),
                buildingField.getText().trim(), (Integer) floorSpinner.getValue());

        if (roomDAO.update(room)) {
            JOptionPane.showMessageDialog(this, "Room updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRooms();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update room.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRoom() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a room to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete room \"" + tableModel.getValueAt(row, 1) + "\"?\nThis will also delete all its reservations.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(row, 0);
            if (roomDAO.delete(id)) {
                JOptionPane.showMessageDialog(this, "Room deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRooms();
                clearForm();
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        capacitySpinner.setValue(30);
        equipmentField.setText("");
        statusCombo.setSelectedIndex(0);
        buildingField.setText("");
        floorSpinner.setValue(0);
        table.clearSelection();
    }
}
