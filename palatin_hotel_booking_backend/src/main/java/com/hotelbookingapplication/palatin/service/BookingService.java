package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.BookingDTO;
import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
import com.hotelbookingapplication.palatin.exception.ValidationException;
import com.hotelbookingapplication.palatin.model.Booking;
import com.hotelbookingapplication.palatin.model.Hotel;
import com.hotelbookingapplication.palatin.model.Room;
import com.hotelbookingapplication.palatin.model.User;
import com.hotelbookingapplication.palatin.repository.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public List<BookingDTO> getBookingsByUser(String email) {
        return bookingRepository.findByUserEmail(email).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getBookingsByHotel(Long hotelId, LocalDate startDate, LocalDate endDate, String status) {
        List<Booking> bookings;
        if (hotelId != null && startDate != null && endDate != null) {
            bookings = bookingRepository.findBookingsByHotelIdAndDateRange(hotelId, startDate, endDate);
        } else if (hotelId != null) {
            bookings = bookingRepository.findByHotelId(hotelId);
        } else if (startDate != null && endDate != null) {
            bookings = bookingRepository.findByDateRange(startDate, endDate);
        } else {
            bookings = bookingRepository.findAll();
        }
        if (status != null && !status.isEmpty()) {
            bookings = bookings.stream()
                    .filter(b -> b.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        return bookings.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BookingDTO> createBooking(BookingDTO bookingDTO, String paymentId) {
        if (bookingDTO.getRoomIds() == null || bookingDTO.getRoomIds().isEmpty()) {
            throw new ValidationException("At least one room must be selected");
        }

        Hotel hotel = hotelRepository.findById(bookingDTO.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        Optional<User> userOptional = userRepository.findByEmail(bookingDTO.getUserEmail());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = new User();
            user.setEmail(bookingDTO.getUserEmail());
            user.setName(bookingDTO.getUserName());
            user.setRole("GUEST");
            user.setHotelId(bookingDTO.getHotelId());
            user = userRepository.save(user);
        }

        List<Booking> bookings = new ArrayList<>();
        double totalPrice = bookingDTO.getTotalPrice();
        List<String> roomNumbers = new ArrayList<>();
        List<String> roomTypes = new ArrayList<>();
        double calculatedTotalPrice = 0;

        List<Booking> roomIds = new ArrayList<>();
        double totalPricePerDay = new BookingDTO().getTotalPrice();
         // Frontend's total price
        long days = java.time.temporal.ChronoUnit.DAYS.between(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        if (days <= 0) {
            throw new ValidationException("Check-out must be after check-in");
        }
        List<Room> roomsToBook = new ArrayList<>();
        double calculatedPricePerDay = 0;
        for (Long roomId : bookingDTO.getRoomIds()) {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room " + roomId + " not found"));
            if (!"AVAILABLE".equals(room.getStatus())) {
                throw new ValidationException("Room " + room.getRoomNumber() + " is not available");
            }
            roomsToBook.add(room);
            calculatedTotalPrice += room.getPrice() * days; 
            roomNumbers.add(room.getRoomNumber());
            roomTypes.add(room.getType());
        }

        // Validate that the frontend's totalPrice matches the calculated total
        if (Math.abs(calculatedTotalPrice - totalPrice) > 0.01) {
            throw new ValidationException("Total price mismatch: expected " + calculatedTotalPrice + ", but received " + totalPrice);
        }

        for (Room room : roomsToBook) {
            Booking booking = new Booking();
            booking.setHotel(hotel);
            booking.setRoom(room);
            booking.setUser(user);
            booking.setCheckInDate(bookingDTO.getCheckInDate());
            booking.setCheckOutDate(bookingDTO.getCheckOutDate());
            booking.setStatus("CONFIRMED");
            booking.setPaymentStatus("PAID");
            booking.setTotalPrice(room.getPrice() * days); // Set price for duration
            booking.setPaymentDetails(paymentId);

            room.setStatus("OCCUPIED");
            roomRepository.save(room);

            bookings.add(bookingRepository.save(booking));
        }

        bookingDTO.setRoomNumber(String.join(", ", roomNumbers));
        bookingDTO.setRoomType(String.join(", ", roomTypes));
        bookingDTO.setTotalPrice(calculatedTotalPrice); // Ensure DTO reflects the sum for frontend/email

        for (Booking booking : bookings) {
            BookingDTO confirmedBookingDTO = toDTO(booking);
            emailService.sendBookingConfirmation(booking.getUser().getEmail(), confirmedBookingDTO);
        }

        return bookings.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public BookingDTO cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new ValidationException("Booking is already cancelled");
        }
        booking.setStatus("CANCELLED");
        Room room = booking.getRoom();
        room.setStatus("AVAILABLE");
        roomRepository.save(room);
        BookingDTO cancelledBookingDTO = toDTO(bookingRepository.save(booking));
        emailService.sendCancellationEmail(booking.getUser().getEmail(), cancelledBookingDTO);
        return cancelledBookingDTO;
    }

    @Transactional
    public BookingDTO checkIn(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new ValidationException("Booking is not in CONFIRMED status");
        }
        booking.setStatus("CHECKED_IN");
        return toDTO(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDTO checkOut(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!"CHECKED_IN".equals(booking.getStatus())) {
            throw new ValidationException("Booking is not in CHECKED_IN status");
        }
        booking.setStatus("CHECKED_OUT");
        Room room = booking.getRoom();
        room.setStatus("AVAILABLE");
        roomRepository.save(room);
        BookingDTO checkedOutBookingDTO = toDTO(bookingRepository.save(booking));
        emailService.sendCheckoutEmail(booking.getUser().getEmail(), checkedOutBookingDTO);
        return checkedOutBookingDTO;
    }

    @Transactional
    public BookingDTO markPaid(Long id, String paymentIntentId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if ("PAID".equals(booking.getPaymentStatus())) {
            throw new ValidationException("Booking is already paid");
        }
        booking.setPaymentStatus("PAID");
        booking.setPaymentDetails(paymentIntentId);
        return toDTO(bookingRepository.save(booking));
    }

    public BookingDTO getBookingByRoomNumber(String roomNumber) {
        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        Booking booking = bookingRepository.findActiveBookingByRoomId(room.getId(), LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("No active booking for room"));
        return toDTO(booking);
    }

    public BookingDTO getLatestBookingByRoomNumberAndHotelId(String roomNumber, Long hotelId) {
        Room room = roomRepository.findByHotelIdAndRoomNumber(hotelId, roomNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found for hotelId: " + hotelId + " and roomNumber: " + roomNumber));
        List<Booking> bookings = bookingRepository.findBookingsByRoomIdAndDateRange(room.getId(), LocalDate.now().minusYears(1), LocalDate.now().plusYears(1));
        Booking latestBooking = bookings.stream()
                .filter(b -> !b.getStatus().equals("CANCELLED") && !b.getStatus().equals("CHECKED_OUT"))
                .max((b1, b2) -> b1.getCheckInDate().compareTo(b2.getCheckInDate()))
                .orElse(null);
        return latestBooking != null ? toDTO(latestBooking) : null;
    }

    public byte[] generateBill(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Palatin Hotels - Bill"));
            document.add(new Paragraph("Hotel: " + booking.getHotel().getName()));
            document.add(new Paragraph("Booking ID: " + booking.getId()));
            document.add(new Paragraph("Guest: " + booking.getUser().getName()));
            document.add(new Paragraph("Room: " + booking.getRoom().getRoomNumber()));
            document.add(new Paragraph("Check-In: " + booking.getCheckInDate()));
            document.add(new Paragraph("Check-Out: " + booking.getCheckOutDate()));
            document.add(new Paragraph("Total Price: $" + booking.getTotalPrice()));
            document.add(new Paragraph("Payment Status: " + booking.getPaymentStatus()));
            if (booking.getPaymentDetails() != null) {
                document.add(new Paragraph("Transaction ID: " + booking.getPaymentDetails()));
            }
            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF bill: " + e.getMessage(), e);
        }
    }

    public BookingDTO toDTO(Booking booking) {
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
}