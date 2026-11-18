package gui;

import dao.RoomDAO;
import dao.TimetableDAO;
import dao.UserDAO;
import gui.components.PlaceholderTextField;
import gui.components.RoundedButton;
import gui.components.RoundedPanel;
import model.Room;
import model.Timetable;
import model.User;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * TimetableManagementPanel — Panneau d'administration des emplois du temps.
 * Permet à l'admin d'ajouter, modifier et supprimer des séances dans le
 * planning.
 */
public class TimetableManagementPanel extends JPanel {

    private User currentUser;
    private TimetableDAO timetableDAO = new TimetableDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private UserDAO userDAO = new UserDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    // Form fields
    private JComboBox<String> dayCombo, typeCombo;
    private JComboBox<ComboItem> teacherCombo, roomCombo;
    private PlaceholderTextField subjectField, groupField, semesterField, colorField;
    private JComboBox<String> startHourCombo, endHourCombo;

    public TimetableManagementPanel(User user) {
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
        JLabel header = new JLabel("🗓  Gestion des Emplois du Temps");
        header.setFont(UIConstants.FONT_TITLE);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Ajouter, modifier et supprimer des séances");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(subtitle);
        container.add(Box.createVerticalStrut(25));

        // ── Form Card ───────────────────────────────────────
        RoundedPanel formCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_MEDIUM);
        formCard.setLayout(new BorderLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        formCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Day, Start, End, Type
        dayCombo = new JComboBox<>(new String[] { "LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI" });
        dayCombo.setFont(UIConstants.FONT_BODY);
        addField(form, gbc, 0, 0, "Jour :", dayCombo);

        String[] hours = new String[11];
        for (int h = 8; h <= 18; h++) {
            hours[h - 8] = String.format("%02d:00", h);
        }

        startHourCombo = new JComboBox<>(hours);
        startHourCombo.setFont(UIConstants.FONT_BODY);
        addField(form, gbc, 1, 0, "Début :", startHourCombo);

        endHourCombo = new JComboBox<>(hours);
        endHourCombo.setFont(UIConstants.FONT_BODY);
        endHourCombo.setSelectedIndex(2); // default 10:00
        addField(form, gbc, 2, 0, "Fin :", endHourCombo);

        typeCombo = new JComboBox<>(new String[] { "COURS", "TD", "TP", "EXAM" });
        typeCombo.setFont(UIConstants.FONT_BODY);
        addField(form, gbc, 3, 0, "Type :", typeCombo);

        // Row 2: Subject, Teacher, Room
        subjectField = new PlaceholderTextField("Nom de la matière");
        subjectField.setPreferredSize(new Dimension(180, 36));
        addField(form, gbc, 0, 2, "Matière :", subjectField);

        // Combo enseignants
        List<User> teachers = userDAO.findByRole("TEACHER");
        teacherCombo = new JComboBox<>();
        for (User t : teachers) {
            teacherCombo.addItem(new ComboItem(t.getId(), t.getName()));
        }
        teacherCombo.setFont(UIConstants.FONT_BODY);
        addField(form, gbc, 1, 2, "Enseignant :", teacherCombo);

        // Combo salles
        List<Room> rooms = roomDAO.findAll();
        roomCombo = new JComboBox<>();
        for (Room r : rooms) {
            roomCombo.addItem(new ComboItem(r.getId(), r.getName()));
        }
        roomCombo.setFont(UIConstants.FONT_BODY);
        addField(form, gbc, 2, 2, "Salle :", roomCombo);

        // Row 4: Groupe, Semestre, Couleur
        groupField = new PlaceholderTextField("Groupe A");
        groupField.setPreferredSize(new Dimension(110, 36));
        addField(form, gbc, 0, 4, "Groupe :", groupField);

        semesterField = new PlaceholderTextField("S1");
        semesterField.setPreferredSize(new Dimension(80, 36));
        addField(form, gbc, 1, 4, "Semestre :", semesterField);

        colorField = new PlaceholderTextField("#6366F1");
        colorField.setPreferredSize(new Dimension(90, 36));
        addField(form, gbc, 2, 4, "Couleur :", colorField);

        // Action buttons
        gbc.gridx = 3;
        gbc.gridy = 2;
        RoundedButton addBtn = new RoundedButton("➕  Ajouter", UIConstants.ACCENT_SUCCESS,
                UIConstants.ACCENT_SUCCESS.brighter());
        addBtn.setPreferredSize(new Dimension(130, 40));
        form.add(addBtn, gbc);

        gbc.gridy = 4;
        RoundedButton updateBtn = new RoundedButton("✏️  Modifier", UIConstants.ACCENT_WARNING,
                UIConstants.ACCENT_WARNING.brighter());
        updateBtn.setPreferredSize(new Dimension(130, 40));
        form.add(updateBtn, gbc);

        formCard.add(form, BorderLayout.CENTER);
        container.add(formCard);
        container.add(Box.createVerticalStrut(20));

        // ── Table ───────────────────────────────────────────
        String[] columns = { "ID", "Jour", "Début", "Fin", "Matière", "Enseignant", "Salle", "Groupe", "Sem.", "Type",
                "Couleur" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        DashboardPanel.styleTable(table);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(55);
        table.getColumnModel().getColumn(3).setPreferredWidth(55);
        table.getColumnModel().getColumn(4).setPreferredWidth(160);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);
        table.getColumnModel().getColumn(8).setPreferredWidth(40);
        table.getColumnModel().getColumn(9).setPreferredWidth(60);
        table.getColumnModel().getColumn(10).setPreferredWidth(70);

        loadTimetable();

        // Sélection → remplir le formulaire
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

        RoundedButton deleteBtn = new RoundedButton("🗑  Supprimer", UIConstants.ACCENT_DANGER,
                UIConstants.ACCENT_DANGER.brighter());
        deleteBtn.setPreferredSize(new Dimension(140, 40));

        RoundedButton refreshBtn = new RoundedButton("🔄  Actualiser");
        refreshBtn.setPreferredSize(new Dimension(140, 40));

        RoundedButton clearBtn = new RoundedButton("🧹  Vider", UIConstants.BG_HOVER, UIConstants.BG_LIGHT);
        clearBtn.setPreferredSize(new Dimension(120, 40));

        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(clearBtn);
        container.add(btnPanel);

        // ── Events ──────────────────────────────────────────
        addBtn.addActionListener(e -> addEntry());
        updateBtn.addActionListener(e -> updateEntry());
        deleteBtn.addActionListener(e -> deleteEntry());
        refreshBtn.addActionListener(e -> loadTimetable());
        clearBtn.addActionListener(e -> clearForm());

        // Scroll
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setBackground(UIConstants.BG_DARK);
        mainScroll.getViewport().setBackground(UIConstants.BG_DARK);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    // ── Helper: ajouter un champ dans la grille ─────────────
    private void addField(JPanel panel, GridBagConstraints gbc, int col, int row, String label, JComponent field) {
        gbc.gridx = col;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(UIConstants.TEXT_SECONDARY);
        panel.add(lbl, gbc);
        gbc.gridy = row + 1;
        panel.add(field, gbc);
    }

    // ── Charger les données ─────────────────────────────────
    private void loadTimetable() {
        tableModel.setRowCount(0);
        List<Timetable> list = timetableDAO.findAll();
        for (Timetable t : list) {
            tableModel.addRow(new Object[] {
                    t.getId(), t.getDayOfWeek(), t.getStartTime(), t.getEndTime(),
                    t.getSubject(),
                    t.getTeacherName() != null ? t.getTeacherName() : "",
                    t.getRoomName() != null ? t.getRoomName() : "",
                    t.getGroupName(), t.getSemester(), t.getType(), t.getColor()
            });
        }
    }

    // ── Remplir le formulaire depuis la sélection ───────────
    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;

        int id = (int) tableModel.getValueAt(row, 0);
        Timetable t = timetableDAO.findById(id);
        if (t == null)
            return;

        dayCombo.setSelectedItem(t.getDayOfWeek());

        // Start/End hours
        startHourCombo.setSelectedItem(t.getStartTime().substring(0, 5));
        endHourCombo.setSelectedItem(t.getEndTime().substring(0, 5));

        typeCombo.setSelectedItem(t.getType());
        subjectField.setText(t.getSubject());
        groupField.setText(t.getGroupName());
        semesterField.setText(t.getSemester());
        colorField.setText(t.getColor());

        // Sélectionner l'enseignant
        selectComboItemById(teacherCombo, t.getTeacherId());
        selectComboItemById(roomCombo, t.getRoomId());
    }

    private void selectComboItemById(JComboBox<ComboItem> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).id == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    // ── Ajouter ─────────────────────────────────────────────
    private void addEntry() {
        Timetable t = buildTimetableFromForm();
        if (t == null)
            return;

        if (timetableDAO.insert(t)) {
            JOptionPane.showMessageDialog(this, "Séance ajoutée avec succès !", "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            loadTimetable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Modifier ────────────────────────────────────────────
    private void updateEntry() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une séance.", "Avertissement",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Timetable t = buildTimetableFromForm();
        if (t == null)
            return;

        t.setId((int) tableModel.getValueAt(row, 0));

        if (timetableDAO.update(t)) {
            JOptionPane.showMessageDialog(this, "Séance modifiée !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            loadTimetable();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Supprimer ───────────────────────────────────────────
    private void deleteEntry() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une séance.", "Avertissement",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer la séance \"" + tableModel.getValueAt(row, 4) + "\" ?",
                "Confirmer", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(row, 0);
            if (timetableDAO.delete(id)) {
                JOptionPane.showMessageDialog(this, "Séance supprimée.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                loadTimetable();
                clearForm();
            }
        }
    }

    // ── Construire un Timetable depuis le formulaire ────────
    private Timetable buildTimetableFromForm() {
        String subject = subjectField.getText().trim();
        if (subject.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom de la matière est requis.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        ComboItem teacher = (ComboItem) teacherCombo.getSelectedItem();
        ComboItem room = (ComboItem) roomCombo.getSelectedItem();
        if (teacher == null || room == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un enseignant et une salle.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String startTime = (String) startHourCombo.getSelectedItem();
        String endTime = (String) endHourCombo.getSelectedItem();

        if (startTime.compareTo(endTime) >= 0) {
            JOptionPane.showMessageDialog(this, "L'heure de fin doit être après l'heure de début.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String color = colorField.getText().trim();
        if (color.isEmpty())
            color = "#6366F1";

        return new Timetable(
                (String) dayCombo.getSelectedItem(),
                startTime, endTime,
                subject,
                teacher.id, room.id,
                groupField.getText().trim().isEmpty() ? "Tous" : groupField.getText().trim(),
                semesterField.getText().trim().isEmpty() ? "S1" : semesterField.getText().trim(),
                (String) typeCombo.getSelectedItem(),
                color);
    }

    // ── Vider le formulaire ─────────────────────────────────
    private void clearForm() {
        dayCombo.setSelectedIndex(0);
        startHourCombo.setSelectedIndex(0);
        endHourCombo.setSelectedIndex(2);
        typeCombo.setSelectedIndex(0);
        subjectField.setText("");
        groupField.setText("");
        semesterField.setText("S1");
        colorField.setText("#6366F1");
        if (teacherCombo.getItemCount() > 0)
            teacherCombo.setSelectedIndex(0);
        if (roomCombo.getItemCount() > 0)
            roomCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    // ═══════════════════════════════════════════════════════
    // Helper class: ComboBox item avec ID
    // ═══════════════════════════════════════════════════════
    private static class ComboItem {
        int id;
        String label;

        ComboItem(int id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
