package gui;

import dao.ReservationDAO;
import gui.components.RoundedButton;
import model.Reservation;
import model.User;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * AdminReservationsPanel — Admin view for managing all reservations.
 */
public class AdminReservationsPanel extends JPanel {

    private User currentUser;
    private ReservationDAO reservationDAO = new ReservationDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    public AdminReservationsPanel(User user) {
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
        JLabel header = new JLabel("📅  All Reservations (Admin)");
        header.setFont(UIConstants.FONT_TITLE);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("View and manage all room reservations");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(subtitle);
        container.add(Box.createVerticalStrut(25));

        // Table
        String[] columns = { "ID", "User", "Room", "Date", "Start", "End", "Purpose", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        DashboardPanel.styleTable(table);

        loadAll();

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1));
        tableScroll.getViewport().setBackground(UIConstants.BG_MEDIUM);
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(tableScroll);
        container.add(Box.createVerticalStrut(15));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(UIConstants.BG_DARK);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton cancelBtn = new RoundedButton("❌  Cancel", UIConstants.ACCENT_DANGER,
                UIConstants.ACCENT_DANGER.brighter());
        cancelBtn.setPreferredSize(new Dimension(130, 40));
        cancelBtn.addActionListener(e -> cancelSelected());

        RoundedButton deleteBtn = new RoundedButton("🗑  Delete", new Color(180, 50, 50), new Color(200, 70, 70));
        deleteBtn.setPreferredSize(new Dimension(130, 40));
        deleteBtn.addActionListener(e -> deleteSelected());

        RoundedButton refreshBtn = new RoundedButton("🔄  Refresh");
        refreshBtn.setPreferredSize(new Dimension(130, 40));
        refreshBtn.addActionListener(e -> loadAll());

        btnPanel.add(cancelBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);
        container.add(btnPanel);

        // Scroll
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setBackground(UIConstants.BG_DARK);
        mainScroll.getViewport().setBackground(UIConstants.BG_DARK);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    private void loadAll() {
        tableModel.setRowCount(0);
        List<Reservation> list = reservationDAO.findAll();
        for (Reservation r : list) {
            tableModel.addRow(new Object[] {
                    r.getId(), r.getUserName(), r.getRoomName(),
                    r.getReservationDate(), r.getStartTime(), r.getEndTime(),
                    r.getPurpose(), r.getStatus()
            });
        }
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a reservation.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        if (reservationDAO.cancel(id)) {
            JOptionPane.showMessageDialog(this, "Reservation cancelled.");
            loadAll();
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a reservation.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Permanently delete this reservation?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(row, 0);
            if (reservationDAO.delete(id)) {
                JOptionPane.showMessageDialog(this, "Reservation deleted.");
                loadAll();
            }
        }
    }
}
