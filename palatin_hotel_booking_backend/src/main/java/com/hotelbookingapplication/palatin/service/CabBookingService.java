//package com.hotelbookingapplication.palatin.service;
//
//import com.hotelbookingapplication.palatin.dto.CabBookingDTO;
//import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
//import com.hotelbookingapplication.palatin.exception.UnauthorizedException;
//import com.hotelbookingapplication.palatin.exception.ValidationException;
//import com.hotelbookingapplication.palatin.model.CabBooking;
//import com.hotelbookingapplication.palatin.model.User;
//import com.hotelbookingapplication.palatin.repository.CabBookingRepository;
//import com.hotelbookingapplication.palatin.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class CabBookingService {
//
//    @Autowired
//    private CabBookingRepository cabBookingRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private EmailService emailService;
//
//    public List<CabBookingDTO> getCabBookingsByUser(String userEmail) {
//        return cabBookingRepository.findByUserEmail(userEmail).stream()
//                .map(this::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<CabBookingDTO> getAssignedCabBookings(String driverEmail) {
//        User driver = userRepository.findByEmail(driverEmail)
//                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
//        Long hotelId = driver.getHotelId();
//        if (hotelId == null) {
//            throw new UnauthorizedException("Driver is not assigned to any hotel");
//        }
//        return cabBookingRepository.findByDriverEmail(driverEmail).stream()
//                .filter(booking -> booking.getUser().getHotelId() == null || booking.getUser().getHotelId().equals(hotelId))
//                .map(this::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional
//    public CabBookingDTO createCabBooking(CabBookingDTO cabBookingDTO) {
//        User user = userRepository.findByEmail(cabBookingDTO.getUserEmail())
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//        CabBooking cabBooking = new CabBooking();
//        cabBooking.setUser(user);
//        cabBooking.setPickupLocation(cabBookingDTO.getPickupLocation());
//        cabBooking.setDropLocation(cabBookingDTO.getDropLocation());
//        cabBooking.setPickupTime(cabBookingDTO.getPickupTime());
//        cabBooking.setVehicleType(cabBookingDTO.getVehicleType());
//        cabBooking.setStatus("PENDING");
//        CabBooking savedBooking = cabBookingRepository.save(cabBooking);
//        emailService.sendCabBookingConfirmation(user.getEmail(), cabBookingDTO);
//        return toDTO(savedBooking);
//    }
//
//    @Transactional
//    public CabBookingDTO cancelCabBooking(Long id, String userEmail) {
//        CabBooking cabBooking = cabBookingRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Cab booking not found"));
//        if (!cabBooking.getUser().getEmail().equals(userEmail)) {
//            throw new UnauthorizedException("User not authorized to cancel this booking");
//        }
//        if ("CANCELLED".equals(cabBooking.getStatus())) {
//            throw new ValidationException("Cab booking is already cancelled");
//        }
//        cabBooking.setStatus("CANCELLED");
//        CabBooking savedBooking = cabBookingRepository.save(cabBooking);
//        emailService.sendCabCancellationEmail(cabBooking.getUser().getEmail(), toDTO(savedBooking));
//        return toDTO(savedBooking);
//    }
//
//    @Transactional
//    public CabBookingDTO assignDriver(Long id, String driverEmail, String receptionistEmail) {
//        User receptionist = userRepository.findByEmail(receptionistEmail)
//                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found"));
//        Long hotelId = receptionist.getHotelId();
//        if (hotelId == null) {
//            throw new UnauthorizedException("Receptionist is not assigned to any hotel");
//        }
//        CabBooking cabBooking = cabBookingRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Cab booking not found"));
//        User driver = userRepository.findByEmail(driverEmail)
//                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
//        if (!"CAB".equals(driver.getRole())) {
//            throw new ValidationException("Assigned user is not a driver");
//        }
//        if (!hotelId.equals(driver.getHotelId())) {
//            throw new UnauthorizedException("Driver is not assigned to the same hotel");
//        }
//        cabBooking.setDriverEmail(driverEmail);
//        cabBooking.setStatus("ASSIGNED");
//        return toDTO(cabBookingRepository.save(cabBooking));
//    }
//
//    CabBookingDTO toDTO(CabBooking cabBooking) {
//        CabBookingDTO dto = new CabBookingDTO();
//        dto.setId(cabBooking.getId());
//        dto.setUserEmail(cabBooking.getUser().getEmail());
//        dto.setPickupLocation(cabBooking.getPickupLocation());
//        dto.setDropLocation(cabBooking.getDropLocation());
//        dto.setPickupTime(cabBooking.getPickupTime());
//        dto.setVehicleType(cabBooking.getVehicleType());
//        dto.setStatus(cabBooking.getStatus());
//        dto.setDriverEmail(cabBooking.getDriverEmail());
//        dto.setAdditionalCharges(cabBooking.getAdditionalCharges());
//        return dto;
//    }
//}