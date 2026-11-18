package util;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * QRCodeGenerator — Generates a simple QR-like visual code for reservations.
 * 
 * This is a lightweight implementation that creates a unique visual pattern
 * based on the reservation data. For production use, consider using the
 * ZXing library for actual QR code generation.
 */
public class QRCodeGenerator {

    private static final int QR_SIZE = 200;
    private static final int MODULES = 21; // 21x21 grid (similar to QR Version 1)
    private static final int MODULE_SIZE = QR_SIZE / MODULES;

    /**
     * Generates a QR-like image for the given data string.
     * The pattern is deterministically generated from the input data.
     */
    public static BufferedImage generateQRCode(String data) {
        BufferedImage image = new BufferedImage(QR_SIZE + 20, QR_SIZE + 20, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // White background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, QR_SIZE + 20, QR_SIZE + 20);

        int offsetX = 10;
        int offsetY = 10;

        // Generate pattern from data hash
        int hash = data.hashCode();
        boolean[][] grid = new boolean[MODULES][MODULES];

        // Fill grid based on data hash
        for (int row = 0; row < MODULES; row++) {
            for (int col = 0; col < MODULES; col++) {
                int seed = hash ^ (row * 31 + col * 17);
                grid[row][col] = (seed % 3 != 0);
            }
        }

        // Draw finder patterns (top-left, top-right, bottom-left)
        drawFinderPattern(grid, 0, 0);
        drawFinderPattern(grid, 0, MODULES - 7);
        drawFinderPattern(grid, MODULES - 7, 0);

        // Draw the modules
        g2d.setColor(Color.BLACK);
        for (int row = 0; row < MODULES; row++) {
            for (int col = 0; col < MODULES; col++) {
                if (grid[row][col]) {
                    g2d.fillRect(offsetX + col * MODULE_SIZE, offsetY + row * MODULE_SIZE,
                            MODULE_SIZE, MODULE_SIZE);
                }
            }
        }

        g2d.dispose();
        return image;
    }

    /**
     * Draws the characteristic QR finder pattern (the big squares in corners).
     */
    private static void drawFinderPattern(boolean[][] grid, int startRow, int startCol) {
        // Outer border (7x7)
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (startRow + i < MODULES && startCol + j < MODULES) {
                    // Border or inner square
                    boolean isBorder = (i == 0 || i == 6 || j == 0 || j == 6);
                    boolean isInner = (i >= 2 && i <= 4 && j >= 2 && j <= 4);
                    grid[startRow + i][startCol + j] = isBorder || isInner;
                }
            }
        }
    }

    /**
     * Creates a unique reservation code string for QR encoding.
     */
    public static String generateReservationCode(int reservationId, int userId, int roomId, String date) {
        return String.format("UNIROOM-RES-%d-U%d-R%d-%s-%d",
                reservationId, userId, roomId, date.replace("-", ""),
                System.currentTimeMillis() % 10000);
    }
}
