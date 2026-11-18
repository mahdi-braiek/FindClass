package gui;

import dao.UserDAO;
import gui.components.PlaceholderTextField;
import gui.components.RoundedButton;
import gui.components.RoundedPanel;
import model.User;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * UserManagementPanel — Admin panel for managing system users.
 * Allows creating, editing, and deleting users with role assignment.
 */
public class UserManagementPanel extends JPanel {

    private User currentUser;
    private UserDAO userDAO = new UserDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    // Form fields
    private PlaceholderTextField nameField, emailField, passwordField;
    private JComboBox<String> roleCombo;

    public UserManagementPanel(User user) {
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
        JLabel header = new JLabel("👥  User Management");
        header.setFont(UIConstants.FONT_TITLE);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Manage students, teachers, and administrators");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(subtitle);
        container.add(Box.createVerticalStrut(25));

        // ── Form Card ───────────────────────────────────────
        RoundedPanel formCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_MEDIUM);
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        formCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new PlaceholderTextField("Full Name");
        nameField.setPreferredSize(new Dimension(160, 36));
        addField(form, gbc, 0, "Name:", nameField);

        emailField = new PlaceholderTextField("email@example.com");
        emailField.setPreferredSize(new Dimension(180, 36));
        addField(form, gbc, 1, "Email:", emailField);

        passwordField = new PlaceholderTextField("Password");
        passwordField.setPreferredSize(new Dimension(140, 36));
        addField(form, gbc, 2, "Password:", passwordField);

        roleCombo = new JComboBox<>(new String[] { "STUDENT", "TEACHER", "ADMIN" });
        roleCombo.setFont(UIConstants.FONT_BODY);
        roleCombo.setPreferredSize(new Dimension(120, 36));
        addField(form, gbc, 3, "Role:", roleCombo);

        // Buttons
        gbc.gridx = 4;
        gbc.gridy = 1;
        RoundedButton addBtn = new RoundedButton("➕  Add", UIConstants.ACCENT_SUCCESS,
                UIConstants.ACCENT_SUCCESS.brighter());
        addBtn.setPreferredSize(new Dimension(110, 38));
        form.add(addBtn, gbc);

        gbc.gridy = 2;
        RoundedButton updateBtn = new RoundedButton("✏️  Update", UIConstants.ACCENT_WARNING,
                UIConstants.ACCENT_WARNING.brighter());
        updateBtn.setPreferredSize(new Dimension(110, 38));
        form.add(updateBtn, gbc);

        formCard.add(form, BorderLayout.CENTER);
        container.add(formCard);
        container.add(Box.createVerticalStrut(20));

        // ── Table ───────────────────────────────────────────
        String[] columns = { "ID", "Name", "Email", "Role" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        DashboardPanel.styleTable(table);

        loadUsers();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                fillForm();
        });

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1));
        tableScroll.getViewport().setBackground(UIConstants.BG_MEDIUM);
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(tableScroll);
        container.add(Box.createVerticalStrut(15));

        // Bottom buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(UIConstants.BG_DARK);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton deleteBtn = new RoundedButton("🗑  Delete", UIConstants.ACCENT_DANGER,
                UIConstants.ACCENT_DANGER.brighter());
        deleteBtn.setPreferredSize(new Dimension(130, 40));

        RoundedButton refreshBtn = new RoundedButton("🔄  Refresh");
        refreshBtn.setPreferredSize(new Dimension(130, 40));

        RoundedButton clearBtn = new RoundedButton("🧹  Clear", UIConstants.BG_HOVER, UIConstants.BG_LIGHT);
        clearBtn.setPreferredSize(new Dimension(130, 40));

        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(clearBtn);
        container.add(btnPanel);

        // Events
        addBtn.addActionListener(e -> addUser());
        updateBtn.addActionListener(e -> updateUser());
        deleteBtn.addActionListener(e -> deleteUser());
        refreshBtn.addActionListener(e -> loadUsers());
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

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userDAO.findAll();
        for (User u : users) {
            tableModel.addRow(new Object[] { u.getId(), u.getName(), u.getEmail(), u.getRole() });
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            nameField.setText((String) tableModel.getValueAt(row, 1));
            emailField.setText((String) tableModel.getValueAt(row, 2));
            passwordField.setText(""); // Don't show password
            roleCombo.setSelectedItem(tableModel.getValueAt(row, 3));
        }
    }

    private void addUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = passwordField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User(name, email, pass, role);
        if (userDAO.insert(user)) {
            JOptionPane.showMessageDialog(this, "User created!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUsers();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed. Email might already exist.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        User user = userDAO.findById(id);
        if (user == null)
            return;

        user.setName(nameField.getText().trim());
        user.setEmail(emailField.getText().trim());
        if (!passwordField.getText().trim().isEmpty()) {
            user.setPassword(passwordField.getText().trim());
        }
        user.setRole((String) roleCombo.getSelectedItem());

        if (userDAO.update(user)) {
            JOptionPane.showMessageDialog(this, "User updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUsers();
        }
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        if (id == currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "Cannot delete your own account!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete user \"" + tableModel.getValueAt(row, 1) + "\"?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.delete(id)) {
                JOptionPane.showMessageDialog(this, "User deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
                clearForm();
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        roleCombo.setSelectedIndex(0);
        table.clearSelection();
    }
}
