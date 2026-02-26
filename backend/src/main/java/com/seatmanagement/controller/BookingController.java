package com.seatmanagement.controller;

import com.seatmanagement.dto.BookingRequest;
import com.seatmanagement.dto.BookingResponse;
import com.seatmanagement.dto.SeatAvailabilityDto;
import com.seatmanagement.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * POST /api/book
     * Book a seat for a user on a given date.
     */
    @PostMapping("/book")
    public ResponseEntity<BookingResponse> bookSeat(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * DELETE /api/cancel/{bookingId}
     * Cancel an active booking and free the seat immediately.
     */
    @DeleteMapping("/cancel/{bookingId}")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long bookingId) {
        BookingResponse response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/availability?date=YYYY-MM-DD
     * Returns all seats and their availability for the given date.
     */
    @GetMapping("/availability")
    public ResponseEntity<List<SeatAvailabilityDto>> getAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(bookingService.getAvailability(date));
    }
}
