package gui;

import dao.ReservationDAO;
import gui.components.RoundedButton;
import gui.components.RoundedPanel;
import model.Reservation;
import model.User;
import util.QRCodeGenerator;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * MyReservationsPanel — Shows the user's reservations with cancel and QR view
 * options.
 */
public class MyReservationsPanel extends JPanel {

    private User currentUser;
    private ReservationDAO reservationDAO = new ReservationDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    public MyReservationsPanel(User user) {
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
        JLabel header = new JLabel("📋  My Reservations");
        header.setFont(UIConstants.FONT_TITLE);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("View and manage your room reservations");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(subtitle);
        container.add(Box.createVerticalStrut(25));

        // Table
        String[] columns = { "ID", "Room", "Date", "Start", "End", "Purpose", "Status", "QR Code" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        DashboardPanel.styleTable(table);

        loadReservations();

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1));
        tableScroll.getViewport().setBackground(UIConstants.BG_MEDIUM);
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(tableScroll);
        container.add(Box.createVerticalStrut(15));

        // Action Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(UIConstants.BG_DARK);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton viewQRBtn = new RoundedButton("📱  View QR Code", UIConstants.ACCENT_INFO,
                UIConstants.ACCENT_INFO.brighter());
        viewQRBtn.setPreferredSize(new Dimension(180, 42));
        viewQRBtn.addActionListener(e -> viewQRCode());

        RoundedButton cancelBtn = new RoundedButton("❌  Cancel", UIConstants.ACCENT_DANGER,
                UIConstants.ACCENT_DANGER.brighter());
        cancelBtn.setPreferredSize(new Dimension(140, 42));
        cancelBtn.addActionListener(e -> cancelReservation());

        RoundedButton refreshBtn = new RoundedButton("🔄  Refresh");
        refreshBtn.setPreferredSize(new Dimension(140, 42));
        refreshBtn.addActionListener(e -> loadReservations());

        btnPanel.add(viewQRBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(refreshBtn);
        container.add(btnPanel);

        // Scroll pane
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setBackground(UIConstants.BG_DARK);
        mainScroll.getViewport().setBackground(UIConstants.BG_DARK);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    private void loadReservations() {
        tableModel.setRowCount(0);
        List<Reservation> reservations = reservationDAO.findByUserId(currentUser.getId());
        for (Reservation r : reservations) {
            tableModel.addRow(new Object[] {
                    r.getId(), r.getRoomName(), r.getReservationDate(),
                    r.getStartTime(), r.getEndTime(), r.getPurpose(),
                    r.getStatus(), r.getQrCode() != null ? r.getQrCode() : ""
            });
        }
    }

    private void viewQRCode() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation.", "UniRoom", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String qrCode = (String) tableModel.getValueAt(row, 7);
        String room = (String) tableModel.getValueAt(row, 1);
        String date = (String) tableModel.getValueAt(row, 2);

        if (qrCode == null || qrCode.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No QR code for this reservation.", "UniRoom",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        BufferedImage qrImage = QRCodeGenerator.generateQRCode(qrCode);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "QR Code", true);
        dialog.setSize(380, 450);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UIConstants.BG_MEDIUM);

        JPanel content = new JPanel();
        content.setBackground(UIConstants.BG_MEDIUM);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("📱  Reservation QR Code");
        title.setFont(UIConstants.FONT_SUBTITLE);
        title.setForeground(UIConstants.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel info = new JLabel(room + " - " + date);
        info.setFont(UIConstants.FONT_BODY);
        info.setForeground(UIConstants.TEXT_SECONDARY);
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel qrLabel = new JLabel(new ImageIcon(qrImage));
        qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel codeText = new JLabel(qrCode);
        codeText.setFont(UIConstants.FONT_SMALL);
        codeText.setForeground(UIConstants.TEXT_MUTED);
        codeText.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundedButton closeBtn = new RoundedButton("Close");
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> dialog.dispose());

        content.add(title);
        content.add(Box.createVerticalStrut(8));
        content.add(info);
        content.add(Box.createVerticalStrut(20));
        content.add(qrLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(codeText);
        content.add(Box.createVerticalStrut(20));
        content.add(closeBtn);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private void cancelReservation() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation.", "UniRoom", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = (String) tableModel.getValueAt(row, 6);
        if ("CANCELLED".equals(status) || "COMPLETED".equals(status)) {
            JOptionPane.showMessageDialog(this, "Cannot cancel a " + status.toLowerCase() + " reservation.", "UniRoom",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this reservation?",
                "Cancel Reservation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int resId = (int) tableModel.getValueAt(row, 0);
            if (reservationDAO.cancel(resId)) {
                JOptionPane.showMessageDialog(this, "Reservation cancelled successfully.", "UniRoom",
                        JOptionPane.INFORMATION_MESSAGE);
                loadReservations();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel reservation.", "UniRoom",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
