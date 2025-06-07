package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.ServiceRequestDTO;
import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
import com.hotelbookingapplication.palatin.exception.UnauthorizedException;
import com.hotelbookingapplication.palatin.model.ServiceRequest;
import com.hotelbookingapplication.palatin.model.User;
import com.hotelbookingapplication.palatin.repository.ServiceRequestRepository;
import com.hotelbookingapplication.palatin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LaundryService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ServiceRequestDTO> getAssignedLaundryRequests(String staffEmail) {
        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffEmail));
        Long hotelId = staff.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Staff is not assigned to any hotel");
        }
        return serviceRequestRepository.findLaundryRequestsByHotelIdAndStatus(hotelId, "ASSIGNED").stream()
                .filter(request -> staffEmail.equals(request.getStaff().getEmail()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ServiceRequestDTO> getAllLaundryRequestsForHotel(String staffEmail) {
        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffEmail));
        Long hotelId = staff.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Staff is not assigned to any hotel");
        }
        return serviceRequestRepository.findAllLaundryRequestsByHotelId(hotelId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServiceRequestDTO completeRequest(Long id, String staffEmail) {
        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffEmail));
        Long hotelId = staff.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Staff is not assigned to any hotel");
        }
        ServiceRequest request = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service request not found: " + id));
        if (!request.getHotel().getId().equals(hotelId)) {
            throw new UnauthorizedException("Service request does not belong to staff's hotel");
        }
        if (!"LAUNDRY".equals(request.getServiceType())) {
            throw new UnauthorizedException("Not a laundry request");
        }
        if (request.getStaff() == null || !staffEmail.equals(request.getStaff().getEmail())) {
            throw new UnauthorizedException("Request not assigned to this staff");
        }
        if (!"ASSIGNED".equals(request.getStatus())) {
            throw new UnauthorizedException("Only ASSIGNED requests can be completed");
        }
        request.setStatus("COMPLETED");
        return toDTO(serviceRequestRepository.save(request));
    }

    private ServiceRequestDTO toDTO(ServiceRequest request) {
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setId(request.getId());
        dto.setUserEmail(request.getUser().getEmail());
        dto.setHotelId(request.getHotel().getId());
        dto.setServiceType(request.getServiceType());
        dto.setNotes(request.getNotes());
        dto.setRequestTime(request.getRequestTime());
        dto.setStatus(request.getStatus());
        dto.setRoomNumber(request.getRoomNumber());
        dto.setStaffId(request.getStaff() != null ? request.getStaff().getId() : null);
        dto.setStaffName(request.getStaff() != null ? request.getStaff().getName() : null);
        return dto;
    }
}