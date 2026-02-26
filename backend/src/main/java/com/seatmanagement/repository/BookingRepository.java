package com.seatmanagement.repository;

import com.seatmanagement.model.Booking;
import com.seatmanagement.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByUserIdAndBookingDateAndStatus(Long userId, LocalDate bookingDate, BookingStatus status);

    boolean existsBySeatIdAndBookingDateAndStatus(Long seatId, LocalDate bookingDate, BookingStatus status);

    Optional<Booking> findByIdAndStatus(Long id, BookingStatus status);

    @Query("SELECT b.seat.id FROM Booking b WHERE b.bookingDate = :date AND b.status = 'ACTIVE'")
    List<Long> findBookedSeatIdsByDate(@Param("date") LocalDate date);

    List<Booking> findByBookingDateAndStatus(LocalDate bookingDate, BookingStatus status);
}
