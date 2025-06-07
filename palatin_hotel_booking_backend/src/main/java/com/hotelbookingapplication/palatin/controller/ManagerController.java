package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.BookingDTO;
import com.hotelbookingapplication.palatin.dto.FeedbackDTO;
import com.hotelbookingapplication.palatin.dto.ReportDTO;
import com.hotelbookingapplication.palatin.dto.RoomDTO;
import com.hotelbookingapplication.palatin.dto.UserDTO;
import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
import com.hotelbookingapplication.palatin.exception.UnauthorizedException;
import com.hotelbookingapplication.palatin.model.Room;
import com.hotelbookingapplication.palatin.model.User;
import com.hotelbookingapplication.palatin.repository.RoomRepository;
import com.hotelbookingapplication.palatin.repository.UserRepository;
import com.hotelbookingapplication.palatin.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/report")
    public ResponseEntity<ReportDTO> generateReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            return ResponseEntity.ok(managerService.generateReport(email, startDate, endDate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/feedback")
    public ResponseEntity<List<FeedbackDTO>> getFeedback(Authentication authentication) {
        try {
            String email = authentication.getName();
            return ResponseEntity.ok(managerService.getFeedback(email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/staff")
    public ResponseEntity<UserDTO> addStaff(@RequestBody UserDTO userDTO, Authentication authentication) {
        try {
            String email = authentication.getName();
            return ResponseEntity.ok(managerService.addStaff(userDTO, email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/staff/deactivate/{id}")
    public ResponseEntity<Void> deactivateStaff(@PathVariable Long id, Authentication authentication) {
        try {
            String email = authentication.getName();
            managerService.deactivateStaff(id, email);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/staff/deactivate/email/{email}")
    public ResponseEntity<Void> deactivateStaffByEmail(@PathVariable String email, Authentication authentication) {
        try {
            String managerEmail = authentication.getName();
            managerService.deactivateStaffByEmail(email, managerEmail);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/staff/status/{id}")
    public ResponseEntity<Void> updateStaffStatus(
            @PathVariable Long id,
            @RequestBody boolean active,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            managerService.updateStaffStatus(id, active, email);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/rooms/{id}/status")
    public ResponseEntity<Void> updateRoomStatus(
            @PathVariable Long id,
            @RequestBody String status,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            managerService.updateRoomStatus(id, status, email);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/rooms/roomNumber/{roomNumber}/status")
    public ResponseEntity<Void> updateRoomStatusByRoomNumber(
            @PathVariable String roomNumber,
            @RequestParam Long hotelId,
            @RequestBody String status,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User manager = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            if (!hotelId.equals(manager.getHotelId())) {
                throw new UnauthorizedException("Manager is not authorized for this hotel");
            }
            Room room = roomRepository.findByHotelIdAndRoomNumber(hotelId, roomNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with number: " + roomNumber));
            room.setStatus(status);
            roomRepository.save(room);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/rooms/hotel/{hotelId}")
    public ResponseEntity<List<RoomDTO>> getRoomsByHotelId(
            @PathVariable Long hotelId,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User manager = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            if (!hotelId.equals(manager.getHotelId())) {
                throw new UnauthorizedException("Manager is not authorized for this hotel");
            }
            List<Room> rooms = roomRepository.findByHotelId(hotelId);
            List<RoomDTO> roomDTOs = rooms.stream().map(this::toRoomDTO).collect(Collectors.toList());
            return ResponseEntity.ok(roomDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/rooms/floor/{hotelId}")
    public ResponseEntity<Void> addFloor(
            @PathVariable Long hotelId,
            @RequestParam Integer floorNumber,
            @RequestBody List<RoomDTO> rooms,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            managerService.addFloor(hotelId, floorNumber, rooms, email);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/rooms/hotel/{hotelId}/room/{roomNumber}")
    public ResponseEntity<Void> updateRoomByHotelIdAndRoomNumber(
            @PathVariable Long hotelId,
            @PathVariable String roomNumber,
            @RequestBody RoomDTO roomDTO,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            managerService.updateRoomByHotelIdAndRoomNumber(hotelId, roomNumber, roomDTO, email);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/bookings/hotel/{hotelId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByHotel(
            @PathVariable Long hotelId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            List<BookingDTO> bookings = managerService.getBookingsByHotelWithFilters(hotelId, startDate, endDate, status, email);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/bookings/hotel/{hotelId}/revenue")
    public ResponseEntity<Double> getRevenueByHotel(
            @PathVariable Long hotelId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            double revenue = managerService.getRevenueByHotelWithFilters(hotelId, startDate, endDate, status, email);
            return ResponseEntity.ok(revenue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private RoomDTO toRoomDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setType(room.getType());
        dto.setPrice(room.getPrice());
        dto.setView(room.getView());
        dto.setAmenities(room.getAmenities());
        dto.setStatus(room.getStatus());
        dto.setFloor(room.getFloor());
        dto.setHotelId(room.getHotel().getId());
        return dto;
    }
}