package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.BookingDTO;
import com.hotelbookingapplication.palatin.dto.RoomDTO;
import com.hotelbookingapplication.palatin.model.Room;
import com.hotelbookingapplication.palatin.repository.RoomRepository;
import com.hotelbookingapplication.palatin.service.BookingService;
import com.hotelbookingapplication.palatin.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/user")
    public ResponseEntity<List<BookingDTO>> getBookingsByUser(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(email));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByHotel(
            @PathVariable Long hotelId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(bookingService.getBookingsByHotel(hotelId, startDate, endDate, status));
    }

    @PostMapping
    public ResponseEntity<String> initiateBooking(
            @RequestBody BookingDTO bookingDTO,
            @AuthenticationPrincipal String email) {
        bookingDTO.setUserEmail(email);
        List<RoomDTO> rooms = roomRepository.findAllById(bookingDTO.getRoomIds()).stream()
                .map(this::mapToRoomDTO)
                .collect(Collectors.toList());
        if (rooms.size() != bookingDTO.getRoomIds().size()) {
            throw new IllegalArgumentException("One or more rooms not found");
        }
        String approvalUrl = paymentService.createPayment(bookingDTO, rooms, "user");
        return ResponseEntity.ok(approvalUrl);
    }

    @PostMapping("/execute")
    public ResponseEntity<List<BookingDTO>> executeBooking(
            @RequestBody BookingDTO bookingDTO,
            @RequestParam String paymentId,
            @RequestParam String payerId,
            @AuthenticationPrincipal String email) {
        if (bookingDTO.getRoomIds() == null || bookingDTO.getRoomIds().isEmpty()) {
            throw new IllegalArgumentException("At least one room must be selected");
        }
        bookingDTO.setUserEmail(email);
        String executedPaymentId = paymentService.executePayment(paymentId, payerId);
        List<BookingDTO> bookings = bookingService.createBooking(bookingDTO, executedPaymentId);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PutMapping("/check-in/{id}")
    public ResponseEntity<BookingDTO> checkIn(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.checkIn(id));
    }

    @PutMapping("/check-out/{id}")
    public ResponseEntity<BookingDTO> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.checkOut(id));
    }

    @PutMapping("/pay/{id}")
    public ResponseEntity<BookingDTO> markPaid(@PathVariable Long id, @RequestBody String paymentIntentId) {
        return ResponseEntity.ok(bookingService.markPaid(id, paymentIntentId));
    }

    @GetMapping("/room/{roomNumber}")
    public ResponseEntity<BookingDTO> getBookingByRoomNumber(@PathVariable String roomNumber) {
        return ResponseEntity.ok(bookingService.getBookingByRoomNumber(roomNumber));
    }

    @GetMapping("/{id}/bill")
    public ResponseEntity<byte[]> generateBill(@PathVariable Long id) {
        byte[] billPdf = bookingService.generateBill(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=bill_" + id + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(billPdf);
    }

    private RoomDTO mapToRoomDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setHotelId(room.getHotel().getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setType(room.getType());
        dto.setPrice(room.getPrice());
        dto.setStatus(room.getStatus());
        dto.setView(room.getView());
        dto.setAmenities(room.getAmenities());
        dto.setFloor(room.getFloor());
        return dto;
    }
}