package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.RestaurantReservationDTO;
import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
import com.hotelbookingapplication.palatin.model.RestaurantReservation;
import com.hotelbookingapplication.palatin.model.Hotel;
import com.hotelbookingapplication.palatin.model.User;
import com.hotelbookingapplication.palatin.repository.RestaurantReservationRepository;
import com.hotelbookingapplication.palatin.repository.HotelRepository;
import com.hotelbookingapplication.palatin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantReservationService {

    @Autowired
    private RestaurantReservationRepository restaurantReservationRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    public List<RestaurantReservationDTO> getReservationsByUser(String email) {
        return restaurantReservationRepository.findByUserEmail(email).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<RestaurantReservationDTO> getReservationsByHotel(Long hotelId) {
        return restaurantReservationRepository.findByHotelId(hotelId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RestaurantReservationDTO createReservation(RestaurantReservationDTO reservationDTO) {
        Hotel hotel = hotelRepository.findById(reservationDTO.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        User user = userRepository.findByEmail(reservationDTO.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        RestaurantReservation reservation = new RestaurantReservation();
        reservation.setHotel(hotel);
        reservation.setUser(user);
        reservation.setReservationTime(reservationDTO.getReservationTime());
        reservation.setSeats(reservationDTO.getSeats());
        reservation.setSpecialRequests(reservationDTO.getSpecialRequests());
        reservation.setStatus("CONFIRMED");
        return toDTO(restaurantReservationRepository.save(reservation));
    }

    @Transactional
    public RestaurantReservationDTO cancelReservation(Long id) {
        RestaurantReservation reservation = restaurantReservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        reservation.setStatus("CANCELLED");
        return toDTO(restaurantReservationRepository.save(reservation));
    }

    private RestaurantReservationDTO toDTO(RestaurantReservation reservation) {
        RestaurantReservationDTO dto = new RestaurantReservationDTO();
        dto.setId(reservation.getId());
        dto.setHotelId(reservation.getHotel().getId());
        dto.setUserEmail(reservation.getUser().getEmail());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setSeats(reservation.getSeats());
        dto.setSpecialRequests(reservation.getSpecialRequests());
        dto.setStatus(reservation.getStatus());
        return dto;
    }
}