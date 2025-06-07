package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.HotelDTO;
import com.hotelbookingapplication.palatin.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @GetMapping("/public")
    public ResponseEntity<List<HotelDTO>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/public/city/{city}")
    public ResponseEntity<List<HotelDTO>> getHotelsByCity(@PathVariable String city) {
        return ResponseEntity.ok(hotelService.getHotelsByCity(city));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDTO> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }
}