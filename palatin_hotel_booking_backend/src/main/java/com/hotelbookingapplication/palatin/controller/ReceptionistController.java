package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.*;
import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
import com.hotelbookingapplication.palatin.exception.UnauthorizedException;
import com.hotelbookingapplication.palatin.model.Booking;
import com.hotelbookingapplication.palatin.model.User;
import com.hotelbookingapplication.palatin.repository.BookingRepository;
import com.hotelbookingapplication.palatin.repository.UserRepository;
import com.hotelbookingapplication.palatin.service.BookingService;
import com.hotelbookingapplication.palatin.service.ReceptionistService;
import com.hotelbookingapplication.palatin.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/receptionist")
@PreAuthorize("hasRole('RECEPTIONIST')")
public class ReceptionistController {

    @Autowired
    private ReceptionistService receptionistService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/bookings/today")
    public ResponseEntity<List<BookingDTO>> getTodayBookings(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(receptionistService.getTodayBookings(email));
    }

    @GetMapping("/bookings/date-range")
    public ResponseEntity<List<BookingDTO>> getBookingsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(receptionistService.getBookingsByDateRange(email, startDate, endDate));
    }

    @PostMapping("/check-in/{bookingId}")
    public ResponseEntity<BookingDTO> checkIn(@PathVariable Long bookingId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(receptionistService.checkIn(bookingId, email));
    }

    @PostMapping("/check-out/{bookingId}")
    public ResponseEntity<BookingDTO> checkOut(@PathVariable Long bookingId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(receptionistService.checkOut(bookingId, email));
    }

    @PostMapping("/offline-booking/initiate")
    public ResponseEntity<String> initiateOfflineBooking(@RequestBody BookingDTO bookingDTO, Authentication authentication) {
        String email = authentication.getName();
        String approvalUrl = receptionistService.initiateOfflineBooking(bookingDTO, email);
        return ResponseEntity.ok(approvalUrl);
    }

    @PostMapping("/offline-booking/execute")
    public ResponseEntity<List<BookingDTO>> executeOfflineBooking(
            @RequestBody BookingDTO bookingDTO,
            @RequestParam String paymentId,
            @RequestParam String payerId,
            Authentication authentication) {
        String email = authentication.getName();
        List<BookingDTO> bookings = receptionistService.executeOfflineBooking(bookingDTO, paymentId, payerId, email);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/room-stats")
    public ResponseEntity<Map<String, Long>> getRoomStats(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(receptionistService.getRoomStats(email));
    }

    @GetMapping("/service-requests")
    public ResponseEntity<List<ServiceRequestDTO>> getServiceRequests(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(receptionistService.getServiceRequests(email));
    }

    @GetMapping("/staff-by-role")
    public ResponseEntity<List<UserDTO>> getStaffByRole(@RequestParam String role, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(receptionistService.getStaffByRole(email, role));
    }

    @PostMapping("/service-requests/assign/{id}")
    public ResponseEntity<ServiceRequestDTO> assignStaff(@PathVariable Long id, @RequestBody String staffEmail, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(receptionistService.assignStaff(id, staffEmail, email));
    }

    @GetMapping("/room-status")
    public ResponseEntity<RoomDTO> getRoomByRoomNumber(@RequestParam String roomNumber, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Long hotelId = user.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("User is not assigned to any hotel");
        }
        RoomDTO room = roomService.getRoomByHotelIdAndRoomNumber(hotelId, roomNumber);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/booking-by-room")
    public ResponseEntity<BookingDTO> getBookingByRoomNumber(@RequestParam String roomNumber, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Long hotelId = user.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("User is not assigned to any hotel");
        }
        BookingDTO booking = bookingService.getLatestBookingByRoomNumberAndHotelId(roomNumber, hotelId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Long hotelId = user.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("User is not assigned to any hotel");
        }
        List<RoomDTO> rooms = roomService.getAvailableRooms(hotelId, null, null, null, null, null, null, null);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/bill/{bookingId}")
    public ResponseEntity<byte[]> generateBill(@PathVariable Long bookingId, Authentication authentication) {
        String email = authentication.getName();
        byte[] bill = receptionistService.generateBill(bookingId, email);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "booking_bill_" + bookingId + ".pdf");
        return ResponseEntity.ok().headers(headers).body(bill);
    }
}