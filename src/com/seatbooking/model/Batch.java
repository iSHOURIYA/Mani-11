package com.seatbooking.model;

/**
 * Represents the two batches in the system.
 * Used for dynamic rotation logic.
 */
public enum Batch {
    BATCH_1("Batch 1"),
    BATCH_2("Batch 2");
    
    private final String displayName;
    
    Batch(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}