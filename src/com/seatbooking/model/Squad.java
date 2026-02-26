package com.seatbooking.model;

/**
 * Represents the 8 squads in the system, organized into 2 batches.
 */
public enum Squad {
    // Batch 1 squads
    SQUAD_A1("Squad A1", Batch.BATCH_1),
    SQUAD_B1("Squad B1", Batch.BATCH_1),
    SQUAD_C1("Squad C1", Batch.BATCH_1),
    SQUAD_D1("Squad D1", Batch.BATCH_1),
    
    // Batch 2 squads
    SQUAD_A2("Squad A2", Batch.BATCH_2),
    SQUAD_B2("Squad B2", Batch.BATCH_2),
    SQUAD_C2("Squad C2", Batch.BATCH_2),
    SQUAD_D2("Squad D2", Batch.BATCH_2);
    
    private final String displayName;
    private final Batch batch;
    
    Squad(String displayName, Batch batch) {
        this.displayName = displayName;
        this.batch = batch;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Batch getBatch() {
        return batch;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}