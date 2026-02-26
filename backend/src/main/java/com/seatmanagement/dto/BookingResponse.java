package com.seatmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private String userName;
    private Integer seatNumber;
    private String seatType;
    private LocalDate bookingDate;
    private String status;
    private String message;
}
