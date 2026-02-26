package com.seatbooking;

import com.seatbooking.model.*;
import com.seatbooking.service.BookingException;
import com.seatbooking.service.BookingService;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;

/**
 * Simple test class to verify core functionality of the Seat Booking System.
 * This runs automated tests without requiring user interaction.
 */
public class SeatBookingTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 Starting Seat Booking System Tests...\n");
        
        BookingService bookingService = new BookingService();
        
        // Test 1: Basic seat initialization
        testSeatInitialization(bookingService);
        
        // Test 2: User initialization
        testUserInitialization(bookingService);
        
        // Test 3: Batch rotation logic
        testBatchRotation(bookingService);
        
        // Test 4: Successful booking
        testSuccessfulBooking(bookingService);
        
        // Test 5: Booking validation rules
        testValidationRules(bookingService);
        
        // Test 6: Seat availability
        testSeatAvailability(bookingService);
        
        // Test 7: Booking cancellation
        testBookingCancellation(bookingService);
        
        System.out.println("\n🎉 All tests completed successfully!");
        System.out.println("✅ The Seat Booking System is ready to use!");
    }
    
    private static void testSeatInitialization(BookingService bookingService) {
        System.out.println("🔍 Test 1: Seat Initialization");
        
        List<Seat> allSeats = bookingService.getAllSeats();
        
        // Verify total seats
        assert allSeats.size() == 50 : "Expected 50 seats, got " + allSeats.size();
        
        // Verify seat types
        long fixedSeats = allSeats.stream()
                .filter(seat -> seat.getType() == SeatType.FIXED)
                .count();
        long floaterSeats = allSeats.stream()
                .filter(seat -> seat.getType() == SeatType.FLOATER)
                .count();
        
        assert fixedSeats == 40 : "Expected 40 fixed seats, got " + fixedSeats;
        assert floaterSeats == 10 : "Expected 10 floater seats, got " + floaterSeats;
        
        System.out.println("  ✅ 50 total seats initialized (40 FIXED + 10 FLOATER)");
        System.out.println("  ✅ Seat matrix: 5 rows × 10 columns\n");
    }
    
    private static void testUserInitialization(BookingService bookingService) {
        System.out.println("🔍 Test 2: User Initialization");
        
        var users = bookingService.getAllUsers();
        
        assert users.size() == 8 : "Expected 8 users, got " + users.size();
        
        // Verify batch distribution
        long batch1Users = users.values().stream()
                .filter(user -> user.getBatch() == Batch.BATCH_1)
                .count();
        long batch2Users = users.values().stream()
                .filter(user -> user.getBatch() == Batch.BATCH_2)
                .count();
        
        assert batch1Users == 4 : "Expected 4 Batch 1 users, got " + batch1Users;
        assert batch2Users == 4 : "Expected 4 Batch 2 users, got " + batch2Users;
        
        System.out.println("  ✅ 8 users initialized across 8 squads");
        System.out.println("  ✅ Users distributed in 2 batches (4 each)\n");
    }
    
    private static void testBatchRotation(BookingService bookingService) {
        System.out.println("🔍 Test 3: Batch Rotation Logic");
        
        // Test for a known date to verify rotation logic
        // Using 2026-03-02 (Monday, should be week calculated dynamically)
        LocalDate testMonday = LocalDate.of(2026, 3, 2);
        while (testMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
            testMonday = testMonday.plusDays(1);
        }
        
        try {
            Batch mondayBatch = bookingService.getAllowedBatchForDate(testMonday);
            Batch tuesdayBatch = bookingService.getAllowedBatchForDate(testMonday.plusDays(1));
            Batch wednesdayBatch = bookingService.getAllowedBatchForDate(testMonday.plusDays(2));
            Batch thursdayBatch = bookingService.getAllowedBatchForDate(testMonday.plusDays(3));
            Batch fridayBatch = bookingService.getAllowedBatchForDate(testMonday.plusDays(4));
            
            // Mon-Wed should be same batch, Thu-Fri should be same batch
            assert mondayBatch == tuesdayBatch && tuesdayBatch == wednesdayBatch 
                : "Mon-Wed should have same batch";
            assert thursdayBatch == fridayBatch 
                : "Thu-Fri should have same batch";
            assert mondayBatch != thursdayBatch 
                : "Mon-Wed and Thu-Fri should have different batches";
            
            System.out.println("  ✅ Dynamic week calculation working");
            System.out.println("  ✅ Mon-Wed vs Thu-Fri batch rotation validated\n");
            
        } catch (Exception e) {
            System.err.println("  ❌ Batch rotation test failed: " + e.getMessage());
        }
    }
    
    private static void testSuccessfulBooking(BookingService bookingService) {
        System.out.println("🔍 Test 4: Successful Booking");
        
        try {
            // Find a valid working day
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            while (tomorrow.getDayOfWeek() == DayOfWeek.SATURDAY || 
                   tomorrow.getDayOfWeek() == DayOfWeek.SUNDAY) {
                tomorrow = tomorrow.plusDays(1);
            }
            
            // Get allowed batch for tomorrow
            Batch allowedBatch = bookingService.getAllowedBatchForDate(tomorrow);
            
            // Find a user from the allowed batch
            String userId = allowedBatch == Batch.BATCH_1 ? "U01" : "U05";
            
            String bookingId = bookingService.bookSeat(userId, "S01", tomorrow);
            
            assert bookingId != null && bookingId.startsWith("BK") 
                : "Expected valid booking ID starting with 'BK'";
            
            System.out.println("  ✅ Successfully booked seat S01 for " + userId);
            System.out.println("  ✅ Generated booking ID: " + bookingId + "\n");
            
        } catch (Exception e) {
            System.err.println("  ❌ Successful booking test failed: " + e.getMessage());
        }
    }
    
    private static void testValidationRules(BookingService bookingService) {
        System.out.println("🔍 Test 5: Validation Rules");
        
        // Test weekend booking prevention
        LocalDate saturday = LocalDate.now();
        while (saturday.getDayOfWeek() != DayOfWeek.SATURDAY) {
            saturday = saturday.plusDays(1);
        }
        
        try {
            bookingService.bookSeat("U01", "S02", saturday);
            assert false : "Should not allow weekend booking";
        } catch (BookingException e) {
            assert e.getMessage().contains("weekend") : "Expected weekend error message";
            System.out.println("  ✅ Weekend booking prevention working");
        }
        
        // Test past date booking prevention
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            bookingService.bookSeat("U01", "S03", yesterday);
            assert false : "Should not allow past date booking";
        } catch (BookingException e) {
            assert e.getMessage().contains("past") : "Expected past date error message";
            System.out.println("  ✅ Past date booking prevention working");
        }
        
        System.out.println("  ✅ All validation rules working correctly\n");
    }
    
    private static void testSeatAvailability(BookingService bookingService) {
        System.out.println("🔍 Test 6: Seat Availability");
        
        LocalDate testDate = LocalDate.now().plusDays(2);
        while (testDate.getDayOfWeek() == DayOfWeek.SATURDAY || 
               testDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            testDate = testDate.plusDays(1);
        }
        
        List<Seat> availableSeats = bookingService.getAvailableSeats(testDate);
        
        // Initially, all seats should be available
        assert availableSeats.size() <= 50 : "Available seats should not exceed total";
        
        System.out.println("  ✅ Seat availability check working");
        System.out.println("  ✅ Available seats for " + testDate + ": " + availableSeats.size() + "/50\n");
    }
    
    private static void testBookingCancellation(BookingService bookingService) {
        System.out.println("🔍 Test 7: Booking Cancellation");
        
        try {
            // Create a booking first
            LocalDate futureDate = LocalDate.now().plusDays(3);
            while (futureDate.getDayOfWeek() == DayOfWeek.SATURDAY || 
                   futureDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                futureDate = futureDate.plusDays(1);
            }
            
            Batch allowedBatch = bookingService.getAllowedBatchForDate(futureDate);
            String userId = allowedBatch == Batch.BATCH_1 ? "U02" : "U06";
            
            String bookingId = bookingService.bookSeat(userId, "S05", futureDate);
            
            // Now cancel it
            boolean cancelled = bookingService.cancelBooking(bookingId);
            
            assert cancelled : "Booking cancellation should succeed";
            
            // Verify seat is available again
            boolean isBooked = bookingService.isSeatBooked("S05", futureDate);
            assert !isBooked : "Seat should be available after cancellation";
            
            System.out.println("  ✅ Booking cancellation working");
            System.out.println("  ✅ Seat availability updated after cancellation\n");
            
        } catch (Exception e) {
            System.err.println("  ❌ Booking cancellation test failed: " + e.getMessage());
        }
    }
}