package com.seatbooking.service;

import com.seatbooking.model.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class that handles all seat booking operations and business rules.
 * Implements efficient O(1) conflict checking using HashMap and HashSet.
 */
public class BookingService {
    
    private static final int TOTAL_SEATS = 50;
    private static final int FIXED_SEATS = 40;
    private static final int FLOATER_SEATS = 10;
    private static final int ROWS = 5;
    private static final int COLUMNS = 10;
    private static final LocalTime FLOATER_BOOKING_TIME = LocalTime.of(15, 0); // 3 PM
    
    // In-memory storage
    private final Map<LocalDate, List<Booking>> bookingsByDate;
    private final Map<String, Booking> bookingsById;
    private final List<Seat> allSeats;
    private final Map<String, User> users;
    
    // Efficient lookup structures
    private final Map<LocalDate, Set<String>> bookedSeatsPerDate;
    private final Map<LocalDate, Set<String>> bookedUsersPerDate;
    
    public BookingService() {
        this.bookingsByDate = new HashMap<>();
        this.bookingsById = new HashMap<>();
        this.allSeats = initializeSeats();
        this.users = initializeUsers();
        this.bookedSeatsPerDate = new HashMap<>();
        this.bookedUsersPerDate = new HashMap<>();
    }
    
    /**
     * Initialize all 50 seats (40 FIXED + 10 FLOATER) in a 5x10 matrix.
     */
    private List<Seat> initializeSeats() {
        List<Seat> seats = new ArrayList<>();
        int seatCounter = 1;
        
        // Create seats in row-major order
        for (int row = 1; row <= ROWS; row++) {
            for (int col = 1; col <= COLUMNS; col++) {
                String seatId = String.format("S%02d", seatCounter);
                // First 40 seats are FIXED, last 10 are FLOATER
                SeatType type = seatCounter <= FIXED_SEATS ? SeatType.FIXED : SeatType.FLOATER;
                seats.add(new Seat(seatId, type, row, col));
                seatCounter++;
            }
        }
        
        return seats;
    }
    
    /**
     * Initialize sample users for demonstration.
     */
    private Map<String, User> initializeUsers() {
        Map<String, User> userMap = new HashMap<>();
        
        // Create sample users across different squads
        Squad[] squads = Squad.values();
        for (int i = 0; i < squads.length; i++) {
            String userId = String.format("U%02d", i + 1);
            String name = String.format("User %d", i + 1);
            User user = new User(userId, name, squads[i]);
            userMap.put(userId, user);
        }
        
        return userMap;
    }
    
    /**
     * Determines which batch can book for a given date using dynamic rotation logic.
     */
    public Batch getAllowedBatchForDate(LocalDate date) {
        if (isWeekend(date)) {
            throw new IllegalArgumentException("Booking not allowed on weekends");
        }
        
        // Calculate week number from epoch (1970-01-05 was a Monday - week 1)
        LocalDate epoch = LocalDate.of(1970, 1, 5);
        long weekNumber = ChronoUnit.WEEKS.between(epoch, date.with(DayOfWeek.MONDAY)) + 1;
        boolean isEvenWeek = weekNumber % 2 == 0;
        
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        boolean isMonToWed = dayOfWeek.getValue() <= 3; // Mon=1, Tue=2, Wed=3
        
        if (isEvenWeek) {
            return isMonToWed ? Batch.BATCH_1 : Batch.BATCH_2;
        } else {
            return isMonToWed ? Batch.BATCH_2 : Batch.BATCH_1;
        }
    }
    
    /**
     * Books a seat for a user on a specific date.
     * Validates all business rules before booking.
     */
    public String bookSeat(String userId, String seatId, LocalDate date) throws BookingException {
        User user = users.get(userId);
        if (user == null) {
            throw new BookingException("User not found: " + userId);
        }
        
        Seat seat = findSeatById(seatId);
        if (seat == null) {
            throw new BookingException("Seat not found: " + seatId);
        }
        
        validateBookingRules(user, seat, date);
        
        // Create booking
        String bookingId = generateBookingId();
        Booking booking = new Booking(bookingId, user, seat, date);
        
        // Store booking
        bookingsByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(booking);
        bookingsById.put(bookingId, booking);
        
        // Update efficient lookup structures
        bookedSeatsPerDate.computeIfAbsent(date, k -> new HashSet<>()).add(seatId);
        bookedUsersPerDate.computeIfAbsent(date, k -> new HashSet<>()).add(userId);
        
        return bookingId;
    }
    
    /**
     * Validates all booking rules for a specific booking request.
     */
    private void validateBookingRules(User user, Seat seat, LocalDate date) throws BookingException {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        
        // Rule: No booking on weekends
        if (isWeekend(date)) {
            throw new BookingException("Booking not allowed on weekends");
        }
        
        // Rule: No booking for past dates
        if (date.isBefore(today)) {
            throw new BookingException("Cannot book seats for past dates");
        }
        
        // Rule: One user can book only 1 seat per day
        Set<String> usersForDate = bookedUsersPerDate.get(date);
        if (usersForDate != null && usersForDate.contains(user.getUserId())) {
            throw new BookingException("User already has a booking for " + date);
        }
        
        // Rule: Prevent double booking of same seat for same date
        Set<String> seatsForDate = bookedSeatsPerDate.get(date);
        if (seatsForDate != null && seatsForDate.contains(seat.getSeatId())) {
            throw new BookingException("Seat " + seat.getSeatId() + " is already booked for " + date);
        }
        
        // Rule: Batch rotation validation
        Batch allowedBatch = getAllowedBatchForDate(date);
        if (user.getBatch() != allowedBatch) {
            throw new BookingException(String.format("Only %s can book for %s", 
                                     allowedBatch.getDisplayName(), date));
        }
        
        // Seat-specific rules
        if (seat.getType() == SeatType.FIXED) {
            validateFixedSeatRules(date, today);
        } else {
            validateFloaterSeatRules(date, today, now);
        }
    }
    
    /**
     * Validates rules specific to FIXED seats.
     */
    private void validateFixedSeatRules(LocalDate date, LocalDate today) throws BookingException {
        // Rule: Can book up to 14 days in advance
        if (ChronoUnit.DAYS.between(today, date) > 14) {
            throw new BookingException("Fixed seats can only be booked up to 14 days in advance");
        }
    }
    
    /**
     * Validates rules specific to FLOATER seats.
     */
    private void validateFloaterSeatRules(LocalDate date, LocalDate today, LocalTime now) 
            throws BookingException {
        // Rule: Can only book after 3 PM
        if (date.equals(today) && now.isBefore(FLOATER_BOOKING_TIME)) {
            throw new BookingException("Floater seats can only be booked after 3:00 PM");
        }
        
        // Rule: Can only book for tomorrow
        LocalDate tomorrow = today.plusDays(1);
        if (!date.equals(tomorrow)) {
            throw new BookingException("Floater seats can only be booked for tomorrow");
        }
    }
    
    /**
     * Cancels a booking by booking ID.
     */
    public boolean cancelBooking(String bookingId) {
        Booking booking = bookingsById.remove(bookingId);
        if (booking == null) {
            return false;
        }
        
        // Remove from date-based storage
        List<Booking> bookingsForDate = bookingsByDate.get(booking.getDate());
        if (bookingsForDate != null) {
            bookingsForDate.remove(booking);
            if (bookingsForDate.isEmpty()) {
                bookingsByDate.remove(booking.getDate());
            }
        }
        
        // Remove from efficient lookup structures
        Set<String> seatsForDate = bookedSeatsPerDate.get(booking.getDate());
        if (seatsForDate != null) {
            seatsForDate.remove(booking.getSeat().getSeatId());
            if (seatsForDate.isEmpty()) {
                bookedSeatsPerDate.remove(booking.getDate());
            }
        }
        
        Set<String> usersForDate = bookedUsersPerDate.get(booking.getDate());
        if (usersForDate != null) {
            usersForDate.remove(booking.getUser().getUserId());
            if (usersForDate.isEmpty()) {
                bookedUsersPerDate.remove(booking.getDate());
            }
        }
        
        return true;
    }
    
    /**
     * Returns all bookings for a specific user.
     */
    public List<Booking> getUserBookings(String userId) {
        return bookingsById.values().stream()
                .filter(booking -> booking.getUser().getUserId().equals(userId))
                .sorted(Comparator.comparing(Booking::getDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Returns available seats for a specific date.
     */
    public List<Seat> getAvailableSeats(LocalDate date) {
        Set<String> bookedSeats = bookedSeatsPerDate.getOrDefault(date, Collections.emptySet());
        return allSeats.stream()
                .filter(seat -> !bookedSeats.contains(seat.getSeatId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Returns booked seats for a specific date.
     */
    public List<Booking> getBookingsForDate(LocalDate date) {
        return bookingsByDate.getOrDefault(date, Collections.emptyList());
    }
    
    /**
     * Returns all seats in the system.
     */
    public List<Seat> getAllSeats() {
        return new ArrayList<>(allSeats);
    }
    
    /**
     * Returns all users in the system.
     */
    public Map<String, User> getAllUsers() {
        return new HashMap<>(users);
    }
    
    /**
     * Checks if a seat is booked for a specific date.
     */
    public boolean isSeatBooked(String seatId, LocalDate date) {
        Set<String> bookedSeats = bookedSeatsPerDate.get(date);
        return bookedSeats != null && bookedSeats.contains(seatId);
    }
    
    // Utility methods
    
    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
    
    private Seat findSeatById(String seatId) {
        return allSeats.stream()
                .filter(seat -> seat.getSeatId().equals(seatId))
                .findFirst()
                .orElse(null);
    }
    
    private String generateBookingId() {
        return "BK" + System.currentTimeMillis();
    }
}