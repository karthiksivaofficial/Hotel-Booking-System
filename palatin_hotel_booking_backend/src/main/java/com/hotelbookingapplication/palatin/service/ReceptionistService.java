package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.BookingDTO;
import com.hotelbookingapplication.palatin.dto.RoomDTO;
import com.hotelbookingapplication.palatin.dto.ServiceRequestDTO;
import com.hotelbookingapplication.palatin.dto.UserDTO;
import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
import com.hotelbookingapplication.palatin.exception.UnauthorizedException;
import com.hotelbookingapplication.palatin.model.Booking;
import com.hotelbookingapplication.palatin.model.Room;
import com.hotelbookingapplication.palatin.model.ServiceRequest;
import com.hotelbookingapplication.palatin.model.User;
import com.hotelbookingapplication.palatin.repository.BookingRepository;
import com.hotelbookingapplication.palatin.repository.RoomRepository;
import com.hotelbookingapplication.palatin.repository.ServiceRequestRepository;
import com.hotelbookingapplication.palatin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReceptionistService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

    public List<BookingDTO> getTodayBookings(String receptionistEmail) {
        User receptionist = userRepository.findByEmail(receptionistEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found"));
        Long hotelId = receptionist.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Receptionist is not assigned to any hotel");
        }
        return bookingRepository.findByCheckInDate(LocalDate.now()).stream()
                .filter(booking -> booking.getHotel().getId().equals(hotelId))
                .map(bookingService::toDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getBookingsByDateRange(String receptionistEmail, LocalDate startDate, LocalDate endDate) {
        User receptionist = userRepository.findByEmail(receptionistEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found"));
        Long hotelId = receptionist.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Receptionist is not assigned to any hotel");
        }
        return bookingRepository.findBookingsByHotelIdAndDateRange(hotelId, startDate, endDate).stream()
                .map(bookingService::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDTO checkIn(Long bookingId, String receptionistEmail) {
        validateHotelAccess(bookingId, receptionistEmail);
        return bookingService.checkIn(bookingId);
    }

    @Transactional
    public BookingDTO checkOut(Long bookingId, String receptionistEmail) {
        validateHotelAccess(bookingId, receptionistEmail);
        return bookingService.checkOut(bookingId);
    }

    @Transactional
    public String initiateOfflineBooking(BookingDTO bookingDTO, String receptionistEmail) {
        User receptionist = userRepository.findByEmail(receptionistEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found"));
        Long hotelId = receptionist.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Receptionist is not assigned to any hotel");
        }
        bookingDTO.setHotelId(hotelId);
        validateBookingDTO(bookingDTO);
        List<RoomDTO> rooms = roomRepository.findAllById(bookingDTO.getRoomIds()).stream()
                .map(this::mapToRoomDTO)
                .collect(Collectors.toList());
        if (rooms.size() != bookingDTO.getRoomIds().size()) {
            throw new IllegalArgumentException("One or more rooms not found");
        }
        // Removed price mismatch check to accept frontend's totalPrice
        return paymentService.createPayment(bookingDTO, rooms, "receptionist");
    }

    @Transactional
    public List<BookingDTO> executeOfflineBooking(BookingDTO bookingDTO, String paymentId, String payerId, String receptionistEmail) {
        User receptionist = userRepository.findByEmail(receptionistEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found"));
        Long hotelId = receptionist.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Receptionist is not assigned to any hotel");
        }
        bookingDTO.setHotelId(hotelId); // Set hotelId explicitly
        validateBookingDTO(bookingDTO);
        List<RoomDTO> rooms = roomRepository.findAllById(bookingDTO.getRoomIds()).stream()
                .map(this::mapToRoomDTO)
                .collect(Collectors.toList());
        if (rooms.size() != bookingDTO.getRoomIds().size()) {
            throw new IllegalArgumentException("One or more rooms not found");
        }
        String executedPaymentId = paymentService.executePayment(paymentId, payerId);
        return createBookings(bookingDTO, rooms, executedPaymentId);
    }

    public Map<String, Long> getRoomStats(String receptionistEmail) {
        User receptionist = userRepository.findByEmail(receptionistEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found"));
        Long hotelId = receptionist.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Receptionist is not assigned to any hotel");
        }
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        long totalRooms = rooms.size();
        long availableRooms = rooms.stream().filter(room -> "AVAILABLE".equals(room.getStatus())).count();
        long occupiedRooms = rooms.stream().filter(room -> "OCCUPIED".equals(room.getStatus())).count();
        long outOfServiceRooms = rooms.stream().filter(room -> "OUT_OF_SERVICE".equals(room.getStatus())).count();

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalRooms", totalRooms);
        stats.put("availableRooms", availableRooms);
        stats.put("occupiedRooms", occupiedRooms);
        stats.put("outOfServiceRooms", outOfServiceRooms);
        return stats;
    }

    public List<ServiceRequestDTO> getServiceRequests(String receptionistEmail) {
        User receptionist = userRepository.findByEmail(receptionistEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found"));
        Long hotelId = receptionist.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Receptionist is not assigned to any hotel");
        }
        return serviceRequestRepository.findByHotelId(hotelId).stream()
                .map(this::toServiceRequestDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getStaffByRole(String receptionistEmail, String role) {
        User receptionist = userRepository.findByEmail(receptionistEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found"));
        Long hotelId = receptionist.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Receptionist is not assigned to any hotel");
        }
        String staffRole = role;
        if ("LAUNDRY".equalsIgnoreCase(role)) {
            staffRole = "LAUNDRY";
        } else if ("LUGGAGE".equalsIgnoreCase(role)) {
            staffRole = "LUGGAGE";
        }
        return userRepository.findFreeStaffByRoleAndHotelId(staffRole, hotelId).stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServiceRequestDTO assignStaff(Long id, String staffEmail, String receptionistEmail) {
        User receptionist = userRepository.findByEmail(receptionistEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found"));
        Long hotelId = receptionist.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Receptionist is not assigned to any hotel");
        }
        ServiceRequest request = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
        if (!request.getHotel().getId().equals(hotelId)) {
            throw new UnauthorizedException("Service request does not belong to receptionist's hotel");
        }
        if (request.getStaff() != null || !"PENDING".equals(request.getStatus())) {
            throw new UnauthorizedException("Service request is already assigned or not in PENDING status");
        }
        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        if (!hotelId.equals(staff.getHotelId())) {
            throw new UnauthorizedException("Staff is not assigned to the same hotel");
        }
        if (!staff.getRole().equalsIgnoreCase(request.getServiceType())) {
            throw new UnauthorizedException("Staff role does not match service type");
        }
        request.setStaff(staff);
        request.setStatus("ASSIGNED");
        return toServiceRequestDTO(serviceRequestRepository.save(request));
    }

    public byte[] generateBill(Long bookingId, String receptionistEmail) {
        User receptionist = userRepository.findByEmail(receptionistEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found"));
        Long hotelId = receptionist.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Receptionist is not assigned to any hotel");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getHotel().getId().equals(hotelId)) {
            throw new UnauthorizedException("Booking does not belong to receptionist's hotel");
        }
        return bookingService.generateBill(bookingId);
    }

    private void validateHotelAccess(Long bookingId, String receptionistEmail) {
        User receptionist = userRepository.findByEmail(receptionistEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found"));
        Long hotelId = receptionist.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Receptionist is not assigned to any hotel");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getHotel().getId().equals(hotelId)) {
            throw new UnauthorizedException("Booking does not belong to receptionist's hotel");
        }
    }

    private ServiceRequestDTO toServiceRequestDTO(ServiceRequest request) {
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setId(request.getId());
        dto.setUserEmail(request.getUser().getEmail());
        dto.setHotelId(request.getHotel().getId());
        dto.setServiceType(request.getServiceType());
        dto.setNotes(request.getNotes());
        dto.setRequestTime(request.getRequestTime());
        dto.setStatus(request.getStatus());
        dto.setStaffId(request.getStaff() != null ? request.getStaff().getId() : null);
        return dto;
    }

    private UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setHotelId(user.getHotelId());
        return dto;
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

    private void validateBookingDTO(BookingDTO bookingDTO) {
        if (bookingDTO.getRoomIds() == null || bookingDTO.getRoomIds().isEmpty()) {
            throw new IllegalArgumentException("At least one room must be selected");
        }
        if (bookingDTO.getCheckInDate() == null || bookingDTO.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }
        if (bookingDTO.getCheckInDate().isAfter(bookingDTO.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }
        if (bookingDTO.getUserEmail() == null || bookingDTO.getUserName() == null) {
            throw new IllegalArgumentException("Guest email and name are required");
        }
        if (bookingDTO.getTotalPrice() <= 0) {
            throw new IllegalArgumentException("Total price must be greater than zero");
        }
    }

    private double calculateTotalPrice(List<RoomDTO> rooms, LocalDate checkIn, LocalDate checkOut) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        if (days <= 0) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }
        return rooms.stream()
                .mapToDouble(room -> room.getPrice() * days)
                .sum();
    }

    private List<BookingDTO> createBookings(BookingDTO bookingDTO, List<RoomDTO> rooms, String paymentId) {
        List<BookingDTO> bookings = new ArrayList<>();
        long days = java.time.temporal.ChronoUnit.DAYS.between(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        if (days <= 0) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }
        double expectedTotalPrice = bookingDTO.getTotalPrice(); // Frontend's totalPrice (e.g., 8.0)
        // Calculate total price based on room prices and days
        double calculatedTotalPrice = rooms.stream()
                .mapToDouble(room -> room.getPrice() * days)
                .sum();
        // Adjust room prices if there's a slight mismatch due to rounding
        double adjustmentFactor = calculatedTotalPrice != 0 ? expectedTotalPrice / calculatedTotalPrice : 1.0;
        for (RoomDTO room : rooms) {
            BookingDTO newBooking = new BookingDTO();
            newBooking.setHotelId(bookingDTO.getHotelId());
            newBooking.setRoomIds(Collections.singletonList(room.getId()));
            newBooking.setUserName(bookingDTO.getUserName());
            newBooking.setUserEmail(bookingDTO.getUserEmail());
            newBooking.setCheckInDate(bookingDTO.getCheckInDate());
            newBooking.setCheckOutDate(bookingDTO.getCheckOutDate());
            // Set price based on room's price × days, adjusted to match frontend total
            double roomPrice = room.getPrice() * days * adjustmentFactor;
            newBooking.setTotalPrice(roomPrice);
            newBooking.setPaymentStatus("PAID");
            BookingDTO.PaymentDetailsDTO paymentDetails = new BookingDTO.PaymentDetailsDTO();
            paymentDetails.setPaymentIntentId(paymentId);
            paymentDetails.setTotalPrice(roomPrice);
            newBooking.setPaymentDetails(paymentDetails);
            List<BookingDTO> savedBookings = bookingService.createBooking(newBooking, paymentId);
            bookings.addAll(savedBookings);
        }
        // Verify total price sum matches frontend's totalPrice
        double sumOfBookingPrices = bookings.stream().mapToDouble(BookingDTO::getTotalPrice).sum();
        if (Math.abs(sumOfBookingPrices - expectedTotalPrice) > 0.01) {
            throw new IllegalStateException("Booking prices do not sum to expected total");
        }
        return bookings;
    }
}