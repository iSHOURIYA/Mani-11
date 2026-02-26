package com.seatmanagement.controller;

import com.seatmanagement.model.Seat;
import com.seatmanagement.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@CrossOrigin(origins = "*")
public class SeatController {

    private final BookingService bookingService;

    public SeatController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * GET /api/seats
     * Returns all seats (FIXED + FLOATER).
     */
    @GetMapping
    public ResponseEntity<List<Seat>> getAllSeats() {
        return ResponseEntity.ok(bookingService.getAllSeats());
    }
}
