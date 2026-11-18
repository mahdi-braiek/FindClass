package util;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * ExportUtil — Utility to export any Swing JPanel/Component
 * as a PNG image file. Used for timetable and reservation exports.
 */
public class ExportUtil {

    /**
     * Export a JComponent to a PNG image.
     * Prompts user for file save location.
     *
     * @param component   The component to capture
     * @param parent      Parent component for file dialog
     * @param defaultName Default file name (without extension)
     */
    public static void exportToPNG(JComponent component, Component parent, String defaultName) {
        // Ensure the component has been laid out
        Dimension size = component.getPreferredSize();
        if (size.width <= 0 || size.height <= 0) {
            size = component.getSize();
        }
        if (size.width <= 0 || size.height <= 0) {
            JOptionPane.showMessageDialog(parent,
                    "Le composant est vide ou n'a pas de dimensions.",
                    "Erreur d'export", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // File chooser
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exporter en PNG");
        chooser.setSelectedFile(new File(defaultName + ".png"));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images PNG", "png"));

        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION)
            return;

        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".png")) {
            file = new File(file.getAbsolutePath() + ".png");
        }

        try {
            // Create image buffer
            BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();

            // High quality rendering
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Paint the component onto the image
            component.paint(g2d);
            g2d.dispose();

            // Write to file
            ImageIO.write(image, "png", file);
            JOptionPane.showMessageDialog(parent,
                    "✅ Image exportée avec succès !\n" + file.getAbsolutePath(),
                    "Export réussi", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent,
                    "❌ Erreur lors de l'export : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Export a JTable to a PNG image (renders the table with header).
     */
    public static void exportTableToPNG(JTable table, Component parent, String defaultName) {
        // Get table + header dimensions
        JTableHeader header = table.getTableHeader();
        int headerHeight = header != null ? header.getPreferredSize().height : 0;
        int width = table.getPreferredSize().width;
        int height = table.getPreferredSize().height + headerHeight;

        if (width <= 0 || height <= 0) {
            JOptionPane.showMessageDialog(parent,
                    "La table est vide.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // File chooser
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exporter la table en PNG");
        chooser.setSelectedFile(new File(defaultName + ".png"));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images PNG", "png"));

        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION)
            return;

        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".png")) {
            file = new File(file.getAbsolutePath() + ".png");
        }

        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Paint header then table
            if (header != null) {
                header.setSize(width, headerHeight);
                header.paint(g2d);
                g2d.translate(0, headerHeight);
            }
            table.setSize(width, table.getPreferredSize().height);
            table.paint(g2d);

            g2d.dispose();
            ImageIO.write(image, "png", file);
            JOptionPane.showMessageDialog(parent,
                    "✅ Table exportée avec succès !\n" + file.getAbsolutePath(),
                    "Export réussi", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent,
                    "❌ Erreur lors de l'export : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
