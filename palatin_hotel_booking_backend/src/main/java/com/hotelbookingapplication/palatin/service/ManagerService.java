package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.BookingDTO;
import com.hotelbookingapplication.palatin.dto.FeedbackDTO;
import com.hotelbookingapplication.palatin.dto.ReportDTO;
import com.hotelbookingapplication.palatin.dto.RoomDTO;
import com.hotelbookingapplication.palatin.dto.UserDTO;
import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
import com.hotelbookingapplication.palatin.exception.UnauthorizedException;
import com.hotelbookingapplication.palatin.model.Booking;
import com.hotelbookingapplication.palatin.model.Hotel;
import com.hotelbookingapplication.palatin.model.Room;
import com.hotelbookingapplication.palatin.model.User;
import com.hotelbookingapplication.palatin.repository.BookingRepository;
import com.hotelbookingapplication.palatin.repository.HotelRepository;
import com.hotelbookingapplication.palatin.repository.RoomRepository;
import com.hotelbookingapplication.palatin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ManagerService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

    private static final Set<String> ALLOWED_STAFF_ROLES = new HashSet<>(Arrays.asList(
            "RECEPTIONIST", "LAUNDRY", "CAB"
    ));

    public ReportDTO generateReport(String managerEmail, LocalDate startDate, LocalDate endDate) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        Long hotelId = manager.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Manager is not assigned to any hotel");
        }
        List<Booking> bookings = bookingRepository.findBookingsByHotelIdAndDateRange(hotelId, startDate, endDate);
        double revenue = bookings.stream()
                .filter(booking -> "PAID".equals(booking.getPaymentStatus()))
                .mapToDouble(Booking::getTotalPrice)
                .sum();
        int totalRooms = roomRepository.findByHotelIdAndStatus(hotelId, "AVAILABLE").size() +
                roomRepository.findByHotelIdAndStatus(hotelId, "OCCUPIED").size();
        double occupancyRate = totalRooms > 0 ? ((double) bookings.size() / totalRooms) * 100 : 0;

        ReportDTO report = new ReportDTO();
        report.setTotalRevenue(revenue);
        report.setTotalBookings((long) bookings.size());
        report.setOccupancyRate(occupancyRate);
        return report;
    }

    public List<FeedbackDTO> getFeedback(String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        Long hotelId = manager.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Manager is not assigned to any hotel");
        }
        return feedbackService.getFeedbackByHotel(hotelId);
    }

    @Transactional
    public UserDTO addStaff(UserDTO userDTO, String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        Long hotelId = manager.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Manager is not assigned to any hotel");
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ResourceNotFoundException("Email already exists");
        }
        if (!ALLOWED_STAFF_ROLES.contains(userDTO.getRole())) {
            throw new IllegalArgumentException("Invalid staff role");
        }
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setActive(true);
        user.setPassword("$2a$10$ACpvEb6YEvHKYtoQEDmMre0xJGWmeacL1B3Nypr6sGEaWlqAWUPWS"); // Default password
        user.setHotelId(hotelId);
        return toDTO(userRepository.save(user));
    }

    @Transactional
    public void deactivateStaff(Long id, String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        Long hotelId = manager.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Manager is not assigned to any hotel");
        }
        User staff = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        if (!hotelId.equals(staff.getHotelId())) {
            throw new UnauthorizedException("Staff is not assigned to manager's hotel");
        }
        if (!ALLOWED_STAFF_ROLES.contains(staff.getRole())) {
            throw new UnauthorizedException("Cannot modify staff with this role");
        }
        staff.setActive(false);
        userRepository.save(staff);
    }

    @Transactional
    public void deactivateStaffByEmail(String email, String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        Long hotelId = manager.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Manager is not assigned to any hotel");
        }
        User staff = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with email: " + email));
        if (!hotelId.equals(staff.getHotelId())) {
            throw new UnauthorizedException("Staff is not assigned to manager's hotel");
        }
        if (!ALLOWED_STAFF_ROLES.contains(staff.getRole())) {
            throw new UnauthorizedException("Cannot modify staff with this role");
        }
        staff.setActive(false);
        userRepository.save(staff);
    }

    @Transactional
    public void updateStaffStatus(Long id, boolean active, String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        Long hotelId = manager.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Manager is not assigned to any hotel");
        }
        User staff = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        if (!hotelId.equals(staff.getHotelId())) {
            throw new UnauthorizedException("Staff is not assigned to manager's hotel");
        }
        if (!ALLOWED_STAFF_ROLES.contains(staff.getRole())) {
            throw new UnauthorizedException("Cannot modify staff with this role");
        }
        staff.setActive(active);
        userRepository.save(staff);
    }

    @Transactional
    public void updateRoomStatus(Long id, String status, String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        Long hotelId = manager.getHotelId();
        if (hotelId == null) {
            throw new UnauthorizedException("Manager is not assigned to any hotel");
        }
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (!room.getHotel().getId().equals(hotelId)) {
            throw new UnauthorizedException("Room does not belong to manager's hotel");
        }
        room.setStatus(status);
        roomRepository.save(room);
    }

    @Transactional
    public void updateRoomStatusByRoomNumber(String roomNumber, Long hotelId, String status, String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        if (!hotelId.equals(manager.getHotelId())) {
            throw new UnauthorizedException("Manager is not authorized for this hotel");
        }
        Room room = roomRepository.findByHotelIdAndRoomNumber(hotelId, roomNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with number: " + roomNumber));
        room.setStatus(status);
        roomRepository.save(room);
    }

    @Transactional
    public List<RoomDTO> getRoomsByHotelId(Long hotelId, String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        if (!hotelId.equals(manager.getHotelId())) {
            throw new UnauthorizedException("Manager is not authorized for this hotel");
        }
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        return rooms.stream().map(this::toRoomDTO).collect(Collectors.toList());
    }

    @Transactional
    public void addFloor(Long hotelId, Integer floorNumber, List<RoomDTO> rooms, String managerEmail) {
        if (floorNumber == null || floorNumber <= 0) {
            throw new IllegalArgumentException("Floor number must be a positive integer");
        }
        if (rooms == null || rooms.isEmpty()) {
            throw new IllegalArgumentException("Rooms list cannot be null or empty");
        }
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        if (!hotelId.equals(manager.getHotelId())) {
            throw new UnauthorizedException("Manager is not authorized for this hotel");
        }
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
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

    @Transactional
    public void updateRoomByHotelIdAndRoomNumber(Long hotelId, String roomNumber, RoomDTO roomDTO, String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        if (!hotelId.equals(manager.getHotelId())) {
            throw new UnauthorizedException("Manager is not authorized for this hotel");
        }
        roomService.updateRoomByHotelIdAndRoomNumber(hotelId, roomNumber, roomDTO);
    }

    public List<BookingDTO> getBookingsByHotelWithFilters(Long hotelId, LocalDate startDate, LocalDate endDate, String status, String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        if (!hotelId.equals(manager.getHotelId())) {
            throw new UnauthorizedException("Manager is not authorized for this hotel");
        }
        List<Booking> bookings = bookingRepository.findBookingsByHotelIdWithFilters(hotelId, startDate, endDate, status);
        return bookings.stream().map(bookingService::toDTO).collect(Collectors.toList());
    }

    public double getRevenueByHotelWithFilters(Long hotelId, LocalDate startDate, LocalDate endDate, String status, String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        if (!hotelId.equals(manager.getHotelId())) {
            throw new UnauthorizedException("Manager is not authorized for this hotel");
        }
        List<Booking> bookings = bookingRepository.findBookingsByHotelIdWithFilters(hotelId, startDate, endDate, status);
        return bookings.stream()
                .filter(booking -> "PAID".equals(booking.getPaymentStatus()))
                .mapToDouble(Booking::getTotalPrice)
                .sum();
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setHotelId(user.getHotelId());
        return dto;
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