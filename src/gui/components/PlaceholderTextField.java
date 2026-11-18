package gui.components;

import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * PlaceholderTextField — A styled text field with placeholder text
 * that disappears when the user starts typing.
 */
public class PlaceholderTextField extends JTextField {

    private String placeholder;
    private Color placeholderColor = UIConstants.TEXT_MUTED;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
        setupStyle();
    }

    public PlaceholderTextField(String placeholder, int columns) {
        super(columns);
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

        // Focus effect: change border color
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

        // Draw placeholder text when field is empty and not focused
        if (getText().isEmpty()) {
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
