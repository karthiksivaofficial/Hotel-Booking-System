package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.HotelDTO;
import com.hotelbookingapplication.palatin.dto.RoomDTO;
import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
import com.hotelbookingapplication.palatin.model.Hotel;
import com.hotelbookingapplication.palatin.model.Room;
import com.hotelbookingapplication.palatin.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<HotelDTO> getHotelsByCity(String city) {
        return hotelRepository.findByCity(city).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public HotelDTO getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        return toDTO(hotel);
    }

    private HotelDTO toDTO(Hotel hotel) {
        HotelDTO dto = new HotelDTO();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setCity(hotel.getCity());
        dto.setAddress(hotel.getAddress());
        dto.setManagerEmail(hotel.getManager() != null ? hotel.getManager().getEmail() : null);
        dto.setNumberOfFloors(hotel.getNumberOfFloors());
        dto.setRooms(hotel.getRooms().stream().map(this::toRoomDTO).collect(Collectors.toList()));
        return dto;
    }

    private RoomDTO toRoomDTO(Room room) {
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