package com.hotelbookingapplication.palatin.repository;

import com.hotelbookingapplication.palatin.model.RestaurantReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantReservationRepository extends JpaRepository<RestaurantReservation, Long> {
    List<RestaurantReservation> findByUserEmail(String email);
    List<RestaurantReservation> findByHotelId(Long hotelId);
}