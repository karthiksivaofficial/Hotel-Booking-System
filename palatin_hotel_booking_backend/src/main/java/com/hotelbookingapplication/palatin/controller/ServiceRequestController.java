package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.ServiceRequestDTO;
import com.hotelbookingapplication.palatin.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @GetMapping("/user")
    public ResponseEntity<List<ServiceRequestDTO>> getRequestsByUser(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(serviceRequestService.getRequestsByUser(email));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ServiceRequestDTO>> getRequestsByHotel(
            @PathVariable Long hotelId,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(serviceRequestService.getRequestsByHotel(hotelId, email));
    }

    @PostMapping
    public ResponseEntity<ServiceRequestDTO> createRequest(
            @RequestBody ServiceRequestDTO requestDTO,
            @AuthenticationPrincipal String email) {
        requestDTO.setUserEmail(email);
        return ResponseEntity.ok(serviceRequestService.createRequest(requestDTO));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<ServiceRequestDTO> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody String status) {
        return ResponseEntity.ok(serviceRequestService.updateRequestStatus(id, status));
    }

    @PostMapping("/assign/{id}")
    public ResponseEntity<ServiceRequestDTO> assignRequest(
            @PathVariable Long id,
            @RequestBody Long staffId) {
        return ResponseEntity.ok(serviceRequestService.assignRequest(id, staffId));
    }

    @GetMapping("/user/rooms")
    public ResponseEntity<List<String>> getCheckedInRoomNumbers(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(serviceRequestService.getCheckedInRoomNumbers(email));
    }
}