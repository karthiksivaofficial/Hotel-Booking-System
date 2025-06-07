package com.hotelbookingapplication.palatin.repository;

import com.hotelbookingapplication.palatin.model.EventHallReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventHallReservationRepository extends JpaRepository<EventHallReservation, Long> {
    List<EventHallReservation> findByUserEmail(String email);
    List<EventHallReservation> findByHotelId(Long hotelId);
}