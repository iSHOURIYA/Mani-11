package com.seatbooking.ui;

/**
 * ANSI color codes and console utilities for terminal UI.
 */
public class ConsoleColors {
    // Reset
    public static final String RESET = "\033[0m";
    
    // Regular Colors
    public static final String BLACK = "\033[0;30m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String WHITE = "\033[0;37m";
    
    // Bold
    public static final String BOLD_BLACK = "\033[1;30m";
    public static final String BOLD_RED = "\033[1;31m";
    public static final String BOLD_GREEN = "\033[1;32m";
    public static final String BOLD_YELLOW = "\033[1;33m";
    public static final String BOLD_BLUE = "\033[1;34m";
    public static final String BOLD_PURPLE = "\033[1;35m";
    public static final String BOLD_CYAN = "\033[1;36m";
    public static final String BOLD_WHITE = "\033[1;37m";
    
    // Background Colors
    public static final String BG_RED = "\033[41m";
    public static final String BG_GREEN = "\033[42m";
    public static final String BG_YELLOW = "\033[43m";
    public static final String BG_BLUE = "\033[44m";
    public static final String BG_WHITE = "\033[47m";
    
    // Emojis (optional but clean)
    public static final String SEAT_ICON = "\uD83D\uDCBA";     // 💺
    public static final String CHECK_MARK = "\u2713";          // ✓
    public static final String CROSS_MARK = "\u2717";          // ✗
    public static final String CALENDAR = "\uD83D\uDCC5";      // 📅
    public static final String USER_ICON = "\uD83D\uDC64";     // 👤
    public static final String ARROW_RIGHT = "\u279C";         // ➜
    
    /**
     * Clears the console screen.
     */
    public static void clearScreen() {
        System.out.print("\033[2J\033[H");
    }
    
    /**
     * Prints a colored message.
     */
    public static void printColored(String message, String color) {
        System.out.print(color + message + RESET);
    }
    
    /**
     * Prints a colored message with newline.
     */
    public static void printlnColored(String message, String color) {
        System.out.println(color + message + RESET);
    }
    
    /**
     * Prints an error message in red.
     */
    public static void printError(String message) {
        printlnColored(CROSS_MARK + " ERROR: " + message, BOLD_RED);
    }
    
    /**
     * Prints a success message in green.
     */
    public static void printSuccess(String message) {
        printlnColored(CHECK_MARK + " SUCCESS: " + message, BOLD_GREEN);
    }
    
    /**
     * Prints an info message in cyan.
     */
    public static void printInfo(String message) {
        printlnColored(ARROW_RIGHT + " " + message, CYAN);
    }
    
    /**
     * Creates a horizontal line with specified character and length.
     */
    public static String createLine(char character, int length) {
        return String.valueOf(character).repeat(length);
    }
    
    /**
     * Centers text within a specified width.
     */
    public static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        
        int padding = (width - text.length()) / 2;
        int rightPadding = width - text.length() - padding;
        return " ".repeat(padding) + text + " ".repeat(rightPadding);
    }
}