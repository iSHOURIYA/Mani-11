package com.seatmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Seat ID is required")
    private Long seatId;

    @NotNull(message = "Booking date is required")
    private LocalDate bookingDate;
}
