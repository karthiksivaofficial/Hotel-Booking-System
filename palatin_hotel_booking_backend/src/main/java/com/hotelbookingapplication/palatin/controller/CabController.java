//package com.hotelbookingapplication.palatin.controller;
//
//import com.hotelbookingapplication.palatin.dto.CabBookingDTO;
//import com.hotelbookingapplication.palatin.service.CabBookingService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/cabs")
//public class CabController {
//
//    @Autowired
//    private CabBookingService cabBookingService;
//
//    @GetMapping("/user")
//    public ResponseEntity<List<CabBookingDTO>> getCabBookingsByUser(Authentication authentication) {
//        String email = authentication.getName();
//        return ResponseEntity.ok(cabBookingService.getCabBookingsByUser(email));
//    }
//
//    @GetMapping("/driver")
//    public ResponseEntity<List<CabBookingDTO>> getAssignedCabBookings(Authentication authentication) {
//        String email = authentication.getName();
//        return ResponseEntity.ok(cabBookingService.getAssignedCabBookings(email));
//    }
//
//    @PostMapping
//    public ResponseEntity<CabBookingDTO> createCabBooking(@RequestBody CabBookingDTO cabBookingDTO, Authentication authentication) {
//        cabBookingDTO.setUserEmail(authentication.getName());
//        return ResponseEntity.ok(cabBookingService.createCabBooking(cabBookingDTO));
//    }
//
//    @PostMapping("/cancel/{id}")
//    public ResponseEntity<CabBookingDTO> cancelCabBooking(@PathVariable Long id, Authentication authentication) {
//        String email = authentication.getName();
//        return ResponseEntity.ok(cabBookingService.cancelCabBooking(id, email));
//    }
//
//    @PostMapping("/assign/{id}")
//    public ResponseEntity<CabBookingDTO> assignDriver(
//            @PathVariable Long id,
//            @RequestBody String driverEmail,
//            Authentication authentication) {
//        String receptionistEmail = authentication.getName();
//        return ResponseEntity.ok(cabBookingService.assignDriver(id, driverEmail, receptionistEmail));
//    }
//}