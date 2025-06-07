package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.RestaurantReservationDTO;
import com.hotelbookingapplication.palatin.service.RestaurantReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantReservationService restaurantReservationService;

    @GetMapping("/user")
    public ResponseEntity<List<RestaurantReservationDTO>> getReservationsByUser(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(restaurantReservationService.getReservationsByUser(email));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RestaurantReservationDTO>> getReservationsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(restaurantReservationService.getReservationsByHotel(hotelId));
    }

    @PostMapping
    public ResponseEntity<RestaurantReservationDTO> createReservation(@RequestBody RestaurantReservationDTO reservationDTO, @AuthenticationPrincipal String email) {
        reservationDTO.setUserEmail(email);
        return ResponseEntity.ok(restaurantReservationService.createReservation(reservationDTO));
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<RestaurantReservationDTO> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantReservationService.cancelReservation(id));
    }
}