package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.*;
import com.hotelbookingapplication.palatin.service.AdminService;
import com.hotelbookingapplication.palatin.service.BookingService;
import com.hotelbookingapplication.palatin.service.FeedbackService;
import com.hotelbookingapplication.palatin.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/hotels")
    public ResponseEntity<List<HotelDTO>> getAllHotels() {
        return ResponseEntity.ok(adminService.getAllHotels());
    }

    @PostMapping("/hotels")
    public ResponseEntity<HotelDTO> createHotel(@RequestBody HotelDTO hotelDTO) {
        return ResponseEntity.ok(adminService.createHotel(hotelDTO));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(adminService.updateUser(userDTO));
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(adminService.createUser(userDTO));
    }

    @PostMapping("/rooms")
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomDTO roomDTO) {
        return ResponseEntity.ok(roomService.createRoom(roomDTO));
    }

    @PostMapping("/users/deactivate")
    public ResponseEntity<Void> deactivateUser(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String email) {
        if (id == null && email == null) {
            throw new IllegalArgumentException("Either ID or email must be provided");
        }
        adminService.deactivateUser(id, email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/toggle-status")
    public ResponseEntity<Void> toggleUserStatus(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String email) {
        if (id == null && email == null) {
            throw new IllegalArgumentException("Either ID or email must be provided");
        }
        adminService.toggleUserStatus(id, email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/report")
    public ResponseEntity<ReportDTO> getReport(
            @RequestParam(required = false) Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String endDate) {
        ReportDTO report = adminService.getReport(hotelId, LocalDate.parse(startDate), LocalDate.parse(endDate));
        return ResponseEntity.ok(report);
    }

    @GetMapping("/hotel-summaries")
    public ResponseEntity<List<ReportDTO>> getAllHotelSummaries(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String endDate) {
        List<ReportDTO> summaries = adminService.getAllHotelSummaries(LocalDate.parse(startDate), LocalDate.parse(endDate));
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/hotel-details/{hotelId}")
    public ResponseEntity<HotelDetailsDTO> getHotelDetails(@PathVariable Long hotelId) {
        HotelDetailsDTO details = adminService.getHotelDetails(hotelId);
        return ResponseEntity.ok(details);
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDTO>> getBookings(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String endDate,
            @RequestParam(required = false) String status) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
        List<BookingDTO> bookings = bookingService.getBookingsByHotel(hotelId, start, end, status);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/feedback")
    public ResponseEntity<List<FeedbackDTO>> getFeedback(
            @RequestParam(required = false) Long hotelId) {
        List<FeedbackDTO> feedback;
        if(hotelId!=0) {
            feedback = feedbackService.getFeedbackByHotel(hotelId);
        }
        else{
            feedback = feedbackService.getAllFeedback();
        }
        return ResponseEntity.ok(feedback);
    }
}