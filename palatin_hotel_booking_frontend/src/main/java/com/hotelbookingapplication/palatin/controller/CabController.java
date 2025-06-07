package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.CabBookingDTO;
import com.hotelbookingapplication.palatin.service.CabService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/cabs")
public class CabController {
    @Autowired
    private CabService cabService;

    @GetMapping
    public String cabBookings(Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        model.addAttribute("cabBookings", cabService.getCabBookingsByUser(token));
        return "user/cab_booking";
    }

    @GetMapping("/form")
    public String cabBookingForm(Model model) {
        model.addAttribute("cabBookingDTO", new CabBookingDTO());
        return "user/cab_booking";
    }

    @PostMapping
    public String createCabBooking(@ModelAttribute CabBookingDTO cabBookingDTO, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        cabService.createCabBooking(cabBookingDTO, token);
        return "redirect:/user/cabs?success";
    }

    @PostMapping("/cancel/{id}")
    public String cancelCabBooking(@PathVariable Long id, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        cabService.cancelCabBooking(id, token);
        return "redirect:/user/cabs?cancelled";
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
            if ("jwt".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}