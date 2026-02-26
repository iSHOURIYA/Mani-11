package com.seatbooking.model;

/**
 * Represents the type of seat in the booking system.
 */
public enum SeatType {
    FIXED("Fixed", 'F'),
    FLOATER("Floater", 'L');
    
    private final String displayName;
    private final char displayChar;
    
    SeatType(String displayName, char displayChar) {
        this.displayName = displayName;
        this.displayChar = displayChar;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public char getDisplayChar() {
        return displayChar;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}