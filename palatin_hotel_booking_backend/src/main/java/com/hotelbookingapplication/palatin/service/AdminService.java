package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.*;
import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
import com.hotelbookingapplication.palatin.model.Booking;
import com.hotelbookingapplication.palatin.model.Feedback;
import com.hotelbookingapplication.palatin.model.Hotel;
import com.hotelbookingapplication.palatin.model.Room;
import com.hotelbookingapplication.palatin.model.User;
import com.hotelbookingapplication.palatin.repository.BookingRepository;
import com.hotelbookingapplication.palatin.repository.FeedbackRepository;
import com.hotelbookingapplication.palatin.repository.HotelRepository;
import com.hotelbookingapplication.palatin.repository.RoomRepository;
import com.hotelbookingapplication.palatin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Service
public class AdminService {

    private static final Logger LOGGER = Logger.getLogger(AdminService.class.getName());

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::toHotelDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public HotelDTO createHotel(HotelDTO hotelDTO) {
        Hotel hotel = new Hotel();
        hotel.setName(hotelDTO.getName());
        hotel.setCity(hotelDTO.getCity());
        hotel.setAddress(hotelDTO.getAddress());
        hotel.setNumberOfFloors(hotelDTO.getNumberOfFloors());
        User manager = userRepository.findByEmail(hotelDTO.getManagerEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        hotel.setManager(manager);
        Hotel savedHotel = hotelRepository.save(hotel);
        manager.setHotelId(savedHotel.getId());
        userRepository.save(manager);
        return toHotelDTO(savedHotel);
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setActive(userDTO.isActive());
        user.setHotelId(userDTO.getHotelId());
        return toUserDTO(userRepository.save(user));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getUsersByHotel(Long hotelId) {
        if (hotelId != null) {
            return userRepository.findByHotelId(hotelId).stream()
                    .map(this::toUserDTO)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAll().stream()
                    .map(this::toUserDTO)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setActive(userDTO.isActive());
        user.setHotelId(userDTO.getHotelId());
        return toUserDTO(userRepository.save(user));
    }

    @Transactional
    public void deactivateUser(Long id, String email) {
        User user = userRepository.findByIdOrEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void toggleUserStatus(Long id, String email) {
        User user = userRepository.findByIdOrEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    public ReportDTO getReport(Long hotelId, LocalDate startDate, LocalDate endDate) {
        ReportDTO report = new ReportDTO();
        List<Booking> bookings;
        List<Room> rooms;
        List<User> users;
        Hotel hotel = null;

        if (hotelId != null) {
            hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
            bookings = bookingRepository.findBookingsByHotelIdAndDateRange(hotelId, startDate, endDate);
            rooms = roomRepository.findByHotelId(hotelId);
            users = userRepository.findByHotelId(hotelId);
            report.setHotelId(hotelId);
            report.setHotelName(hotel.getName());
            report.setHotel(toHotelDTO(hotel));
        } else {
            bookings = bookingRepository.findByDateRange(startDate, endDate);
            rooms = roomRepository.findAll();
            users = userRepository.findAll();
            report.setHotelName("All Hotels");
            report.setHotel(null);
        }

        double totalRevenue = bookings.stream()
                .filter(b -> "PAID".equals(b.getPaymentStatus()))
                .mapToDouble(Booking::getTotalPrice)
                .sum();
        report.setTotalRevenue(totalRevenue);

        int totalBookings = bookings.size();
        report.setTotalBookings((long) totalBookings);

        long totalRoomDays = rooms.size() * (endDate.toEpochDay() - startDate.toEpochDay() + 1);
        long bookedRoomDays = bookings.stream()
                .mapToLong(b -> b.getCheckOutDate().toEpochDay() - b.getCheckInDate().toEpochDay() + 1)
                .sum();
        double occupancyRate = totalRoomDays > 0 ? (double) bookedRoomDays / totalRoomDays : 0.0;
        report.setOccupancyRate(occupancyRate);

        report.setTotalUsers((long) users.size());

        List<Double> monthlyRevenue = new ArrayList<>();
        List<Integer> monthlyBookings = new ArrayList<>();
        List<Integer> monthlyUsers = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 11; i >= 0; i--) {
            YearMonth month = YearMonth.from(today.minusMonths(i));
            LocalDate monthStart = month.atDay(1);
            LocalDate monthEnd = month.atEndOfMonth();

            // Monthly revenue
            double revenue = bookingRepository.findBookingsByHotelIdAndDateRange(hotelId, monthStart, monthEnd)
                    .stream()
                    .filter(b -> "PAID".equals(b.getPaymentStatus()))
                    .mapToDouble(Booking::getTotalPrice)
                    .sum();
            monthlyRevenue.add(revenue);

            // Monthly bookings
            int bookingCount = bookingRepository.findBookingsByHotelIdAndDateRange(hotelId, monthStart, monthEnd)
                    .size();
            monthlyBookings.add(bookingCount);

            // Monthly users
            Set<String> usersInMonth = bookingRepository.findBookingsByHotelIdAndDateRange(hotelId, monthStart, monthEnd)
                    .stream()
                    .filter(booking -> booking.getUser() != null)
                    .map(booking -> booking.getUser().getEmail())
                    .filter(email -> email != null)
                    .collect(Collectors.toSet());
            monthlyUsers.add(usersInMonth.size());
        }
        report.setMonthlyRevenue(monthlyRevenue);
        report.setMonthlyBookings(monthlyBookings);
        report.setMonthlyUsers(monthlyUsers);

        // Log the report data for debugging
        LOGGER.info("Report generated for hotelId: " + (hotelId != null ? hotelId : "All Hotels") +
                ", monthlyRevenue: " + monthlyRevenue +
                ", monthlyBookings: " + monthlyBookings +
                ", monthlyUsers: " + monthlyUsers);

        return report;
    }

    public List<ReportDTO> getAllHotelSummaries(LocalDate startDate, LocalDate endDate) {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .map(hotel -> getReport(hotel.getId(), startDate, endDate))
                .collect(Collectors.toList());
    }

    public HotelDetailsDTO getHotelDetails(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        HotelDetailsDTO details = new HotelDetailsDTO();
        details.setHotel(toHotelDTO(hotel));

        List<UserDTO> staff = userRepository.findByHotelId(hotelId).stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
        details.setStaff(staff);

        List<RoomDTO> rooms = roomRepository.findByHotelId(hotelId).stream()
                .map(this::toRoomDTO)
                .collect(Collectors.toList());
        details.setRooms(rooms);

        LocalDate today = LocalDate.now();
        List<BookingDTO> bookings = bookingRepository.findBookingsByHotelIdAndDateRange(
                        hotelId, today.minusYears(1), today.plusYears(1)).stream()
                .map(this::toBookingDTO)
                .collect(Collectors.toList());
        details.setRecentBookings(bookings);

        List<FeedbackDTO> feedback = feedbackRepository.findByHotelId(hotelId).stream()
                .map(this::toFeedbackDTO)
                .collect(Collectors.toList());
        details.setFeedback(feedback);

        return details;
    }

    private HotelDTO toHotelDTO(Hotel hotel) {
        HotelDTO dto = new HotelDTO();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setCity(hotel.getCity());
        dto.setAddress(hotel.getAddress());
        dto.setManagerEmail(hotel.getManager() != null ? hotel.getManager().getEmail() : null);
        dto.setNumberOfFloors(hotel.getNumberOfFloors());
        return dto;
    }

    private UserDTO toUserDTO(User user) {
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

    private BookingDTO toBookingDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setHotelId(booking.getHotel().getId());
        dto.setRoomIds(List.of(booking.getRoom().getId()));
        dto.setUserEmail(booking.getUser().getEmail());
        dto.setUserName(booking.getUser().getName());
        dto.setRoomNumber(booking.getRoom().getRoomNumber());
        dto.setRoomType(booking.getRoom().getType());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        dto.setPaymentStatus(booking.getPaymentStatus());
        BookingDTO.PaymentDetailsDTO paymentDetails = new BookingDTO.PaymentDetailsDTO();
        paymentDetails.setPaymentIntentId(booking.getPaymentDetails());
        paymentDetails.setTotalPrice(booking.getTotalPrice());
        dto.setPaymentDetails(paymentDetails);
        return dto;
    }

    private FeedbackDTO toFeedbackDTO(Feedback feedback) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(feedback.getId());
        dto.setHotelId(feedback.getHotel().getId());
        dto.setUserEmail(feedback.getUser().getEmail());
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }
}