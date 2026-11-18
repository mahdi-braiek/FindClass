package gui.components;

import util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * AppLogo — Un logo vectoriel personnalisé dessiné avec Graphics2D.
 * Représente un "U" stylisé avec un toit académique (mortier) au-dessus,
 * dans les tons dégradés de l'application.
 */
public class AppLogo extends JPanel {

        private int width;
        private int height;

        public AppLogo(int width, int height) {
                this.width = width;
                this.height = height;
                setPreferredSize(new Dimension(width, height));
                setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawLogo((Graphics2D) g, width, height);
        }

        /**
         * Create a BufferedImage of the logo to use as the application icon.
         * 
         * @return 128x128 Image icon
         */
        public static Image createAppIcon() {
                int size = 128;
                java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                                size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = image.createGraphics();
                drawLogo(g2d, size, size);
                g2d.dispose();
                return image;
        }

        private static void drawLogo(Graphics2D g2d, int width, int height) {
                // Anti-aliasing de qualité
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int centerX = width / 2;
                int centerY = height / 2;

                // Dégradé principal (Blanc -> Cyan pour contraste maximal)
                GradientPaint grad = new GradientPaint(
                                0, 0, Color.WHITE,
                                width, height, UIConstants.ACCENT_PRIMARY);
                g2d.setPaint(grad);

                // 1. Le toit (Chapeau académique simplifié / Toit université)
                Path2D roof = new Path2D.Float();
                roof.moveTo(centerX, centerY - height * 0.4); // Pointe haut
                roof.lineTo(centerX + width * 0.4, centerY - height * 0.15); // Coin droit
                roof.lineTo(centerX, centerY + height * 0.1); // Pointe bas
                roof.lineTo(centerX - width * 0.4, centerY - height * 0.15); // Coin gauche
                roof.closePath();
                g2d.fill(roof);

                // 2. La base "U" (Université / UniRoom)
                // Deux piliers verticaux avec un arc en bas
                int pillarW = (int) (width * 0.12);
                int pillarH = (int) (height * 0.35);
                int arcH = (int) (height * 0.15);

                // Pilier gauche
                g2d.fillRoundRect(centerX - (int) (width * 0.25), centerY, pillarW, pillarH, 8, 8);
                // Pilier droit
                g2d.fillRoundRect(centerX + (int) (width * 0.25) - pillarW, centerY, pillarW, pillarH, 8, 8);

                // Arc de connexion en bas
                g2d.drawArc(centerX - (int) (width * 0.25), centerY + pillarH - arcH,
                                (int) (width * 0.5), arcH * 2, 180, 180);

                // On remplit l'arc manuellement pour faire propre
                Path2D arc = new Path2D.Float();
                arc.moveTo(centerX - (int) (width * 0.25), centerY + pillarH - 5);
                arc.curveTo(centerX - (int) (width * 0.25), centerY + pillarH + arcH,
                                centerX + (int) (width * 0.25), centerY + pillarH + arcH,
                                centerX + (int) (width * 0.25), centerY + pillarH - 5);
                arc.lineTo(centerX + (int) (width * 0.25) - pillarW, centerY + pillarH - 5);
                arc.curveTo(centerX + (int) (width * 0.25) - pillarW, centerY + pillarH + arcH - pillarW,
                                centerX - (int) (width * 0.25) + pillarW, centerY + pillarH + arcH - pillarW,
                                centerX - (int) (width * 0.25) + pillarW, centerY + pillarH - 5);
                arc.closePath();
                g2d.fill(arc);

                // 3. Petit accent brillant style "néon"
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.fillOval(centerX - width / 4, centerY - height / 4, 6, 6);
        }
}
