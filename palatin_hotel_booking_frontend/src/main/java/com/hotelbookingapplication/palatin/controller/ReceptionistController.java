package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.BookingDTO;
import com.hotelbookingapplication.palatin.dto.RoomDTO;
import com.hotelbookingapplication.palatin.dto.ServiceRequestDTO;
import com.hotelbookingapplication.palatin.dto.UserDTO;
import com.hotelbookingapplication.palatin.service.BookingService;
import com.hotelbookingapplication.palatin.service.CabService;
import com.hotelbookingapplication.palatin.service.ReceptionistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/receptionist")
public class ReceptionistController {

    @Autowired
    private ReceptionistService receptionistService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CabService cabService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, "roomIds", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                List<Long> roomIds = Arrays.stream(text.split(","))
                        .map(String::trim)
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                setValue(roomIds);
            }
        });

        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                setValue(LocalDate.parse(text, formatter));
            }
        });
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "receptionist/dashboard";
    }

    @GetMapping("/bookings/today")
    public String todayBookings(Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        model.addAttribute("bookings", receptionistService.getTodayBookings(token));
        return "receptionist/today_bookings";
    }

    @GetMapping("/bookings/date-range")
    public String bookingsByDateRange(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            model.addAttribute("bookings", receptionistService.getBookingsByDateRange(token, start, end));
        } else {
            model.addAttribute("bookings", receptionistService.getTodayBookings(token));
        }
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "receptionist/bookings_date_range";
    }

    @PostMapping("/check-in/{id}")
    public String checkIn(@PathVariable Long id, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        receptionistService.checkIn(id, token);
        return "redirect:/receptionist/bookings/today?success";
    }

    @PostMapping("/check-out/{id}")
    public String checkOut(@PathVariable Long id, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        receptionistService.checkOut(id, token);
        return "redirect:/receptionist/bookings/today?success";
    }

    @GetMapping("/offline-booking")
    public String offlineBookingForm(Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }
        Map<String, Long> roomStats = receptionistService.getRoomStats(token);
        List<RoomDTO> availableRooms = receptionistService.getAvailableRooms(token);
        UserDTO currentUser = receptionistService.getCurrentUser(token);
        model.addAttribute("bookingDTO", new BookingDTO());
        model.addAttribute("roomStats", roomStats);
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("hotelId", currentUser.getHotelId());
        return "receptionist/offline_booking";
    }

    @PostMapping("/offline-booking/initiate")
    public String initiateOfflineBooking(@ModelAttribute BookingDTO bookingDTO, Model model, HttpServletRequest request, HttpSession session) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }
        if (bookingDTO.getRoomIds() == null || bookingDTO.getRoomIds().isEmpty()) {
            model.addAttribute("error", "At least one room must be selected");
            return prepareBookingForm(model, token);
        }
        try {
            session.setAttribute("bookingDTO", bookingDTO);
            String approvalUrl = receptionistService.initiateOfflineBooking(bookingDTO, token);
            return "redirect:" + approvalUrl;
        } catch (Exception e) {
            model.addAttribute("error", "Failed to initiate payment: " + e.getMessage());
            return prepareBookingForm(model, token);
        }
    }

    @GetMapping("/offline-booking/success")
    public String offlineBookingSuccess(@RequestParam String paymentId, @RequestParam("PayerID") String payerId,
                                        Model model, HttpServletRequest request, HttpSession session) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }
        BookingDTO bookingDTO = (BookingDTO) session.getAttribute("bookingDTO");
        if (bookingDTO == null || bookingDTO.getRoomIds() == null || bookingDTO.getRoomIds().isEmpty()) {
            model.addAttribute("error", "Booking data is missing or invalid");
            return "error";
        }
        if (paymentId == null || payerId == null) {
            model.addAttribute("error", "Missing payment parameters");
            return "error";
        }
        try {
            List<BookingDTO> bookings = receptionistService.executeOfflineBooking(bookingDTO, paymentId, payerId, token);
            if (bookings == null || bookings.isEmpty()) {
                model.addAttribute("error", "Failed to create booking");
                return "error";
            }
            String hotelName = "Palatin Hotel"; // Replace with actual HotelService call if available
            model.addAttribute("bookings", bookings);
            model.addAttribute("bookingIds", bookings.stream().map(BookingDTO::getId).collect(Collectors.toList()));
            model.addAttribute("hotelName", hotelName);
            model.addAttribute("guestName", bookingDTO.getUserName());
            model.addAttribute("guestEmail", bookingDTO.getUserEmail());
            model.addAttribute("roomNumbers", bookings.stream().map(BookingDTO::getRoomNumber).collect(Collectors.joining(", ")));
            model.addAttribute("roomTypes", bookings.stream().map(BookingDTO::getRoomType).collect(Collectors.joining(", ")));
            model.addAttribute("checkIn", bookingDTO.getCheckInDate());
            model.addAttribute("checkOut", bookingDTO.getCheckOutDate());
            model.addAttribute("totalPrice", bookingDTO.getTotalPrice());
            model.addAttribute("paymentStatus", bookings.get(0).getPaymentStatus());
            session.removeAttribute("bookingDTO");
            return "receptionist/offline_booking_success";
        } catch (Exception e) {
            model.addAttribute("error", "Booking failed: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/offline-booking/cancel")
    public String offlineBookingCancel(Model model, HttpSession session) {
        session.removeAttribute("bookingDTO");
        model.addAttribute("error", "Payment was cancelled.");
        return "receptionist/offline_booking_cancel";
    }

    @GetMapping("/room-status")
    public String roomStatus(Model model, @RequestParam(required = false) String roomNumber, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (roomNumber != null && !roomNumber.trim().isEmpty()) {
            RoomDTO room = receptionistService.getRoomByRoomNumber(roomNumber, token);
            BookingDTO booking = receptionistService.getBookingByRoomNumber(roomNumber, token);
            model.addAttribute("room", room);
            model.addAttribute("booking", booking);
        }
        return "receptionist/room_status";
    }

    @GetMapping("/service-requests")
    public String serviceRequests(Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        List<ServiceRequestDTO> requests = receptionistService.getServiceRequests(token);
        model.addAttribute("requests", requests);
        model.addAttribute("laundryStaff", receptionistService.getStaffByRole(token, "LAUNDRY"));
        model.addAttribute("luggageStaff", receptionistService.getStaffByRole(token, "LUGGAGE"));
        return "receptionist/service_requests";
    }

    @PostMapping("/service-requests/assign/{id}")
    public String assignStaff(@PathVariable Long id, @RequestParam String staffEmail, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        receptionistService.assignStaff(id, staffEmail, token);
        return "redirect:/receptionist/service-requests?success";
    }

    @GetMapping("/cab-requests")
    public String cabRequests(Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        model.addAttribute("cabBookings", receptionistService.getCabRequests(token));
        model.addAttribute("cabDrivers", receptionistService.getStaffByRole(token, "CAB"));
        return "receptionist/cab_requests";
    }

    @PostMapping("/cab-requests/assign/{id}")
    public String assignDriver(@PathVariable Long id, @RequestParam String driverEmail, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        receptionistService.assignDriver(id, driverEmail, token);
        return "redirect:/receptionist/cab-requests?success";
    }

    @GetMapping("/bill/{id}")
    public ResponseEntity<ByteArrayResource> downloadBill(@PathVariable Long id, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return ResponseEntity.status(401).build();
        }
        ByteArrayResource resource = bookingService.generateBill(id, token);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    private String prepareBookingForm(Model model, String token) {
        Map<String, Long> roomStats = receptionistService.getRoomStats(token);
        List<RoomDTO> availableRooms = receptionistService.getAvailableRooms(token);
        UserDTO currentUser = receptionistService.getCurrentUser(token);
        model.addAttribute("bookingDTO", new BookingDTO());
        model.addAttribute("roomStats", roomStats);
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("hotelId", currentUser.getHotelId());
        return "receptionist/offline_booking";
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}