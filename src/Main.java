import gui.LoginFrame;
import util.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Main — Application entry point.
 * Sets the dark Look & Feel and launches the login screen.
 */
public class Main {

    public static void main(String[] args) {
        // Set dark UI defaults before creating any Swing components
        configureDarkLookAndFeel();

        // Launch the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║   FindClass — Application Started    ║");
            System.out.println("║   Smart University Room Management   ║");
            System.out.println("╚══════════════════════════════════════╝");
        });
    }

    /**
     * Configure Swing's UIManager for a dark theme across all components.
     */
    private static void configureDarkLookAndFeel() {
        try {
            // Use system look and feel as base
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ── Global dark theme overrides ─────────────────────
        Color bg = UIConstants.BG_DARK;
        Color bgMed = UIConstants.BG_MEDIUM;
        Color bgInput = UIConstants.BG_INPUT;
        Color text = UIConstants.TEXT_PRIMARY;
        Color textSec = UIConstants.TEXT_SECONDARY;
        Color border = UIConstants.BORDER_COLOR;
        Color accent = UIConstants.ACCENT_PRIMARY;

        // Panel & Container
        UIManager.put("Panel.background", bg);
        UIManager.put("Panel.foreground", text);

        // Labels
        UIManager.put("Label.foreground", text);

        // Text fields & inputs
        UIManager.put("TextField.background", bgInput);
        UIManager.put("TextField.foreground", text);
        UIManager.put("TextField.caretForeground", accent);
        UIManager.put("PasswordField.background", bgInput);
        UIManager.put("PasswordField.foreground", text);
        UIManager.put("PasswordField.caretForeground", accent);
        UIManager.put("TextArea.background", bgInput);
        UIManager.put("TextArea.foreground", text);

        // Combo box
        UIManager.put("ComboBox.background", bgInput);
        UIManager.put("ComboBox.foreground", text);
        UIManager.put("ComboBox.selectionBackground", accent);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);

        // Table
        UIManager.put("Table.background", bgMed);
        UIManager.put("Table.foreground", text);
        UIManager.put("Table.selectionBackground", accent.darker());
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", border);
        UIManager.put("TableHeader.background", UIConstants.BG_LIGHT);
        UIManager.put("TableHeader.foreground", textSec);

        // Scroll pane
        UIManager.put("ScrollPane.background", bg);
        UIManager.put("ScrollBar.background", bg);
        UIManager.put("ScrollBar.thumb", border);
        UIManager.put("ScrollBar.track", bg);

        // Spinner
        UIManager.put("Spinner.background", bgInput);
        UIManager.put("Spinner.foreground", text);

        // Option panes & dialogs
        UIManager.put("OptionPane.background", bgMed);
        UIManager.put("OptionPane.foreground", text);
        UIManager.put("OptionPane.messageForeground", text);
        UIManager.put("OptionPane.messageFont", UIConstants.FONT_BODY);
        UIManager.put("Button.background", accent);
        UIManager.put("Button.foreground", Color.WHITE);

        // List
        UIManager.put("List.background", bgMed);
        UIManager.put("List.foreground", text);
        UIManager.put("List.selectionBackground", accent);
        UIManager.put("List.selectionForeground", Color.WHITE);

        // Viewport
        UIManager.put("Viewport.background", bg);

        // Menu
        UIManager.put("Menu.background", bgMed);
        UIManager.put("Menu.foreground", text);
        UIManager.put("MenuBar.background", bgMed);
        UIManager.put("MenuItem.background", bgMed);
        UIManager.put("MenuItem.foreground", text);

        // Separator
        UIManager.put("Separator.foreground", border);

        // ToolTip
        UIManager.put("ToolTip.background", UIConstants.BG_LIGHT);
        UIManager.put("ToolTip.foreground", text);

        // Make all fonts use Segoe UI
        Font defaultFont = UIConstants.FONT_BODY;
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", UIConstants.FONT_BUTTON);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("Table.font", UIConstants.FONT_TABLE);
        UIManager.put("TableHeader.font", UIConstants.FONT_TABLE_HEAD);
    }
}
