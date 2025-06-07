package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.BookingDTO;
import com.hotelbookingapplication.palatin.dto.ServiceRequestDTO;
import com.hotelbookingapplication.palatin.dto.UserDTO;
import com.hotelbookingapplication.palatin.service.BookingService;
import com.hotelbookingapplication.palatin.service.ServiceRequestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequestMapping("/user/service-requests")
public class ServiceRequestController {
    @Autowired
    private ServiceRequestService serviceRequestService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RestTemplate restTemplate;

    private static final String USER_API_URL = "http://localhost:8086/api/users/me";

    @GetMapping
    public String serviceRequests(Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }

        // Fetch user details
        UserDTO user = getCurrentUser(token);
        if (user == null) {
            return "redirect:/login";
        }

        // Fetch checked-in room numbers and corresponding bookings
        List<String> roomNumbers = serviceRequestService.getCheckedInRoomNumbers(token);
        List<BookingDTO> checkedInBookings = bookingService.getBookingsByUser(token).stream()
                .filter(booking -> "CHECKED_IN".equals(booking.getStatus()))
                .toList();

        model.addAttribute("requests", serviceRequestService.getRequestsByUser(token));
        model.addAttribute("requestDTO", new ServiceRequestDTO());
        model.addAttribute("roomNumbers", roomNumbers);
        model.addAttribute("bookings", checkedInBookings);
        model.addAttribute("canRequestService", !roomNumbers.isEmpty());
        return "user/service_request";
    }

    @PostMapping
    public String createRequest(@ModelAttribute ServiceRequestDTO requestDTO, HttpServletRequest request, Model model) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }

        try {
            serviceRequestService.createRequest(requestDTO, token);
            return "redirect:/user/service-requests?success";
        } catch (RuntimeException e) {
            // Fetch data again to repopulate the form
            UserDTO user = getCurrentUser(token);
            List<String> roomNumbers = serviceRequestService.getCheckedInRoomNumbers(token);
            List<BookingDTO> checkedInBookings = bookingService.getBookingsByUser(token).stream()
                    .filter(booking -> "CHECKED_IN".equals(booking.getStatus()))
                    .toList();

            model.addAttribute("error", e.getMessage());
            model.addAttribute("requests", serviceRequestService.getRequestsByUser(token));
            model.addAttribute("requestDTO", requestDTO);
            model.addAttribute("roomNumbers", roomNumbers);
            model.addAttribute("bookings", checkedInBookings);
            model.addAttribute("canRequestService", !roomNumbers.isEmpty());
            return "user/service_request";
        }
    }

    private UserDTO getCurrentUser(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            return restTemplate.exchange(USER_API_URL, HttpMethod.GET, entity, UserDTO.class).getBody();
        } catch (Exception e) {
            return null;
        }
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