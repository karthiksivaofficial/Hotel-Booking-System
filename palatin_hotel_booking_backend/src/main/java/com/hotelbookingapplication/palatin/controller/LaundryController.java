package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.ServiceRequestDTO;
import com.hotelbookingapplication.palatin.service.LaundryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laundry")
public class LaundryController {

    @Autowired
    private LaundryService laundryService;

    @GetMapping("/requests/assigned")
    public ResponseEntity<List<ServiceRequestDTO>> getAssignedLaundryRequests(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(laundryService.getAssignedLaundryRequests(email));
    }

    @GetMapping("/requests/all")
    public ResponseEntity<List<ServiceRequestDTO>> getAllLaundryRequests(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(laundryService.getAllLaundryRequestsForHotel(email));
    }

    @PostMapping("/requests/complete/{id}")
    public ResponseEntity<ServiceRequestDTO> completeRequest(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(laundryService.completeRequest(id, email));
    }
}