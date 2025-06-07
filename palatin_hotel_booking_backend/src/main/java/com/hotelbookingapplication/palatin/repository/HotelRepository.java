package com.hotelbookingapplication.palatin.repository;

import com.hotelbookingapplication.palatin.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByCity(String city);
}