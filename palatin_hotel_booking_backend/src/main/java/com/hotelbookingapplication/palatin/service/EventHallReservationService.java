package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.EventHallReservationDTO;
import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
import com.hotelbookingapplication.palatin.model.EventHallReservation;
import com.hotelbookingapplication.palatin.model.Hotel;
import com.hotelbookingapplication.palatin.model.User;
import com.hotelbookingapplication.palatin.repository.EventHallReservationRepository;
import com.hotelbookingapplication.palatin.repository.HotelRepository;
import com.hotelbookingapplication.palatin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventHallReservationService {

    @Autowired
    private EventHallReservationRepository eventHallReservationRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    public List<EventHallReservationDTO> getReservationsByUser(String email) {
        return eventHallReservationRepository.findByUserEmail(email).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EventHallReservationDTO> getReservationsByHotel(Long hotelId) {
        return eventHallReservationRepository.findByHotelId(hotelId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventHallReservationDTO createReservation(EventHallReservationDTO reservationDTO) {
        Hotel hotel = hotelRepository.findById(reservationDTO.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        User user = userRepository.findByEmail(reservationDTO.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        EventHallReservation reservation = new EventHallReservation();
        reservation.setHotel(hotel);
        reservation.setUser(user);
        reservation.setOccasionType(reservationDTO.getOccasionType());
        reservation.setGuests(reservationDTO.getGuests());
        reservation.setEventDate(reservationDTO.getEventDate());
        reservation.setAddOns(reservationDTO.getAddOns());
        reservation.setStatus("CONFIRMED");
        return toDTO(eventHallReservationRepository.save(reservation));
    }

    @Transactional
    public EventHallReservationDTO cancelReservation(Long id) {
        EventHallReservation reservation = eventHallReservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        reservation.setStatus("CANCELLED");
        return toDTO(eventHallReservationRepository.save(reservation));
    }

    private EventHallReservationDTO toDTO(EventHallReservation reservation) {
        EventHallReservationDTO dto = new EventHallReservationDTO();
        dto.setId(reservation.getId());
        dto.setHotelId(reservation.getHotel().getId());
        dto.setUserEmail(reservation.getUser().getEmail());
        dto.setOccasionType(reservation.getOccasionType());
        dto.setGuests(reservation.getGuests());
        dto.setEventDate(reservation.getEventDate());
        dto.setAddOns(reservation.getAddOns());
        dto.setStatus(reservation.getStatus());
        return dto;
    }
}