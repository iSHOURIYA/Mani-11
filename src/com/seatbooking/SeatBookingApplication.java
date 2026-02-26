package com.seatbooking;

import com.seatbooking.service.BookingService;
import com.seatbooking.ui.TerminalUI;

/**
 * Main application class for the Seat Booking System.
 * 
 * This is a Java 17 console-based seat booking system with the following features:
 * 
 * System Configuration:
 * - 50 total seats (40 FIXED + 10 FLOATER)
 * - 8 squads organized in 2 batches
 * - Dynamic weekly rotation between batches
 * - Interactive terminal UI with ANSI colors
 * 
 * Business Rules:
 * 1. One user can book only 1 seat per day
 * 2. No double booking prevention
 * 3. FIXED seats: Book any working day, up to 14 days in advance, anytime
 * 4. FLOATER seats: Book only after 3 PM, only for tomorrow
 * 5. Dynamic rotation logic:
 *    - Even weeks: Mon-Wed → Batch 1, Thu-Fri → Batch 2
 *    - Odd weeks: Mon-Wed → Batch 2, Thu-Fri → Batch 1
 * 
 * Architecture:
 * - Clean OOP design with separation of concerns
 * - Model classes: User, Seat, Booking
 * - Service layer: BookingService with O(1) conflict checking
 * - Terminal UI with ASCII art and ANSI colors
 * - In-memory storage using HashMap for efficiency
 * 
 * Usage:
 * - Compile and run this main class
 * - Follow the interactive menu prompts
 * - Use sample user IDs: U01-U08 (representing different squads)
 * - Use seat IDs: S01-S50 (S01-S40 are FIXED, S41-S50 are FLOATER)
 * 
 * @author Seat Booking System
 * @version 1.0
 */
public class SeatBookingApplication {
    
    public static void main(String[] args) {
        try {
            // Initialize the booking service
            BookingService bookingService = new BookingService();
            
            // Create and start the terminal UI
            TerminalUI terminalUI = new TerminalUI(bookingService);
            terminalUI.start();
            
        } catch (Exception e) {
            System.err.println("Failed to start the Seat Booking System: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}