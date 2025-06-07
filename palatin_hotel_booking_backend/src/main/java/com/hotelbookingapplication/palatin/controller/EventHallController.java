package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.EventHallReservationDTO;
import com.hotelbookingapplication.palatin.service.EventHallReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-hall")
public class EventHallController {

    @Autowired
    private EventHallReservationService eventHallReservationService;

    @GetMapping("/user")
    public ResponseEntity<List<EventHallReservationDTO>> getReservationsByUser(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(eventHallReservationService.getReservationsByUser(email));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<EventHallReservationDTO>> getReservationsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(eventHallReservationService.getReservationsByHotel(hotelId));
    }

    @PostMapping
    public ResponseEntity<EventHallReservationDTO> createReservation(@RequestBody EventHallReservationDTO reservationDTO, @AuthenticationPrincipal String email) {
        reservationDTO.setUserEmail(email);
        return ResponseEntity.ok(eventHallReservationService.createReservation(reservationDTO));
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<EventHallReservationDTO> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(eventHallReservationService.cancelReservation(id));
    }
}