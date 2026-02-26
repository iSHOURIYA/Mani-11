package com.seatbooking.service;

/**
 * Custom exception for booking-related errors.
 */
public class BookingException extends Exception {
    
    public BookingException(String message) {
        super(message);
    }
    
    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
}