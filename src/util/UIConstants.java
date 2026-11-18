package util;

import java.awt.*;

/**
 * UIConstants — Centralized design system for FindClass.
 * Theme: "Deep Ocean & Neon Mint" (Modern Dark Mode)
 */
public class UIConstants {

    // ── COLORS ───────────────────────────────────────────────────

    // Backgrounds
    public static final Color BG_DARK = new Color(15, 23, 42); // Slate 900 (Main BG)
    public static final Color BG_MEDIUM = new Color(30, 41, 59); // Slate 800 (Cards/Panels)
    public static final Color BG_LIGHT = new Color(51, 65, 85); // Slate 700 (Inputs/Headers)
    public static final Color BG_HOVER = new Color(71, 85, 105); // Slate 600
    public static final Color BG_INPUT = new Color(30, 41, 59);

    // Sidebar
    public static final Color SIDEBAR_BG = new Color(11, 17, 32); // Darker Slate
    public static final Color SIDEBAR_ACTIVE = new Color(30, 41, 59);

    // Accents
    public static final Color ACCENT_PRIMARY = new Color(34, 211, 238); // Cyan 400 (Neon Mint)
    public static final Color ACCENT_SECONDARY = new Color(129, 140, 248); // Indigo 400
    public static final Color ACCENT_PURPLE = new Color(192, 132, 252); // Purple 400
    public static final Color ACCENT_ORANGE = new Color(251, 146, 60); // Orange 400
    public static final Color ACCENT_HOVER = new Color(56, 189, 248); // Lighter Cyan

    // Status Colors
    public static final Color ACCENT_SUCCESS = new Color(52, 211, 153); // Emerald 400
    public static final Color ACCENT_WARNING = new Color(251, 191, 36); // Amber 400
    public static final Color ACCENT_DANGER = new Color(248, 113, 113); // Red 400
    public static final Color ACCENT_INFO = new Color(96, 165, 250); // Blue 400

    // Text
    public static final Color TEXT_PRIMARY = new Color(241, 245, 249); // Slate 100
    public static final Color TEXT_SECONDARY = new Color(203, 213, 225); // Slate 300
    public static final Color TEXT_MUTED = new Color(148, 163, 184); // Slate 400
    public static final Color BORDER_COLOR = new Color(51, 65, 85); // Slate 700
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 60); // Soft shadow

    public static final Color BG_TABLE_ROW = new Color(22, 30, 46);

    // ── GRADIENTS ────────────────────────────────────────────────
    public static final Color GRAD_BLUE_START = new Color(56, 189, 248); // Sky 400
    public static final Color GRAD_BLUE_END = new Color(37, 99, 235); // Blue 600

    public static final Color GRAD_PURPLE_START = new Color(192, 132, 252); // Purple 400
    public static final Color GRAD_PURPLE_END = new Color(124, 58, 237); // Violet 600

    public static final Color GRAD_ORANGE_START = new Color(251, 146, 60); // Orange 400
    public static final Color GRAD_ORANGE_END = new Color(234, 88, 12); // Orange 600

    public static final Color GRAD_GREEN_START = new Color(52, 211, 153); // Emerald 400
    public static final Color GRAD_GREEN_END = new Color(5, 150, 105); // Emerald 600

    // ── FONTS ────────────────────────────────────────────────────
    public static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SIDEBAR = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_TABLE_HEAD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    // ── DIMENSIONS ───────────────────────────────────────────────
    public static final int SIDEBAR_WIDTH = 250;
    public static final int BORDER_RADIUS = 16;
}
