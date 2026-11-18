package gui.components;

import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * RoundedPanel — A JPanel with rounded corners, optional gradient background,
 * and drop shadow effect for the modern card look.
 */
public class RoundedPanel extends JPanel {

    private int radius;
    private Color bgColor;
    private boolean isGradient;
    private Color gradientStart;
    private Color gradientEnd;
    private boolean hasShadow;

    public RoundedPanel() {
        this(UIConstants.BORDER_RADIUS, UIConstants.BG_LIGHT);
    }

    public RoundedPanel(int radius, Color bgColor) {
        this.radius = radius;
        this.bgColor = bgColor;
        setOpaque(false);
        setLayout(new BorderLayout());
    }

    /**
     * Enable gradient background.
     */
    public void setGradient(Color start, Color end) {
        this.gradientStart = start;
        this.gradientEnd = end;
        this.isGradient = true;
        repaint();
    }

    /**
     * Enable drop shadow effect.
     */
    public void setShadow(boolean shadow) {
        this.hasShadow = shadow;
        repaint();
    }

    public void setBgColor(Color color) {
        this.bgColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = hasShadow ? 2 : 0;
        int y = hasShadow ? 2 : 0;
        int w = getWidth() - (hasShadow ? 6 : 0);
        int h = getHeight() - (hasShadow ? 6 : 0);

        // Draw shadow
        if (hasShadow) {
            g2d.setColor(UIConstants.SHADOW_COLOR);
            g2d.fill(new RoundRectangle2D.Float(x + 3, y + 3, w, h, radius, radius));
        }

        // Draw background
        if (isGradient && gradientStart != null && gradientEnd != null) {
            GradientPaint gradient = new GradientPaint(0, 0, gradientStart, w, h, gradientEnd);
            g2d.setPaint(gradient);
        } else {
            g2d.setColor(bgColor);
        }

        g2d.fill(new RoundRectangle2D.Float(x, y, w, h, radius, radius));
        g2d.dispose();
    }
}
