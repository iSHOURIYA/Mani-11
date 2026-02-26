package com.seatbooking.model;

import java.util.Objects;

/**
 * Represents a user in the seat booking system.
 * Each user belongs to a specific squad and batch.
 */
public class User {
    private final String userId;
    private final String name;
    private final Squad squad;
    
    public User(String userId, String name, Squad squad) {
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.squad = Objects.requireNonNull(squad, "Squad cannot be null");
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getName() {
        return name;
    }
    
    public Squad getSquad() {
        return squad;
    }
    
    public Batch getBatch() {
        return squad.getBatch();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(userId, user.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', squad=%s}", userId, name, squad);
    }
}