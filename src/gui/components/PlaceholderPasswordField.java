package gui.components;

import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * PlaceholderPasswordField — Styled password field with placeholder text.
 */
public class PlaceholderPasswordField extends JPasswordField {

    private String placeholder;
    private Color placeholderColor = UIConstants.TEXT_MUTED;

    public PlaceholderPasswordField(String placeholder) {
        this.placeholder = placeholder;
        setupStyle();
    }

    private void setupStyle() {
        setFont(UIConstants.FONT_BODY);
        setForeground(UIConstants.TEXT_PRIMARY);
        setBackground(UIConstants.BG_INPUT);
        setCaretColor(UIConstants.ACCENT_PRIMARY);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        setPreferredSize(new Dimension(250, 42));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.ACCENT_PRIMARY, 2),
                        BorderFactory.createEmptyBorder(9, 13, 9, 13)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)));
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getPassword().length == 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setColor(placeholderColor);
            g2d.setFont(getFont());
            Insets insets = getInsets();
            g2d.drawString(placeholder, insets.left, getHeight() / 2 + g2d.getFontMetrics().getAscent() / 2 - 2);
            g2d.dispose();
        }
    }
}
