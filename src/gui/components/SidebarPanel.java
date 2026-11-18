package gui.components;

import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * SidebarPanel — Modern sidebar navigation with icons, active state,
 * and smooth hover effects.
 */
public class SidebarPanel extends JPanel {

    private List<SidebarItem> items = new ArrayList<>();
    private int activeIndex = 0;
    private Runnable onItemChanged;

    public SidebarPanel(String userName, String userRole) {
        setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        setBackground(UIConstants.SIDEBAR_BG);
        setLayout(new BorderLayout());

        // ── Top section: Logo ──────────────────────────────
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(UIConstants.SIDEBAR_BG);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        JLabel logoLabel = new JLabel("🏛 UniRoom");
        logoLabel.setFont(UIConstants.FONT_LOGO);
        logoLabel.setForeground(UIConstants.ACCENT_PRIMARY);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Smart Room Reservation");
        tagline.setFont(UIConstants.FONT_SMALL);
        tagline.setForeground(UIConstants.TEXT_MUTED);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(logoLabel);
        logoPanel.add(Box.createVerticalStrut(4));
        logoPanel.add(tagline);

        // ── Separator ──────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(UIConstants.BORDER_COLOR);
        sep.setBackground(UIConstants.SIDEBAR_BG);
        logoPanel.add(Box.createVerticalStrut(20));
        logoPanel.add(sep);

        add(logoPanel, BorderLayout.NORTH);

        // ── Bottom section: User info ──────────────────────
        JPanel userPanel = new JPanel();
        userPanel.setBackground(UIConstants.SIDEBAR_BG);
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 25, 20));

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(UIConstants.BORDER_COLOR);
        sep2.setBackground(UIConstants.SIDEBAR_BG);
        userPanel.add(sep2);
        userPanel.add(Box.createVerticalStrut(15));

        JLabel userLabel = new JLabel("👤 " + userName);
        userLabel.setFont(UIConstants.FONT_BODY);
        userLabel.setForeground(UIConstants.TEXT_PRIMARY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel("   " + userRole);
        roleLabel.setFont(UIConstants.FONT_SMALL);
        roleLabel.setForeground(UIConstants.ACCENT_PRIMARY);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        userPanel.add(userLabel);
        userPanel.add(Box.createVerticalStrut(4));
        userPanel.add(roleLabel);

        add(userPanel, BorderLayout.SOUTH);
    }

    /**
     * Build the navigation items in the center section.
     * Call this after adding all items with addItem().
     */
    public void buildNavigation() {
        JPanel navPanel = new JPanel();
        navPanel.setBackground(UIConstants.SIDEBAR_BG);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < items.size(); i++) {
            final int index = i;
            SidebarItem item = items.get(i);

            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBackground(index == activeIndex ? UIConstants.SIDEBAR_ACTIVE : UIConstants.SIDEBAR_BG);
            itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            itemPanel.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 20, 48));
            itemPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
            itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel label = new JLabel(item.icon + "   " + item.title);
            label.setFont(UIConstants.FONT_SIDEBAR);
            label.setForeground(index == activeIndex ? UIConstants.ACCENT_PRIMARY : UIConstants.TEXT_SECONDARY);
            itemPanel.add(label, BorderLayout.CENTER);

            // Active indicator bar
            if (index == activeIndex) {
                JPanel indicator = new JPanel();
                indicator.setBackground(UIConstants.ACCENT_PRIMARY);
                indicator.setPreferredSize(new Dimension(3, 48));
                itemPanel.add(indicator, BorderLayout.WEST);
            }

            // Hover effect
            itemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (index != activeIndex) {
                        itemPanel.setBackground(UIConstants.BG_HOVER);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (index != activeIndex) {
                        itemPanel.setBackground(UIConstants.SIDEBAR_BG);
                    }
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    setActiveIndex(index);
                }
            });

            navPanel.add(itemPanel);
            navPanel.add(Box.createVerticalStrut(4));
        }

        // Wrap in scroll pane for many items
        JScrollPane scroll = new JScrollPane(navPanel);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(UIConstants.SIDEBAR_BG);
        scroll.getViewport().setBackground(UIConstants.SIDEBAR_BG);

        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Add a navigation item.
     */
    public void addItem(String icon, String title) {
        items.add(new SidebarItem(icon, title));
    }

    /**
     * Set the active (selected) navigation index.
     */
    public void setActiveIndex(int index) {
        this.activeIndex = index;
        // Rebuild to update visual state
        removeAll();
        // Re-add logo and user panels by rebuilding
        revalidate();
        repaint();

        if (onItemChanged != null) {
            onItemChanged.run();
        }
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    /**
     * Set callback for when a navigation item is clicked.
     */
    public void setOnItemChanged(Runnable callback) {
        this.onItemChanged = callback;
    }

    /**
     * Internal class to hold sidebar item data.
     */
    private static class SidebarItem {
        String icon;
        String title;

        SidebarItem(String icon, String title) {
            this.icon = icon;
            this.title = title;
        }
    }
}
