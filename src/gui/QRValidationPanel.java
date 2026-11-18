package gui;

import dao.ReservationDAO;
import gui.components.PlaceholderTextField;
import gui.components.RoundedButton;
import gui.components.RoundedPanel;
import model.Reservation;
import model.User;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * QRValidationPanel — Admin panel for validating reservations by QR code.
 * Simulates scanning by entering the QR code string.
 */
public class QRValidationPanel extends JPanel {

    private User currentUser;
    private ReservationDAO reservationDAO = new ReservationDAO();
    private PlaceholderTextField qrField;
    private JPanel resultPanel;

    public QRValidationPanel(User user) {
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
        JLabel header = new JLabel("📱  QR Code Validation");
        header.setFont(UIConstants.FONT_TITLE);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Enter or scan a QR code to validate a reservation");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(subtitle);
        container.add(Box.createVerticalStrut(30));

        // ── Scan Card ───────────────────────────────────────
        RoundedPanel scanCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_MEDIUM);
        scanCard.setLayout(new BoxLayout(scanCard, BoxLayout.Y_AXIS));
        scanCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        scanCard.setMaximumSize(new Dimension(600, 200));
        scanCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel scanIcon = new JLabel("📱");
        scanIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        scanIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scanText = new JLabel("Enter QR Code");
        scanText.setFont(UIConstants.FONT_HEADING);
        scanText.setForeground(UIConstants.TEXT_PRIMARY);
        scanText.setAlignmentX(Component.CENTER_ALIGNMENT);

        qrField = new PlaceholderTextField("Paste QR code here...");
        qrField.setMaximumSize(new Dimension(500, 42));
        qrField.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundedButton validateBtn = new RoundedButton("✅  Validate Reservation", UIConstants.ACCENT_SUCCESS,
                UIConstants.ACCENT_SUCCESS.brighter());
        validateBtn.setMaximumSize(new Dimension(250, 44));
        validateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        validateBtn.addActionListener(e -> validateQR());

        scanCard.add(scanIcon);
        scanCard.add(Box.createVerticalStrut(10));
        scanCard.add(scanText);
        scanCard.add(Box.createVerticalStrut(15));
        scanCard.add(qrField);
        scanCard.add(Box.createVerticalStrut(15));
        scanCard.add(validateBtn);

        container.add(scanCard);
        container.add(Box.createVerticalStrut(25));

        // ── Result Panel ────────────────────────────────────
        resultPanel = new JPanel();
        resultPanel.setBackground(UIConstants.BG_DARK);
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(resultPanel);

        // Scroll
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setBackground(UIConstants.BG_DARK);
        mainScroll.getViewport().setBackground(UIConstants.BG_DARK);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    private void validateQR() {
        String code = qrField.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a QR code.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        resultPanel.removeAll();

        Reservation reservation = reservationDAO.validateByQRCode(code);

        if (reservation != null) {
            // Success card
            RoundedPanel successCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_LIGHT);
            successCard.setGradient(new Color(34, 197, 94, 40), new Color(16, 185, 129, 40));
            successCard.setLayout(new BoxLayout(successCard, BoxLayout.Y_AXIS));
            successCard.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
            successCard.setMaximumSize(new Dimension(600, 250));
            successCard.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel check = new JLabel("✅  Reservation Validated!");
            check.setFont(UIConstants.FONT_SUBTITLE);
            check.setForeground(UIConstants.ACCENT_SUCCESS);
            check.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel details = new JLabel("<html>"
                    + "<b>Reservation #" + reservation.getId() + "</b><br><br>"
                    + "👤 User: " + reservation.getUserName() + "<br>"
                    + "🏠 Room: " + reservation.getRoomName() + "<br>"
                    + "📅 Date: " + reservation.getReservationDate() + "<br>"
                    + "🕐 Time: " + reservation.getStartTime() + " - " + reservation.getEndTime() + "<br>"
                    + "📝 Purpose: " + reservation.getPurpose() + "<br>"
                    + "✅ Status: COMPLETED"
                    + "</html>");
            details.setFont(UIConstants.FONT_BODY);
            details.setForeground(UIConstants.TEXT_PRIMARY);
            details.setAlignmentX(Component.LEFT_ALIGNMENT);

            successCard.add(check);
            successCard.add(Box.createVerticalStrut(15));
            successCard.add(details);

            resultPanel.add(successCard);
        } else {
            // Error card
            RoundedPanel errorCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_LIGHT);
            errorCard.setGradient(new Color(239, 68, 68, 30), new Color(220, 38, 38, 30));
            errorCard.setLayout(new BoxLayout(errorCard, BoxLayout.Y_AXIS));
            errorCard.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
            errorCard.setMaximumSize(new Dimension(600, 120));
            errorCard.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel errorLabel = new JLabel("❌  Invalid QR Code");
            errorLabel.setFont(UIConstants.FONT_SUBTITLE);
            errorLabel.setForeground(UIConstants.ACCENT_DANGER);
            errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel errorDesc = new JLabel("No active reservation found for this QR code.");
            errorDesc.setFont(UIConstants.FONT_BODY);
            errorDesc.setForeground(UIConstants.TEXT_SECONDARY);
            errorDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

            errorCard.add(errorLabel);
            errorCard.add(Box.createVerticalStrut(10));
            errorCard.add(errorDesc);

            resultPanel.add(errorCard);
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }
}
