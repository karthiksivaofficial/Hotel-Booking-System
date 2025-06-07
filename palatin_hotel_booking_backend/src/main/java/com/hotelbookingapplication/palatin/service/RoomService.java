//package com.hotelbookingapplication.palatin.service;
//
//import com.hotelbookingapplication.palatin.dto.RoomDTO;
//import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
//import com.hotelbookingapplication.palatin.model.Booking;
//import com.hotelbookingapplication.palatin.model.Hotel;
//import com.hotelbookingapplication.palatin.model.Room;
//import com.hotelbookingapplication.palatin.repository.BookingRepository;
//import com.hotelbookingapplication.palatin.repository.HotelRepository;
//import com.hotelbookingapplication.palatin.repository.RoomRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class RoomService {
//
//    @Autowired
//    private RoomRepository roomRepository;
//
//    @Autowired
//    private HotelRepository hotelRepository;
//
//    @Autowired
//    private BookingRepository bookingRepository;
//
//    public List<RoomDTO> getAvailableRooms(Long hotelId, String type, String view, Double minPrice, Double maxPrice, Integer floor, LocalDate checkIn, LocalDate checkOut) {
//        List<Room> rooms = roomRepository.findByHotelId(hotelId);
//
//        if (type != null && !type.isEmpty()) {
//            rooms = rooms.stream()
//                    .filter(room -> type.equals(room.getType()))
//                    .collect(Collectors.toList());
//        }
//        if (view != null && !view.isEmpty()) {
//            rooms = rooms.stream()
//                    .filter(room -> view.equals(room.getView()))
//                    .collect(Collectors.toList());
//        }
//        if (minPrice != null) {
//            rooms = rooms.stream()
//                    .filter(room -> room.getPrice() >= minPrice)
//                    .collect(Collectors.toList());
//        }
//        if (maxPrice != null) {
//            rooms = rooms.stream()
//                    .filter(room -> room.getPrice() <= maxPrice)
//                    .collect(Collectors.toList());
//        }
//        if (floor != null) {
//            rooms = rooms.stream()
//                    .filter(room -> floor.equals(room.getFloor()))
//                    .collect(Collectors.toList());
//        }
//
//        if (checkIn != null && checkOut != null) {
//            rooms = rooms.stream()
//                    .filter(room -> {
//                        List<Booking> bookings = bookingRepository.findBookingsByRoomIdAndDateRange(room.getId(), checkIn, checkOut);
//                        return bookings.isEmpty();
//                    })
//                    .collect(Collectors.toList());
//        }
//
//        return rooms.stream()
//                .filter(room -> "AVAILABLE".equals(room.getStatus()))
//                .map(this::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    public RoomDTO getRoomByHotelIdAndRoomNumber(Long hotelId, String roomNumber) {
//        Room room = roomRepository.findByHotelIdAndRoomNumber(hotelId, roomNumber)
//                .orElseThrow(() -> new ResourceNotFoundException("Room not found for hotelId: " + hotelId + " and roomNumber: " + roomNumber));
//        return toDTO(room);
//    }
//
//    @Transactional
//    public RoomDTO createRoom(RoomDTO roomDTO) {
//        Hotel hotel = hotelRepository.findById(roomDTO.getHotelId())
//                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
//        Room room = new Room();
//        room.setHotel(hotel);
//        room.setRoomNumber(roomDTO.getRoomNumber());
//        room.setType(roomDTO.getType());
//        room.setPrice(roomDTO.getPrice());
//        room.setView(roomDTO.getView());
//        room.setAmenities(roomDTO.getAmenities());
//        room.setStatus("AVAILABLE");
//        room.setFloor(roomDTO.getFloor());
//        return toDTO(roomRepository.save(room));
//    }
//
//    @Transactional
//    public RoomDTO updateRoom(Long id, RoomDTO roomDTO) {
//        Room room = roomRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
//        room.setRoomNumber(roomDTO.getRoomNumber());
//        room.setType(roomDTO.getType());
//        room.setPrice(roomDTO.getPrice());
//        room.setView(roomDTO.getView());
//        room.setAmenities(roomDTO.getAmenities());
//        room.setStatus(roomDTO.getStatus());
//        room.setFloor(roomDTO.getFloor());
//        return toDTO(roomRepository.save(room));
//    }
//
//    @Transactional
//    public RoomDTO updateRoomByHotelIdAndRoomNumber(Long hotelId, String roomNumber, RoomDTO roomDTO) {
//        Room room = roomRepository.findByHotelIdAndRoomNumber(hotelId, roomNumber)
//                .orElseThrow(() -> new ResourceNotFoundException("Room not found for hotelId: " + hotelId + " and roomNumber: " + roomNumber));
//        room.setType(roomDTO.getType());
//        room.setPrice(roomDTO.getPrice());
//        room.setView(roomDTO.getView());
//        room.setAmenities(roomDTO.getAmenities());
//        room.setStatus(roomDTO.getStatus());
//        room.setFloor(roomDTO.getFloor());
//        return toDTO(roomRepository.save(room));
//    }
//
//    @Transactional
//    public void deleteRoom(Long id) {
//        roomRepository.deleteById(id);
//    }
//
//    private RoomDTO toDTO(Room room) {
//        RoomDTO dto = new RoomDTO();
//        dto.setId(room.getId());
//        dto.setHotelId(room.getHotel().getId());
//        dto.setRoomNumber(room.getRoomNumber());
//        dto.setType(room.getType());
//        dto.setPrice(room.getPrice());
//        dto.setView(room.getView());
//        dto.setAmenities(room.getAmenities());
//        dto.setStatus(room.getStatus());
//        dto.setFloor(room.getFloor());
//        return dto;
//    }
//}
package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.RoomDTO;
import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
import com.hotelbookingapplication.palatin.model.Booking;
import com.hotelbookingapplication.palatin.model.Hotel;
import com.hotelbookingapplication.palatin.model.Room;
import com.hotelbookingapplication.palatin.repository.BookingRepository;
import com.hotelbookingapplication.palatin.repository.HotelRepository;
import com.hotelbookingapplication.palatin.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public List<RoomDTO> getAvailableRooms(Long hotelId, String type, String view, Double minPrice, Double maxPrice, Integer floor, LocalDate checkIn, LocalDate checkOut) {
        List<Room> rooms = roomRepository.findByHotelId(hotelId);

        if (type != null && !type.isEmpty()) {
            rooms = rooms.stream()
                    .filter(room -> type.equals(room.getType()))
                    .collect(Collectors.toList());
        }
        if (view != null && !view.isEmpty()) {
            rooms = rooms.stream()
                    .filter(room -> view.equals(room.getView()))
                    .collect(Collectors.toList());
        }
        if (minPrice != null) {
            rooms = rooms.stream()
                    .filter(room -> room.getPrice() >= minPrice)
                    .collect(Collectors.toList());
        }
        if (maxPrice != null) {
            rooms = rooms.stream()
                    .filter(room -> room.getPrice() <= maxPrice)
                    .collect(Collectors.toList());
        }
        if (floor != null) {
            rooms = rooms.stream()
                    .filter(room -> floor.equals(room.getFloor()))
                    .collect(Collectors.toList());
        }

        if (checkIn != null && checkOut != null) {
            rooms = rooms.stream()
                    .filter(room -> {
                        List<Booking> bookings = bookingRepository.findBookingsByRoomIdAndDateRange(room.getId(), checkIn, checkOut);
                        return bookings.isEmpty();
                    })
                    .collect(Collectors.toList());
        }

        return rooms.stream()
                .filter(room -> "AVAILABLE".equals(room.getStatus()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RoomDTO getRoomByHotelIdAndRoomNumber(Long hotelId, String roomNumber) {
        Room room = roomRepository.findByHotelIdAndRoomNumber(hotelId, roomNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found for hotelId: " + hotelId + " and roomNumber: " + roomNumber));
        return toDTO(room);
    }

    @Transactional
    public RoomDTO createRoom(RoomDTO roomDTO) {
        Hotel hotel = hotelRepository.findById(roomDTO.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        Room room = new Room();
        room.setHotel(hotel);
        room.setRoomNumber(roomDTO.getRoomNumber());
        room.setType(roomDTO.getType());
        room.setPrice(roomDTO.getPrice());
        room.setView(roomDTO.getView());
        room.setAmenities(roomDTO.getAmenities());
        room.setStatus("AVAILABLE");
        room.setFloor(roomDTO.getFloor());
        return toDTO(roomRepository.save(room));
    }

    @Transactional
    public RoomDTO updateRoom(Long id, RoomDTO roomDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        room.setRoomNumber(roomDTO.getRoomNumber());
        room.setType(roomDTO.getType());
        room.setPrice(roomDTO.getPrice());
        room.setView(roomDTO.getView());
        room.setAmenities(roomDTO.getAmenities());
        room.setStatus(roomDTO.getStatus());
        room.setFloor(roomDTO.getFloor());
        return toDTO(roomRepository.save(room));
    }

    @Transactional
    public RoomDTO updateRoomByHotelIdAndRoomNumber(Long hotelId, String roomNumber, RoomDTO roomDTO) {
        Room room = roomRepository.findByHotelIdAndRoomNumber(hotelId, roomNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found for hotelId: " + hotelId + " and roomNumber: " + roomNumber));
        room.setType(roomDTO.getType());
        room.setPrice(roomDTO.getPrice());
        room.setView(roomDTO.getView());
        room.setAmenities(roomDTO.getAmenities());
        room.setStatus(roomDTO.getStatus());
        room.setFloor(roomDTO.getFloor());
        return toDTO(roomRepository.save(room));
    }

    @Transactional
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    @Transactional
    public void createFloor(Long hotelId, Integer floorNumber, List<RoomDTO> rooms) {
        if (floorNumber == null || floorNumber <= 0) {
            throw new IllegalArgumentException("Floor number must be a positive integer");
        }
        if (rooms == null || rooms.isEmpty()) {
            throw new IllegalArgumentException("Rooms list cannot be null or empty");
        }
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        // Validate that all rooms have the same floor number as provided
        boolean invalidFloor = rooms.stream()
                .anyMatch(roomDTO -> roomDTO.getFloor() == null || !roomDTO.getFloor().equals(floorNumber));
        if (invalidFloor) {
            throw new IllegalArgumentException("All rooms must have the same floor number as specified");
        }
        rooms.forEach(roomDTO -> {
            Room room = new Room();
            room.setHotel(hotel);
            room.setRoomNumber(roomDTO.getRoomNumber());
            room.setType(roomDTO.getType() != null ? roomDTO.getType() : "SINGLE");
            room.setPrice(roomDTO.getPrice() != null ? roomDTO.getPrice() : 100.0);
            room.setView(roomDTO.getView() != null ? roomDTO.getView() : "CITY");
            room.setAmenities(roomDTO.getAmenities() != null ? roomDTO.getAmenities() : "WiFi");
            room.setStatus(roomDTO.getStatus() != null ? roomDTO.getStatus() : "AVAILABLE");
            room.setFloor(roomDTO.getFloor());
            roomRepository.save(room);
        });
    }

    private RoomDTO toDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setHotelId(room.getHotel().getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setType(room.getType());
        dto.setPrice(room.getPrice());
        dto.setView(room.getView());
        dto.setAmenities(room.getAmenities());
        dto.setStatus(room.getStatus());
        dto.setFloor(room.getFloor());
        return dto;
    }
}