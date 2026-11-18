package gui;

import dao.ReservationDAO;
import dao.RoomDAO;
import gui.components.RoundedPanel;
import model.User;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * StatisticsPanel — Displays usage statistics with hand-drawn charts.
 * Shows most used rooms (bar chart) and peak hours (line chart).
 */
public class StatisticsPanel extends JPanel {

    private User currentUser;
    private RoomDAO roomDAO = new RoomDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();

    public StatisticsPanel(User user) {
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
        JLabel header = new JLabel("📈  Usage Statistics");
        header.setFont(UIConstants.FONT_TITLE);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Room usage analytics and peak hours");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(subtitle);
        container.add(Box.createVerticalStrut(30));

        // ── Most Used Rooms (Bar Chart) ─────────────────────
        JLabel barTitle = new JLabel("🏆  Most Used Rooms");
        barTitle.setFont(UIConstants.FONT_SUBTITLE);
        barTitle.setForeground(UIConstants.TEXT_PRIMARY);
        barTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(barTitle);
        container.add(Box.createVerticalStrut(15));

        RoundedPanel barChartCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_MEDIUM);
        barChartCard.setLayout(new BorderLayout());
        barChartCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        barChartCard.setPreferredSize(new Dimension(0, 300));
        barChartCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        barChartCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        barChartCard.add(new BarChartPanel(roomDAO.getMostUsedRooms(8)), BorderLayout.CENTER);
        container.add(barChartCard);
        container.add(Box.createVerticalStrut(30));

        // ── Peak Hours (Bar Chart) ──────────────────────────
        JLabel peakTitle = new JLabel("⏰  Peak Reservation Hours");
        peakTitle.setFont(UIConstants.FONT_SUBTITLE);
        peakTitle.setForeground(UIConstants.TEXT_PRIMARY);
        peakTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(peakTitle);
        container.add(Box.createVerticalStrut(15));

        RoundedPanel peakChartCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_MEDIUM);
        peakChartCard.setLayout(new BorderLayout());
        peakChartCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        peakChartCard.setPreferredSize(new Dimension(0, 300));
        peakChartCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        peakChartCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        peakChartCard.add(new PeakHoursChartPanel(reservationDAO.getPeakHours()), BorderLayout.CENTER);
        container.add(peakChartCard);
        container.add(Box.createVerticalStrut(30));

        // ── Reservations Per Day ────────────────────────────
        JLabel dailyTitle = new JLabel("📅  Reservations (Last 7 Days)");
        dailyTitle.setFont(UIConstants.FONT_SUBTITLE);
        dailyTitle.setForeground(UIConstants.TEXT_PRIMARY);
        dailyTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(dailyTitle);
        container.add(Box.createVerticalStrut(15));

        RoundedPanel dailyChartCard = new RoundedPanel(UIConstants.BORDER_RADIUS, UIConstants.BG_MEDIUM);
        dailyChartCard.setLayout(new BorderLayout());
        dailyChartCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dailyChartCard.setPreferredSize(new Dimension(0, 300));
        dailyChartCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        dailyChartCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        dailyChartCard.add(new DailyChartPanel(reservationDAO.getReservationsPerDay()), BorderLayout.CENTER);
        container.add(dailyChartCard);

        // Scroll
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setBackground(UIConstants.BG_DARK);
        mainScroll.getViewport().setBackground(UIConstants.BG_DARK);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    // ═══════════════════════════════════════════════════════
    // Inner class: Bar Chart for Most Used Rooms
    // ═══════════════════════════════════════════════════════
    private static class BarChartPanel extends JPanel {
        private List<Object[]> data; // [roomName, count]

        BarChartPanel(List<Object[]> data) {
            this.data = data;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                g.setColor(UIConstants.TEXT_MUTED);
                g.setFont(UIConstants.FONT_BODY);
                g.drawString("No data available", getWidth() / 2 - 60, getHeight() / 2);
                return;
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padding = 60;
            int chartWidth = getWidth() - padding * 2;
            int chartHeight = getHeight() - padding * 2;
            int barWidth = Math.min(60, chartWidth / data.size() - 10);
            int gap = (chartWidth - barWidth * data.size()) / (data.size() + 1);

            // Find max value
            int maxVal = 1;
            for (Object[] item : data) {
                maxVal = Math.max(maxVal, (Integer) item[1]);
            }

            // Draw bars
            Color[] barColors = {
                    UIConstants.ACCENT_PRIMARY, UIConstants.ACCENT_SUCCESS,
                    UIConstants.ACCENT_INFO, UIConstants.ACCENT_PURPLE,
                    UIConstants.ACCENT_ORANGE, UIConstants.ACCENT_WARNING,
                    UIConstants.ACCENT_DANGER, new Color(236, 72, 153)
            };

            for (int i = 0; i < data.size(); i++) {
                int count = (Integer) data.get(i)[1];
                String name = (String) data.get(i)[0];

                int barHeight = (int) ((double) count / maxVal * chartHeight);
                int x = padding + gap + i * (barWidth + gap);
                int y = padding + chartHeight - barHeight;

                // Gradient bar
                Color color = barColors[i % barColors.length];
                GradientPaint gradient = new GradientPaint(x, y, color, x, y + barHeight, color.darker());
                g2d.setPaint(gradient);
                g2d.fillRoundRect(x, y, barWidth, barHeight, 6, 6);

                // Value label
                g2d.setColor(UIConstants.TEXT_PRIMARY);
                g2d.setFont(UIConstants.FONT_SMALL);
                String valStr = String.valueOf(count);
                int textW = g2d.getFontMetrics().stringWidth(valStr);
                g2d.drawString(valStr, x + barWidth / 2 - textW / 2, y - 8);

                // Room name (rotated or truncated)
                g2d.setColor(UIConstants.TEXT_SECONDARY);
                String shortName = name.length() > 12 ? name.substring(0, 12) + ".." : name;
                textW = g2d.getFontMetrics().stringWidth(shortName);
                g2d.drawString(shortName, x + barWidth / 2 - textW / 2, padding + chartHeight + 20);
            }

            // Draw axes
            g2d.setColor(UIConstants.BORDER_COLOR);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(padding, padding, padding, padding + chartHeight);
            g2d.drawLine(padding, padding + chartHeight, padding + chartWidth, padding + chartHeight);
        }
    }

    // ═══════════════════════════════════════════════════════
    // Inner class: Peak Hours Chart
    // ═══════════════════════════════════════════════════════
    private static class PeakHoursChartPanel extends JPanel {
        private List<Object[]> data; // [hour, count]

        PeakHoursChartPanel(List<Object[]> data) {
            this.data = data;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                g.setColor(UIConstants.TEXT_MUTED);
                g.setFont(UIConstants.FONT_BODY);
                g.drawString("No data available", getWidth() / 2 - 60, getHeight() / 2);
                return;
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padding = 60;
            int chartWidth = getWidth() - padding * 2;
            int chartHeight = getHeight() - padding * 2;

            int maxVal = 1;
            for (Object[] item : data) {
                maxVal = Math.max(maxVal, (Integer) item[1]);
            }

            int barWidth = Math.min(50, chartWidth / Math.max(data.size(), 1) - 8);
            int gap = (chartWidth - barWidth * data.size()) / (data.size() + 1);

            for (int i = 0; i < data.size(); i++) {
                int hour = (Integer) data.get(i)[0];
                int count = (Integer) data.get(i)[1];

                int barHeight = (int) ((double) count / maxVal * chartHeight);
                int x = padding + gap + i * (barWidth + gap);
                int y = padding + chartHeight - barHeight;

                // Draw bar with gradient
                GradientPaint gradient = new GradientPaint(x, y, UIConstants.ACCENT_INFO,
                        x, y + barHeight, UIConstants.ACCENT_PRIMARY);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(x, y, barWidth, barHeight, 6, 6);

                // Value
                g2d.setColor(UIConstants.TEXT_PRIMARY);
                g2d.setFont(UIConstants.FONT_SMALL);
                String val = String.valueOf(count);
                g2d.drawString(val, x + barWidth / 2 - g2d.getFontMetrics().stringWidth(val) / 2, y - 8);

                // Hour label
                g2d.setColor(UIConstants.TEXT_SECONDARY);
                String hourStr = String.format("%02d:00", hour);
                g2d.drawString(hourStr, x + barWidth / 2 - g2d.getFontMetrics().stringWidth(hourStr) / 2,
                        padding + chartHeight + 20);
            }

            // Axes
            g2d.setColor(UIConstants.BORDER_COLOR);
            g2d.drawLine(padding, padding, padding, padding + chartHeight);
            g2d.drawLine(padding, padding + chartHeight, padding + chartWidth, padding + chartHeight);
        }
    }

    // ═══════════════════════════════════════════════════════
    // Inner class: Daily Reservations Chart
    // ═══════════════════════════════════════════════════════
    private static class DailyChartPanel extends JPanel {
        private List<Object[]> data; // [date, count]

        DailyChartPanel(List<Object[]> data) {
            this.data = data;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                g.setColor(UIConstants.TEXT_MUTED);
                g.setFont(UIConstants.FONT_BODY);
                g.drawString("No data available", getWidth() / 2 - 60, getHeight() / 2);
                return;
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padding = 60;
            int chartWidth = getWidth() - padding * 2;
            int chartHeight = getHeight() - padding * 2;

            int maxVal = 1;
            for (Object[] item : data) {
                maxVal = Math.max(maxVal, (Integer) item[1]);
            }

            // Draw area chart with line
            int pointSpacing = data.size() > 1 ? chartWidth / (data.size() - 1) : chartWidth;

            // Filled area
            int[] xPoints = new int[data.size() + 2];
            int[] yPoints = new int[data.size() + 2];

            for (int i = 0; i < data.size(); i++) {
                int count = (Integer) data.get(i)[1];
                xPoints[i] = padding + i * pointSpacing;
                yPoints[i] = padding + chartHeight - (int) ((double) count / maxVal * chartHeight);
            }
            xPoints[data.size()] = padding + (data.size() - 1) * pointSpacing;
            yPoints[data.size()] = padding + chartHeight;
            xPoints[data.size() + 1] = padding;
            yPoints[data.size() + 1] = padding + chartHeight;

            // Fill area
            g2d.setColor(new Color(99, 102, 241, 40));
            g2d.fillPolygon(xPoints, yPoints, data.size() + 2);

            // Draw line
            g2d.setColor(UIConstants.ACCENT_PRIMARY);
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < data.size() - 1; i++) {
                g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
            }

            // Draw points and labels
            for (int i = 0; i < data.size(); i++) {
                String dateStr = (String) data.get(i)[0];
                int count = (Integer) data.get(i)[1];

                // Point dot
                g2d.setColor(UIConstants.ACCENT_PRIMARY);
                g2d.fillOval(xPoints[i] - 5, yPoints[i] - 5, 10, 10);
                g2d.setColor(UIConstants.BG_MEDIUM);
                g2d.fillOval(xPoints[i] - 3, yPoints[i] - 3, 6, 6);

                // Value
                g2d.setColor(UIConstants.TEXT_PRIMARY);
                g2d.setFont(UIConstants.FONT_SMALL);
                g2d.drawString(String.valueOf(count), xPoints[i] - 4, yPoints[i] - 12);

                // Date label
                g2d.setColor(UIConstants.TEXT_SECONDARY);
                String shortDate = dateStr.length() >= 10 ? dateStr.substring(5) : dateStr; // MM-DD
                int textW = g2d.getFontMetrics().stringWidth(shortDate);
                g2d.drawString(shortDate, xPoints[i] - textW / 2, padding + chartHeight + 20);
            }

            // Axes
            g2d.setColor(UIConstants.BORDER_COLOR);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(padding, padding, padding, padding + chartHeight);
            g2d.drawLine(padding, padding + chartHeight, padding + chartWidth, padding + chartHeight);
        }
    }
}
