package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.ServiceRequestDTO;
import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
import com.hotelbookingapplication.palatin.exception.UnauthorizedException;
import com.hotelbookingapplication.palatin.model.Booking;
import com.hotelbookingapplication.palatin.model.Hotel;
import com.hotelbookingapplication.palatin.model.ServiceRequest;
import com.hotelbookingapplication.palatin.model.User;
import com.hotelbookingapplication.palatin.repository.BookingRepository;
import com.hotelbookingapplication.palatin.repository.HotelRepository;
import com.hotelbookingapplication.palatin.repository.ServiceRequestRepository;
import com.hotelbookingapplication.palatin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public List<ServiceRequestDTO> getRequestsByUser(String email) {
        return serviceRequestRepository.findByUserEmail(email).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ServiceRequestDTO> getRequestsByHotel(Long hotelId, String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        if (!hotelId.equals(manager.getHotelId())) {
            throw new UnauthorizedException("Manager is not authorized for this hotel");
        }
        return serviceRequestRepository.findByHotelId(hotelId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServiceRequestDTO createRequest(ServiceRequestDTO requestDTO) {
        // Validate user
        if (requestDTO.getUserEmail() == null) {
            throw new IllegalArgumentException("User email must not be null");
        }
        User user = userRepository.findByEmail(requestDTO.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + requestDTO.getUserEmail()));

        // Validate room number
        if (requestDTO.getRoomNumber() == null || requestDTO.getRoomNumber().isEmpty()) {
            throw new IllegalArgumentException("Room number must not be null or empty");
        }

        // Find checked-in bookings for the user
        List<Booking> checkedInBookings = bookingRepository.findByUserEmail(user.getEmail()).stream()
                .filter(booking -> "CHECKED_IN".equals(booking.getStatus()))
                .filter(booking -> booking.getCheckInDate().isBefore(LocalDate.now().plusDays(1))
                        && booking.getCheckOutDate().isAfter(LocalDate.now().minusDays(1)))
                .collect(Collectors.toList());

        if (checkedInBookings.isEmpty()) {
            throw new UnauthorizedException("User has no checked-in bookings");
        }

        // Validate room number and get associated hotel
        Booking matchingBooking = checkedInBookings.stream()
                .filter(booking -> booking.getRoom().getRoomNumber().equals(requestDTO.getRoomNumber()))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedException("Room " + requestDTO.getRoomNumber() + " is not booked by the user or not checked in"));

        // Use the hotel from the booking instead of requestDTO.getHotelId()
        Hotel hotel = matchingBooking.getHotel();
        if (hotel == null || hotel.getId() == null) {
            throw new IllegalStateException("Booking hotel is null or has no ID");
        }

        // Validate service type
        if (requestDTO.getServiceType() == null || requestDTO.getServiceType().isEmpty()) {
            throw new IllegalArgumentException("Service type must not be null or empty");
        }

        // Create service request
        ServiceRequest request = new ServiceRequest();
        request.setUser(user);
        request.setHotel(hotel);
        request.setServiceType(requestDTO.getServiceType());
        request.setNotes(requestDTO.getNotes());
        request.setRoomNumber(requestDTO.getRoomNumber());
        request.setStatus("PENDING");
        request.setRequestTime(LocalDateTime.now());

        ServiceRequest savedRequest = serviceRequestRepository.save(request);
        return toDTO(savedRequest);
    }

    @Transactional
    public ServiceRequestDTO updateRequestStatus(Long id, String status) {
        ServiceRequest request = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
        request.setStatus(status);
        return toDTO(serviceRequestRepository.save(request));
    }

    @Transactional
    public ServiceRequestDTO assignRequest(Long id, Long staffId) {
        ServiceRequest request = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        if (!request.getHotel().getId().equals(staff.getHotelId())) {
            throw new UnauthorizedException("Staff is not assigned to the same hotel");
        }
        if (request.getStaff() != null) {
            throw new UnauthorizedException("Request is already assigned to a staff member");
        }
        if (!"PENDING".equals(request.getStatus())) {
            throw new UnauthorizedException("Only PENDING requests can be assigned");
        }
        request.setStaff(staff);
        request.setStatus("ASSIGNED");
        return toDTO(serviceRequestRepository.save(request));
    }

    public List<String> getCheckedInRoomNumbers(String email) {
        return bookingRepository.findByUserEmail(email).stream()
                .filter(booking -> "CHECKED_IN".equals(booking.getStatus()))
                .filter(booking -> booking.getCheckInDate().isBefore(LocalDate.now().plusDays(1))
                        && booking.getCheckOutDate().isAfter(LocalDate.now().minusDays(1)))
                .map(booking -> booking.getRoom().getRoomNumber())
                .collect(Collectors.toList());
    }

    private ServiceRequestDTO toDTO(ServiceRequest request) {
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setId(request.getId());
        dto.setUserEmail(request.getUser().getEmail());
        dto.setHotelId(request.getHotel().getId());
        dto.setServiceType(request.getServiceType());
        dto.setNotes(request.getNotes());
        dto.setRoomNumber(request.getRoomNumber());
        dto.setRequestTime(request.getRequestTime());
        dto.setStatus(request.getStatus());
        dto.setStaffId(request.getStaff() != null ? request.getStaff().getId() : null);
        dto.setStaffName(request.getStaff() != null ? request.getStaff().getName() : null);
        return dto;
    }
}