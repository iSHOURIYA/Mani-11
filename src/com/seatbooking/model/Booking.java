package com.seatbooking.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a seat booking in the system.
 * Links a user to a seat for a specific date.
 */
public class Booking {
    private final String bookingId;
    private final User user;
    private final Seat seat;
    private final LocalDate date;
    private final LocalDateTime bookingTime;
    
    public Booking(String bookingId, User user, Seat seat, LocalDate date) {
        this.bookingId = Objects.requireNonNull(bookingId, "Booking ID cannot be null");
        this.user = Objects.requireNonNull(user, "User cannot be null");
        this.seat = Objects.requireNonNull(seat, "Seat cannot be null");
        this.date = Objects.requireNonNull(date, "Date cannot be null");
        this.bookingTime = LocalDateTime.now();
    }
    
    public String getBookingId() {
        return bookingId;
    }
    
    public User getUser() {
        return user;
    }
    
    public Seat getSeat() {
        return seat;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public LocalDateTime getBookingTime() {
        return bookingTime;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Booking booking = (Booking) obj;
        return Objects.equals(bookingId, booking.bookingId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }
    
    @Override
    public String toString() {
        return String.format("Booking{id='%s', user=%s, seat=%s, date=%s}", 
                           bookingId, user.getName(), seat.getSeatId(), date);
    }
}