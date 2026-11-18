package gui.components;

import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * RoundedButton — A modern button with rounded corners, hover effects, and
 * gradient support.
 */
public class RoundedButton extends JButton {

    private Color normalColor;
    private Color hoverColor;
    private Color pressColor;
    private Color currentColor;
    private int radius;
    private boolean isGradient;
    private Color gradientEnd;

    public RoundedButton(String text) {
        this(text, UIConstants.ACCENT_PRIMARY, UIConstants.ACCENT_HOVER);
    }

    public RoundedButton(String text, Color normal, Color hover) {
        super(text);
        this.normalColor = normal;
        this.hoverColor = hover;
        this.pressColor = normal.darker();
        this.currentColor = normal;
        this.radius = UIConstants.BORDER_RADIUS;

        // Remove default button styling
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(UIConstants.FONT_BUTTON);
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(140, 40));

        // Hover and click animations
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentColor = hoverColor;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentColor = normalColor;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                currentColor = pressColor;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentColor = hoverColor;
                repaint();
            }
        });
    }

    /**
     * Enable gradient rendering with a second color.
     */
    public void setGradient(Color start, Color end) {
        this.normalColor = start;
        this.gradientEnd = end;
        this.isGradient = true;
        this.currentColor = start;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded background
        RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);

        if (isGradient && gradientEnd != null) {
            GradientPaint gradient = new GradientPaint(0, 0, currentColor, getWidth(), getHeight(), gradientEnd);
            g2d.setPaint(gradient);
        } else {
            g2d.setColor(currentColor);
        }

        g2d.fill(shape);

        // Draw text centered
        g2d.setColor(getForeground());
        g2d.setFont(getFont());
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(getText(), x, y);

        g2d.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // No border
    }
}
