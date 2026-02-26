package com.seatbooking.model;

import java.util.Objects;

/**
 * Represents a seat in the booking system.
 * Seats can be either FIXED or FLOATER type.
 */
public class Seat {
    private final String seatId;
    private final SeatType type;
    private final int row;
    private final int column;
    
    public Seat(String seatId, SeatType type, int row, int column) {
        this.seatId = Objects.requireNonNull(seatId, "Seat ID cannot be null");
        this.type = Objects.requireNonNull(type, "Seat type cannot be null");
        this.row = row;
        this.column = column;
    }
    
    public String getSeatId() {
        return seatId;
    }
    
    public SeatType getType() {
        return type;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getColumn() {
        return column;
    }
    
    /**
     * Returns display character for seat matrix.
     * F for FIXED, L for FLOATER
     */
    public char getDisplayChar() {
        return type == SeatType.FIXED ? 'F' : 'L';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Seat seat = (Seat) obj;
        return Objects.equals(seatId, seat.seatId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(seatId);
    }
    
    @Override
    public String toString() {
        return String.format("Seat{id='%s', type=%s, position=(%d,%d)}", 
                           seatId, type, row, column);
    }
}