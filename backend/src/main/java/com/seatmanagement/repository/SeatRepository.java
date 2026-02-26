package com.seatmanagement.repository;

import com.seatmanagement.model.Seat;
import com.seatmanagement.model.enums.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByType(SeatType type);
}
