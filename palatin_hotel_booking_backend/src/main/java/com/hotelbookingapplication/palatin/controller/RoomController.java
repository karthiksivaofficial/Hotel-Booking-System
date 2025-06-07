//package com.hotelbookingapplication.palatin.controller;
//
//import com.hotelbookingapplication.palatin.dto.RoomDTO;
//import com.hotelbookingapplication.palatin.service.RoomService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/rooms")
//public class RoomController {
//
//    @Autowired
//    private RoomService roomService;
//
//    @GetMapping("/hotel/{hotelId}")
//    public ResponseEntity<List<RoomDTO>> getAvailableRooms(
//            @PathVariable Long hotelId,
//            @RequestParam(required = false) String type,
//            @RequestParam(required = false) String view,
//            @RequestParam(required = false) Double minPrice,
//            @RequestParam(required = false) Double maxPrice,
//            @RequestParam(required = false) Integer floor,
//            @RequestParam(required = false) LocalDate checkIn,
//            @RequestParam(required = false) LocalDate checkOut) {
//        return ResponseEntity.ok(roomService.getAvailableRooms(hotelId, type, view, minPrice, maxPrice, floor, checkIn, checkOut));
//    }
//
//    @GetMapping("/hotel/{hotelId}/room/{roomNumber}")
//    public ResponseEntity<RoomDTO> getRoomByHotelIdAndRoomNumber(
//            @PathVariable Long hotelId,
//            @PathVariable String roomNumber) {
//        return ResponseEntity.ok(roomService.getRoomByHotelIdAndRoomNumber(hotelId, roomNumber));
//    }
//
//    @PostMapping
//    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomDTO roomDTO) {
//        return ResponseEntity.ok(roomService.createRoom(roomDTO));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<RoomDTO> updateRoom(@PathVariable Long id, @RequestBody RoomDTO roomDTO) {
//        return ResponseEntity.ok(roomService.updateRoom(id, roomDTO));
//    }
//
//    @PutMapping("/hotel/{hotelId}/room/{roomNumber}")
//    public ResponseEntity<RoomDTO> updateRoomByHotelIdAndRoomNumber(
//            @PathVariable Long hotelId,
//            @PathVariable String roomNumber,
//            @RequestBody RoomDTO roomDTO) {
//        return ResponseEntity.ok(roomService.updateRoomByHotelIdAndRoomNumber(hotelId, roomNumber, roomDTO));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
//        roomService.deleteRoom(id);
//        return ResponseEntity.ok().build();
//    }
//}
package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.RoomDTO;
import com.hotelbookingapplication.palatin.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms(
            @PathVariable Long hotelId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String view,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) LocalDate checkIn,
            @RequestParam(required = false) LocalDate checkOut) {
        return ResponseEntity.ok(roomService.getAvailableRooms(hotelId, type, view, minPrice, maxPrice, floor, checkIn, checkOut));
    }

    @GetMapping("/hotel/{hotelId}/room/{roomNumber}")
    public ResponseEntity<RoomDTO> getRoomByHotelIdAndRoomNumber(
            @PathVariable Long hotelId,
            @PathVariable String roomNumber) {
        return ResponseEntity.ok(roomService.getRoomByHotelIdAndRoomNumber(hotelId, roomNumber));
    }

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomDTO roomDTO) {
        return ResponseEntity.ok(roomService.createRoom(roomDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable Long id, @RequestBody RoomDTO roomDTO) {
        return ResponseEntity.ok(roomService.updateRoom(id, roomDTO));
    }

    @PutMapping("/hotel/{hotelId}/room/{roomNumber}")
    public ResponseEntity<RoomDTO> updateRoomByHotelIdAndRoomNumber(
            @PathVariable Long hotelId,
            @PathVariable String roomNumber,
            @RequestBody RoomDTO roomDTO) {
        return ResponseEntity.ok(roomService.updateRoomByHotelIdAndRoomNumber(hotelId, roomNumber, roomDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/floor/{hotelId}")
    public ResponseEntity<Void> createFloor(
            @PathVariable Long hotelId,
            @RequestParam Integer floorNumber,
            @RequestBody List<RoomDTO> rooms) {
        try {
            roomService.createFloor(hotelId, floorNumber, rooms);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}