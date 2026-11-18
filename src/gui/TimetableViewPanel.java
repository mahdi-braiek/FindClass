package gui;

import dao.TimetableDAO;
import gui.components.RoundedButton;
import model.Timetable;
import model.User;
import util.ExportUtil;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * TimetableViewPanel — Vue calendrier hebdomadaire de l'emploi du temps.
 * Affiche une grille Lundi→Vendredi, 08h→18h avec les cours en blocs colorés.
 * Les utilisateurs peuvent filtrer par groupe et semestre.
 */
public class TimetableViewPanel extends JPanel {

    private User currentUser;
    private TimetableDAO timetableDAO = new TimetableDAO();
    private List<Timetable> entries = new ArrayList<>();

    // Filtres
    private JComboBox<String> groupCombo;
    private JComboBox<String> semesterCombo;

    // Constantes du calendrier
    private static final String[] DAYS = { "LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI" };
    private static final String[] DAY_LABELS = { "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi" };
    private static final int START_HOUR = 8;
    private static final int END_HOUR = 18;
    private static final int TOTAL_HOURS = END_HOUR - START_HOUR; // 10 heures

    // Panel du calendrier (sera redessiné à chaque refresh)
    private JPanel calendarPanel;

    public TimetableViewPanel(User user) {
        this.currentUser = user;
        setBackground(UIConstants.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        buildUI();
        loadData("Tous", "");
    }

    private void buildUI() {
        JPanel container = new JPanel();
        container.setBackground(UIConstants.BG_DARK);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        // ── En-tête ─────────────────────────────────────────
        JLabel header = new JLabel("🗓  Emploi du Temps");
        header.setFont(UIConstants.FONT_TITLE);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(header);
        container.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Consultez votre planning hebdomadaire");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(UIConstants.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(subtitle);
        container.add(Box.createVerticalStrut(20));

        // ── Barre de filtres ────────────────────────────────
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterBar.setBackground(UIConstants.BG_DARK);
        filterBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        filterBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel groupLabel = new JLabel("👥 Groupe :");
        groupLabel.setFont(UIConstants.FONT_BODY);
        groupLabel.setForeground(UIConstants.TEXT_SECONDARY);

        // Charger les groupes depuis la BD
        List<String> groups = timetableDAO.getDistinctGroups();
        groups.add(0, "Tous");
        groupCombo = new JComboBox<>(groups.toArray(new String[0]));
        groupCombo.setFont(UIConstants.FONT_BODY);
        groupCombo.setBackground(UIConstants.BG_INPUT);
        groupCombo.setForeground(UIConstants.TEXT_PRIMARY);
        groupCombo.setPreferredSize(new Dimension(140, 36));

        JLabel semLabel = new JLabel("📚 Semestre :");
        semLabel.setFont(UIConstants.FONT_BODY);
        semLabel.setForeground(UIConstants.TEXT_SECONDARY);

        List<String> semesters = timetableDAO.getDistinctSemesters();
        semesters.add(0, "Tous");
        semesterCombo = new JComboBox<>(semesters.toArray(new String[0]));
        semesterCombo.setFont(UIConstants.FONT_BODY);
        semesterCombo.setBackground(UIConstants.BG_INPUT);
        semesterCombo.setForeground(UIConstants.TEXT_PRIMARY);
        semesterCombo.setPreferredSize(new Dimension(100, 36));

        // Actualiser au changement de filtre
        groupCombo.addActionListener(e -> refreshCalendar());
        semesterCombo.addActionListener(e -> refreshCalendar());

        filterBar.add(groupLabel);
        filterBar.add(groupCombo);
        filterBar.add(Box.createHorizontalStrut(20));
        filterBar.add(semLabel);
        filterBar.add(semesterCombo);

        // ── Légende ─────────────────────────────────────────
        filterBar.add(Box.createHorizontalStrut(40));
        filterBar.add(createLegendDot("#6366F1", "COURS"));
        filterBar.add(createLegendDot("#F59E0B", "TD"));
        filterBar.add(createLegendDot("#22C55E", "TP"));
        filterBar.add(createLegendDot("#EF4444", "EXAM"));

        // Export button
        filterBar.add(Box.createHorizontalStrut(30));
        RoundedButton exportBtn = new RoundedButton("📸  Exporter PNG", UIConstants.ACCENT_INFO,
                UIConstants.ACCENT_INFO.brighter());
        exportBtn.setPreferredSize(new Dimension(150, 36));
        exportBtn.addActionListener(e -> {
            if (calendarPanel.getComponentCount() > 0) {
                ExportUtil.exportToPNG((javax.swing.JComponent) calendarPanel.getComponent(0), this, "emploi_du_temps");
            }
        });
        filterBar.add(exportBtn);

        container.add(filterBar);
        container.add(Box.createVerticalStrut(20));

        // ── Calendrier ──────────────────────────────────────
        calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBackground(UIConstants.BG_DARK);
        calendarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(calendarPanel);

        // Scroll
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        mainScroll.setBackground(UIConstants.BG_DARK);
        mainScroll.getViewport().setBackground(UIConstants.BG_DARK);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(mainScroll, BorderLayout.CENTER);
    }

    /**
     * Créer un point de légende coloré.
     */
    private JPanel createLegendDot(String hexColor, String label) {
        JPanel dot = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dot.setBackground(UIConstants.BG_DARK);

        JPanel colorBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.decode(hexColor));
                g2d.fillRoundRect(0, 0, 14, 14, 4, 4);
            }
        };
        colorBox.setOpaque(false);
        colorBox.setPreferredSize(new Dimension(14, 14));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(UIConstants.TEXT_MUTED);

        dot.add(colorBox);
        dot.add(lbl);
        return dot;
    }

    /**
     * Charger les données et dessiner le calendrier.
     */
    private void loadData(String group, String semester) {
        String grp = (group != null && !group.equals("Tous")) ? group : null;
        String sem = (semester != null && !semester.equals("Tous")) ? semester : null;

        if (grp != null || sem != null) {
            entries = timetableDAO.findByFilter(grp, sem);
        } else {
            entries = timetableDAO.findAll();
        }
        drawCalendar();
    }

    /**
     * Rafraîchir le calendrier quand les filtres changent.
     */
    private void refreshCalendar() {
        String group = (String) groupCombo.getSelectedItem();
        String semester = (String) semesterCombo.getSelectedItem();
        loadData(group, semester);
    }

    /**
     * Dessiner la grille calendrier avec les blocs de cours.
     */
    private void drawCalendar() {
        calendarPanel.removeAll();

        // Dimensions
        int hourLabelWidth = 60;
        int dayWidth = 180;
        int hourHeight = 70;
        int headerHeight = 45;
        int totalWidth = hourLabelWidth + (dayWidth * DAYS.length) + 20;
        int totalHeight = headerHeight + (hourHeight * TOTAL_HOURS) + 20;

        JPanel grid = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // ── En-têtes des jours ──────────────────────
                for (int d = 0; d < DAYS.length; d++) {
                    int x = hourLabelWidth + d * dayWidth;

                    // Fond en-tête
                    g2d.setColor(UIConstants.BG_LIGHT);
                    g2d.fillRoundRect(x + 2, 2, dayWidth - 4, headerHeight - 4, 8, 8);

                    // Texte du jour
                    g2d.setColor(UIConstants.TEXT_PRIMARY);
                    g2d.setFont(UIConstants.FONT_HEADING);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textW = fm.stringWidth(DAY_LABELS[d]);
                    g2d.drawString(DAY_LABELS[d], x + (dayWidth - textW) / 2,
                            headerHeight / 2 + fm.getAscent() / 2 - 2);
                }

                // ── Lignes horizontales (heures) ────────────
                g2d.setFont(UIConstants.FONT_SMALL);
                for (int h = 0; h <= TOTAL_HOURS; h++) {
                    int y = headerHeight + h * hourHeight;

                    // Ligne
                    g2d.setColor(new Color(55, 55, 75, 80));
                    g2d.drawLine(hourLabelWidth, y, hourLabelWidth + dayWidth * DAYS.length, y);

                    // Label heure
                    if (h < TOTAL_HOURS) {
                        g2d.setColor(UIConstants.TEXT_MUTED);
                        String hourStr = String.format("%02d:00", START_HOUR + h);
                        g2d.drawString(hourStr, 8, y + 18);
                    }
                }

                // ── Lignes verticales (jours) ───────────────
                for (int d = 0; d <= DAYS.length; d++) {
                    int x = hourLabelWidth + d * dayWidth;
                    g2d.setColor(new Color(55, 55, 75, 50));
                    g2d.drawLine(x, headerHeight, x, headerHeight + TOTAL_HOURS * hourHeight);
                }

                // ── Blocs de cours ──────────────────────────
                for (Timetable entry : entries) {
                    int dayIndex = getDayIndex(entry.getDayOfWeek());
                    if (dayIndex < 0)
                        continue;

                    int startH = entry.getStartHour();
                    int endH = entry.getEndHour();

                    int x = hourLabelWidth + dayIndex * dayWidth + 4;
                    int y = headerHeight + (startH - START_HOUR) * hourHeight + 2;
                    int blockW = dayWidth - 8;
                    int blockH = (endH - startH) * hourHeight - 4;

                    // Couleur du bloc
                    Color blockColor;
                    try {
                        blockColor = Color.decode(entry.getColor());
                    } catch (Exception e) {
                        blockColor = UIConstants.ACCENT_PRIMARY;
                    }

                    // Fond du bloc avec gradient
                    GradientPaint gradient = new GradientPaint(
                            x, y, blockColor,
                            x + blockW, y + blockH, new Color(
                                    Math.max(blockColor.getRed() - 40, 0),
                                    Math.max(blockColor.getGreen() - 40, 0),
                                    Math.max(blockColor.getBlue() - 40, 0)));
                    g2d.setPaint(gradient);
                    g2d.fill(new RoundRectangle2D.Float(x, y, blockW, blockH, 10, 10));

                    // Bordure gauche colorée
                    g2d.setColor(blockColor.brighter());
                    g2d.fillRoundRect(x, y, 4, blockH, 4, 4);

                    // Texte du cours
                    g2d.setColor(Color.WHITE);
                    int textX = x + 10;
                    int textY = y + 20;

                    // Nom de la matière
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    String subjectText = entry.getSubject();
                    if (g2d.getFontMetrics().stringWidth(subjectText) > blockW - 16) {
                        subjectText = subjectText.substring(0, Math.min(subjectText.length(), 18)) + "..";
                    }
                    g2d.drawString(subjectText, textX, textY);

                    // Type (COURS/TD/TP)
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    g2d.setColor(new Color(255, 255, 255, 200));
                    g2d.drawString(entry.getType(), textX, textY + 16);

                    // Salle + Enseignant (si assez de place)
                    if (blockH > 55) {
                        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                        g2d.setColor(new Color(255, 255, 255, 170));
                        String room = entry.getRoomName() != null ? entry.getRoomName() : "";
                        g2d.drawString("📍 " + room, textX, textY + 33);

                        if (blockH > 72) {
                            String teacher = entry.getTeacherName() != null ? entry.getTeacherName() : "";
                            g2d.drawString("👤 " + teacher, textX, textY + 48);
                        }
                    }

                    // Groupe (en bas à droite du bloc)
                    if (blockH > 55) {
                        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                        g2d.setColor(new Color(255, 255, 255, 140));
                        String grp = entry.getGroupName() != null ? entry.getGroupName() : "";
                        int grpW = g2d.getFontMetrics().stringWidth(grp);
                        g2d.drawString(grp, x + blockW - grpW - 8, y + blockH - 8);
                    }
                }
            }
        };

        grid.setOpaque(false);
        grid.setPreferredSize(new Dimension(totalWidth, totalHeight));

        calendarPanel.add(grid, BorderLayout.CENTER);
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    /**
     * Convertir le nom du jour en index (0=Lundi, 4=Vendredi).
     */
    private int getDayIndex(String day) {
        for (int i = 0; i < DAYS.length; i++) {
            if (DAYS[i].equalsIgnoreCase(day))
                return i;
        }
        return -1;
    }
}
