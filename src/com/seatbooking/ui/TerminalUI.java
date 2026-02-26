package com.seatbooking.ui;

import com.seatbooking.model.*;
import com.seatbooking.service.BookingException;
import com.seatbooking.service.BookingService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static com.seatbooking.ui.ConsoleColors.*;

/**
 * Terminal-based user interface for the seat booking system.
 * Provides a clean, interactive console experience with ANSI colors and ASCII art.
 */
public class TerminalUI {
    
    private static final int BOX_WIDTH = 80;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final BookingService bookingService;
    private final Scanner scanner;
    
    public TerminalUI(BookingService bookingService) {
        this.bookingService = bookingService;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Starts the main UI loop.
     */
    public void start() {
        printWelcomeMessage();
        
        while (true) {
            try {
                printMainMenu();
                int choice = getMenuChoice();
                
                switch (choice) {
                    case 1 -> viewAvailableSeats();
                    case 2 -> bookSeat();
                    case 3 -> cancelBooking();
                    case 4 -> viewMyBookings();
                    case 5 -> {
                        printGoodbyeMessage();
                        return;
                    }
                    default -> printError("Invalid choice. Please try again.");
                }
                
                if (choice != 5) {
                    pressEnterToContinue();
                }
                
            } catch (Exception e) {
                printError("An unexpected error occurred: " + e.getMessage());
                pressEnterToContinue();
            }
        }
    }
    
    /**
     * Displays welcome message with ASCII art.
     */
    private void printWelcomeMessage() {
        clearScreen();
        printBox();
        printlnColored(centerText("🎯 SEAT BOOKING SYSTEM 🎯", BOX_WIDTH - 4), BOLD_CYAN);
        printlnColored(centerText("Welcome to the Interactive Seat Reservation System", BOX_WIDTH - 4), WHITE);
        printBox();
        System.out.println();
        
        printInfo("System Info:");
        printlnColored("  • Total Seats: 50 (40 Fixed + 10 Floater)", WHITE);
        printlnColored("  • 8 Squads organized in 2 Batches", WHITE);
        printlnColored("  • Dynamic weekly rotation (Mon-Wed vs Thu-Fri)", WHITE);
        printlnColored("  • Fixed: Book up to 14 days ahead, any time", WHITE);
        printlnColored("  • Floater: Book only after 3 PM for tomorrow", WHITE);
        System.out.println();
    }
    
    /**
     * Displays main menu.
     */
    private void printMainMenu() {
        printBox();
        printlnColored(centerText("📋 MAIN MENU", BOX_WIDTH - 4), BOLD_YELLOW);
        printBox();
        
        System.out.println();
        printlnColored("  1. " + CALENDAR + "  View Available Seats (by date)", WHITE);
        printlnColored("  2. " + SEAT_ICON + "  Book Seat", WHITE);
        printlnColored("  3. " + CROSS_MARK + "  Cancel Booking", WHITE);
        printlnColored("  4. " + USER_ICON + "  View My Bookings", WHITE);
        printlnColored("  5. " + "🚪" + "  Exit", WHITE);
        System.out.println();
        
        printColored("Enter your choice (1-5): ", BOLD_WHITE);
    }
    
    /**
     * Gets menu choice from user with validation.
     */
    private int getMenuChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > 5) {
                return -1; // Invalid choice
            }
            return choice;
        } catch (NumberFormatException e) {
            return -1; // Invalid input
        }
    }
    
    /**
     * Handles viewing available seats for a specific date.
     */
    private void viewAvailableSeats() {
        clearScreen();
        printSectionHeader("📅 VIEW AVAILABLE SEATS");
        
        LocalDate date = promptForDate("Enter date (yyyy-MM-dd): ");
        if (date == null) return;
        
        try {
            Batch allowedBatch = bookingService.getAllowedBatchForDate(date);
            printInfo("Allowed batch for " + date + ": " + allowedBatch.getDisplayName());
            System.out.println();
            
            displaySeatMatrix(date);
            
            List<Seat> availableSeats = bookingService.getAvailableSeats(date);
            List<Booking> bookings = bookingService.getBookingsForDate(date);
            
            System.out.println();
            printlnColored("📊 SUMMARY FOR " + date.format(DATE_FORMATTER).toUpperCase(), BOLD_CYAN);
            System.out.println(createLine('-', BOX_WIDTH));
            
            printlnColored(String.format("Available Seats: %d/%d", 
                         availableSeats.size(), 50), GREEN);
            printlnColored(String.format("Booked Seats: %d/%d", 
                         bookings.size(), 50), YELLOW);
            
            if (!bookings.isEmpty()) {
                System.out.println();
                printlnColored("🗂️  BOOKINGS:", BOLD_WHITE);
                for (Booking booking : bookings) {
                    System.out.printf("   %-8s | %-15s | %s%n", 
                                    booking.getSeat().getSeatId(),
                                    booking.getUser().getName(),
                                    booking.getUser().getSquad());
                }
            }
            
        } catch (IllegalArgumentException e) {
            printError(e.getMessage());
        }
    }
    
    /**
     * Displays the seat matrix with current booking status.
     */
    private void displaySeatMatrix(LocalDate date) {
        printlnColored("🎭 SEAT LAYOUT (F=Fixed, L=Floater, X=Booked)", BOLD_WHITE);
        System.out.println();
        
        // Column headers
        System.out.print("     ");
        for (int col = 1; col <= 10; col++) {
            System.out.printf("%3d", col);
        }
        System.out.println();
        
        // Top border
        System.out.print("   ┌─");
        for (int col = 1; col <= 10; col++) {
            System.out.print("───");
        }
        System.out.println("┐");
        
        List<Seat> allSeats = bookingService.getAllSeats();
        int seatIndex = 0;
        
        for (int row = 1; row <= 5; row++) {
            System.out.printf("%2d │ ", row);
            
            for (int col = 1; col <= 10; col++) {
                Seat seat = allSeats.get(seatIndex++);
                char displayChar;
                String color;
                
                if (bookingService.isSeatBooked(seat.getSeatId(), date)) {
                    displayChar = 'X';
                    color = BG_RED + BOLD_WHITE;
                } else if (seat.getType() == SeatType.FIXED) {
                    displayChar = 'F';
                    color = BG_GREEN + BOLD_WHITE;
                } else {
                    displayChar = 'L';
                    color = BG_BLUE + BOLD_WHITE;
                }
                
                printColored(String.format(" %c ", displayChar), color);
            }
            System.out.println(" │");
        }
        
        // Bottom border
        System.out.print("   └─");
        for (int col = 1; col <= 10; col++) {
            System.out.print("───");
        }
        System.out.println("┘");
        
        // Legend
        System.out.println();
        printColored(" F ", BG_GREEN + BOLD_WHITE);
        System.out.print(" = Fixed   ");
        printColored(" L ", BG_BLUE + BOLD_WHITE);
        System.out.print(" = Floater   ");
        printColored(" X ", BG_RED + BOLD_WHITE);
        System.out.println(" = Booked");
    }
    
    /**
     * Handles seat booking process.
     */
    private void bookSeat() {
        clearScreen();
        printSectionHeader("🎫 BOOK SEAT");
        
        String userId = promptForInput("Enter User ID (e.g., U01): ");
        if (userId == null || userId.trim().isEmpty()) {
            printError("User ID is required.");
            return;
        }
        
        Map<String, User> users = bookingService.getAllUsers();
        if (!users.containsKey(userId.trim().toUpperCase())) {
            printError("User not found. Available users: " + users.keySet());
            return;
        }
        
        String seatId = promptForInput("Enter Seat ID (e.g., S01): ");
        if (seatId == null || seatId.trim().isEmpty()) {
            printError("Seat ID is required.");
            return;
        }
        
        LocalDate date = promptForDate("Enter booking date (yyyy-MM-dd): ");
        if (date == null) return;
        
        try {
            String bookingId = bookingService.bookSeat(
                userId.trim().toUpperCase(), 
                seatId.trim().toUpperCase(), 
                date);
            
            printSuccess("Seat booked successfully!");
            printInfo("Booking ID: " + bookingId);
            printInfo("User: " + userId.toUpperCase());
            printInfo("Seat: " + seatId.toUpperCase());
            printInfo("Date: " + date.format(DATE_FORMATTER));
            
        } catch (BookingException e) {
            printError(e.getMessage());
        }
    }
    
    /**
     * Handles booking cancellation.
     */
    private void cancelBooking() {
        clearScreen();
        printSectionHeader("❌ CANCEL BOOKING");
        
        String bookingId = promptForInput("Enter Booking ID: ");
        if (bookingId == null || bookingId.trim().isEmpty()) {
            printError("Booking ID is required.");
            return;
        }
        
        boolean success = bookingService.cancelBooking(bookingId.trim());
        
        if (success) {
            printSuccess("Booking cancelled successfully!");
        } else {
            printError("Booking not found: " + bookingId.trim());
        }
    }
    
    /**
     * Handles viewing user's bookings.
     */
    private void viewMyBookings() {
        clearScreen();
        printSectionHeader("📖 MY BOOKINGS");
        
        String userId = promptForInput("Enter User ID: ");
        if (userId == null || userId.trim().isEmpty()) {
            printError("User ID is required.");
            return;
        }
        
        List<Booking> userBookings = bookingService.getUserBookings(userId.trim().toUpperCase());
        
        if (userBookings.isEmpty()) {
            printInfo("No bookings found for user: " + userId.toUpperCase());
        } else {
            printlnColored("📋 Bookings for " + userId.toUpperCase() + ":", BOLD_WHITE);
            System.out.println(createLine('-', BOX_WIDTH));
            
            System.out.printf("%-15s | %-8s | %-12s | %-20s%n", 
                            "Booking ID", "Seat", "Date", "Booking Time");
            System.out.println(createLine('-', BOX_WIDTH));
            
            for (Booking booking : userBookings) {
                System.out.printf("%-15s | %-8s | %-12s | %-20s%n",
                                booking.getBookingId(),
                                booking.getSeat().getSeatId(),
                                booking.getDate().format(DATE_FORMATTER),
                                booking.getBookingTime().format(
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        }
    }
    
    /**
     * Prompts user for a date input with validation.
     */
    private LocalDate promptForDate(String prompt) {
        printColored(prompt, BOLD_WHITE);
        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
            printError("Date is required.");
            return null;
        }
        
        try {
            return LocalDate.parse(input, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            printError("Invalid date format. Please use yyyy-MM-dd (e.g., 2026-02-28)");
            return null;
        }
    }
    
    /**
     * Prompts user for generic input.
     */
    private String promptForInput(String prompt) {
        printColored(prompt, BOLD_WHITE);
        return scanner.nextLine();
    }
    
    /**
     * Prints section header with styling.
     */
    private void printSectionHeader(String title) {
        printBox();
        printlnColored(centerText(title, BOX_WIDTH - 4), BOLD_CYAN);
        printBox();
        System.out.println();
    }
    
    /**
     * Prints a decorative box line.
     */
    private void printBox() {
        printlnColored(createLine('═', BOX_WIDTH), BOLD_BLUE);
    }
    
    /**
     * Prints goodbye message.
     */
    private void printGoodbyeMessage() {
        clearScreen();
        printBox();
        printlnColored(centerText("👋 Thank you for using Seat Booking System!", BOX_WIDTH - 4), BOLD_GREEN);
        printlnColored(centerText("Have a great day! 🌟", BOX_WIDTH - 4), CYAN);
        printBox();
    }
    
    /**
     * Waits for user to press Enter.
     */
    private void pressEnterToContinue() {
        System.out.println();
        printColored("Press Enter to continue...", BOLD_BLACK);
        scanner.nextLine();
        clearScreen();
    }
}