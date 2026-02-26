package com.seatmanagement.service;

import com.seatmanagement.dto.BookingRequest;
import com.seatmanagement.dto.BookingResponse;
import com.seatmanagement.dto.SeatAvailabilityDto;
import com.seatmanagement.exception.BookingException;
import com.seatmanagement.model.Booking;
import com.seatmanagement.model.Seat;
import com.seatmanagement.model.User;
import com.seatmanagement.model.enums.BatchType;
import com.seatmanagement.model.enums.BookingStatus;
import com.seatmanagement.model.enums.SeatType;
import com.seatmanagement.repository.BookingRepository;
import com.seatmanagement.repository.SeatRepository;
import com.seatmanagement.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    // Base date used to determine Week 1 / Week 2 dynamically via ChronoUnit.WEEKS
    private static final LocalDate BASE_DATE = LocalDate.of(2024, 1, 1);

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          SeatRepository seatRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        User user = validateAndGetUser(request.getUserId());
        Seat seat = validateAndGetSeat(request.getSeatId());
        LocalDate bookingDate = request.getBookingDate();

        validateBookingDate(bookingDate);
        validateWeeklyRotation(user.getBatch(), bookingDate);
        validateSeatTypeRules(seat.getType(), bookingDate);
        validateNoDuplicateBooking(user.getId(), seat.getId(), bookingDate);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setSeat(seat);
        booking.setBookingDate(bookingDate);
        booking.setStatus(BookingStatus.ACTIVE);

        Booking saved = bookingRepository.save(booking);
        return toBookingResponse(saved, "Booking confirmed successfully.");
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findByIdAndStatus(bookingId, BookingStatus.ACTIVE)
                .orElseThrow(() -> new BookingException(
                        "Active booking not found with id: " + bookingId, HttpStatus.NOT_FOUND));

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);
        return toBookingResponse(updated, "Booking cancelled. Seat is now available.");
    }

    @Transactional(readOnly = true)
    public List<SeatAvailabilityDto> getAvailability(LocalDate date) {
        List<Long> bookedSeatIds = bookingRepository.findBookedSeatIdsByDate(date);
        List<Seat> allSeats = seatRepository.findAll();

        return allSeats.stream()
                .map(seat -> new SeatAvailabilityDto(
                        seat.getId(),
                        seat.getSeatNumber(),
                        seat.getType().name(),
                        !bookedSeatIds.contains(seat.getId())
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VALIDATION HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private User validateAndGetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BookingException(
                        "User not found with id: " + userId, HttpStatus.NOT_FOUND));
    }

    private Seat validateAndGetSeat(Long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new BookingException(
                        "Seat not found with id: " + seatId, HttpStatus.NOT_FOUND));
    }

    /**
     * Booking allowed only for working days (Mon-Fri), today onwards,
     * and within the next 2 weeks (14 days from today).
     */
    private void validateBookingDate(LocalDate bookingDate) {
        LocalDate today = LocalDate.now();

        if (bookingDate.isBefore(today)) {
            throw new BookingException("Cannot book a seat for a past date.", HttpStatus.BAD_REQUEST);
        }

        if (bookingDate.isAfter(today.plusWeeks(2))) {
            throw new BookingException(
                    "Cannot book more than 2 weeks in advance.", HttpStatus.BAD_REQUEST);
        }

        DayOfWeek day = bookingDate.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new BookingException(
                    "Bookings are only allowed on working days (Mon–Fri).", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Weekly rotation logic – fully dynamic using ChronoUnit.WEEKS from BASE_DATE.
     *
     * Even week offset → Mon-Wed: Batch 1, Thu-Fri: Batch 2
     * Odd  week offset → Mon-Wed: Batch 2, Thu-Fri: Batch 1
     */
    private void validateWeeklyRotation(BatchType userBatch, LocalDate bookingDate) {
        long weekOffset = ChronoUnit.WEEKS.between(BASE_DATE, bookingDate);
        boolean isEvenWeek = (weekOffset % 2 == 0);

        DayOfWeek day = bookingDate.getDayOfWeek();
        boolean isMonToWed = (day == DayOfWeek.MONDAY
                || day == DayOfWeek.TUESDAY
                || day == DayOfWeek.WEDNESDAY);
        boolean isThuToFri = (day == DayOfWeek.THURSDAY || day == DayOfWeek.FRIDAY);

        BatchType allowedBatch;
        if (isEvenWeek) {
            allowedBatch = isMonToWed ? BatchType.BATCH_1 : BatchType.BATCH_2;
        } else {
            allowedBatch = isMonToWed ? BatchType.BATCH_2 : BatchType.BATCH_1;
        }

        // If it's neither Mon-Wed nor Thu-Fri (i.e., weekend), date validation already covers that
        if (!isMonToWed && !isThuToFri) {
            throw new BookingException("Invalid day of week for rotation check.", HttpStatus.BAD_REQUEST);
        }

        if (userBatch != allowedBatch) {
            throw new BookingException(
                    String.format("Your batch (%s) is not allowed to book on %s. Allowed batch: %s.",
                            userBatch.name(), bookingDate.getDayOfWeek().name(), allowedBatch.name()),
                    HttpStatus.FORBIDDEN);
        }
    }

    /**
     * FLOATER seats: Only bookable after 3 PM server time, only for tomorrow.
     * FIXED seats: No time restriction, up to 2 weeks ahead.
     */
    private void validateSeatTypeRules(SeatType seatType, LocalDate bookingDate) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        if (seatType == SeatType.FLOATER) {
            // Must be after 3 PM
            if (LocalTime.now().isBefore(LocalTime.of(15, 0))) {
                throw new BookingException(
                        "Floater seats can only be booked after 3:00 PM.", HttpStatus.FORBIDDEN);
            }
            // Only for tomorrow
            if (!bookingDate.equals(tomorrow)) {
                throw new BookingException(
                        "Floater seats can only be booked for tomorrow.", HttpStatus.BAD_REQUEST);
            }
        }
        // FIXED seats: no extra restriction beyond date validation already done
    }

    /**
     * Prevent double booking: one seat per user per day, one user per seat per day.
     */
    private void validateNoDuplicateBooking(Long userId, Long seatId, LocalDate bookingDate) {
        if (bookingRepository.existsByUserIdAndBookingDateAndStatus(userId, bookingDate, BookingStatus.ACTIVE)) {
            throw new BookingException(
                    "You already have an active booking on " + bookingDate + ".", HttpStatus.CONFLICT);
        }
        if (bookingRepository.existsBySeatIdAndBookingDateAndStatus(seatId, bookingDate, BookingStatus.ACTIVE)) {
            throw new BookingException(
                    "Seat is already booked on " + bookingDate + ".", HttpStatus.CONFLICT);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MAPPER HELPER
    // ─────────────────────────────────────────────────────────────────────────

    private BookingResponse toBookingResponse(Booking booking, String message) {
        return new BookingResponse(
                booking.getId(),
                booking.getUser().getName(),
                booking.getSeat().getSeatNumber(),
                booking.getSeat().getType().name(),
                booking.getBookingDate(),
                booking.getStatus().name(),
                message
        );
    }
}
