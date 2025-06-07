package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.BookingDTO;
import com.hotelbookingapplication.palatin.dto.HotelDTO;
import com.hotelbookingapplication.palatin.dto.UserDTO;
import com.hotelbookingapplication.palatin.service.BookingService;
import com.hotelbookingapplication.palatin.service.HotelService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private RestTemplate restTemplate;

    private static final String USER_API_URL = "http://localhost:8086/api/users/me";

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

    @GetMapping
    public String myBookings(Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }
        List<BookingDTO> bookings = bookingService.getBookingsByUser(token);
        model.addAttribute("bookings", bookings);
        return "user/my_bookings";
    }

    @GetMapping("/form")
    public String bookingForm(@RequestParam Long hotelId, @RequestParam List<Long> roomIds,
                              @RequestParam String checkIn, @RequestParam String checkOut,
                              @RequestParam double totalPrice, Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }
        if (totalPrice <= 0) {
            model.addAttribute("error", "Invalid total price");
            return "error";
        }
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setHotelId(hotelId);
        bookingDTO.setRoomIds(roomIds);
        bookingDTO.setCheckInDate(LocalDate.parse(checkIn));
        bookingDTO.setCheckOutDate(LocalDate.parse(checkOut));
        bookingDTO.setTotalPrice(totalPrice);

        BookingDTO.PaymentDetailsDTO paymentDetails = new BookingDTO.PaymentDetailsDTO();
        paymentDetails.setTotalPrice(totalPrice);
        bookingDTO.setPaymentDetails(paymentDetails);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<UserDTO> response = restTemplate.exchange(
                    USER_API_URL, HttpMethod.GET, entity, UserDTO.class
            );
            UserDTO user = response.getBody();
            if (user != null && user.getEmail() != null) {
                bookingDTO.setUserEmail(user.getEmail());
                bookingDTO.setUserName(user.getName() != null ? user.getName() : "Guest");
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            return "redirect:/login";
        }

        model.addAttribute("bookingDTO", bookingDTO);
        return "user/booking_form";
    }

    @PostMapping("/initiate")
    public String initiateBooking(@ModelAttribute BookingDTO bookingDTO, Model model, HttpServletRequest request, HttpSession session) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }
        if (bookingDTO.getRoomIds() == null || bookingDTO.getRoomIds().isEmpty()) {
            model.addAttribute("error", "At least one room must be selected");
            model.addAttribute("bookingDTO", bookingDTO);
            return "user/booking_form";
        }
        try {
            session.setAttribute("bookingDTO", bookingDTO);
            String approvalUrl = bookingService.initiateBooking(bookingDTO, token);
            return "redirect:" + approvalUrl;
        } catch (Exception e) {
            model.addAttribute("error", "Failed to initiate payment: " + e.getMessage());
            model.addAttribute("bookingDTO", bookingDTO);
            return "user/booking_form";
        }
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam String paymentId, @RequestParam("PayerID") String payerId,
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
            List<BookingDTO> bookings = bookingService.executeBooking(bookingDTO, paymentId, payerId, token);
            if (bookings == null || bookings.isEmpty()) {
                model.addAttribute("error", "Failed to create booking");
                return "error";
            }
            HotelDTO hotel = hotelService.getHotelById(bookingDTO.getHotelId(), token);
            if (hotel == null) {
                model.addAttribute("error", "Hotel not found");
                return "error";
            }
            model.addAttribute("bookings", bookings);
            model.addAttribute("bookingIds", bookings.stream().map(BookingDTO::getId).collect(Collectors.toList()));
            model.addAttribute("hotelName", hotel.getName());
            model.addAttribute("guestName", bookingDTO.getUserName());
            model.addAttribute("guestEmail", bookingDTO.getUserEmail());
            model.addAttribute("roomNumbers", bookings.stream().map(BookingDTO::getRoomNumber).collect(Collectors.joining(", ")));
            model.addAttribute("roomTypes", bookings.stream().map(BookingDTO::getRoomType).collect(Collectors.joining(", ")));
            model.addAttribute("checkIn", bookingDTO.getCheckInDate());
            model.addAttribute("checkOut", bookingDTO.getCheckOutDate());
            model.addAttribute("totalPrice", bookingDTO.getTotalPrice());
            model.addAttribute("paymentStatus", bookings.get(0).getPaymentStatus());
            session.removeAttribute("bookingDTO");
            return "user/booking_success";
        } catch (Exception e) {
            model.addAttribute("error", "Booking failed: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/cancel")
    public String paymentCancel(Model model, HttpSession session) {
        session.removeAttribute("bookingDTO");
        model.addAttribute("error", "Payment was cancelled.");
        return "user/booking_cancel";
    }

    @PostMapping("/check-in/{id}")
    public String checkIn(@PathVariable Long id, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }
        bookingService.checkIn(id, token);
        return "redirect:/user/bookings?checkedIn";
    }

    @PostMapping("/check-out/{id}")
    public String checkOut(@PathVariable Long id, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }
        bookingService.checkOut(id, token);
        return "redirect:/user/bookings?checkedOut";
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }
        bookingService.cancelBooking(id, token);
        return "redirect:/user/bookings?cancelled";
    }

    @GetMapping("/{id}/bill")
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