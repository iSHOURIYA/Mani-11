package com.seatmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatAvailabilityDto {
    private Long seatId;
    private Integer seatNumber;
    private String seatType;
    private boolean available;
}
