package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.HotelDTO;
import com.hotelbookingapplication.palatin.service.HotelService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HotelController {
    @Autowired
    private HotelService hotelService;

    @GetMapping({"/", "/guest/home"})
    public String guestHome(Model model, @RequestParam(required = false) String city) {
        List<HotelDTO> hotels = city != null ? hotelService.getHotelsByCity(city) : hotelService.getAllHotels();
        model.addAttribute("hotels", hotels);
        model.addAttribute("city", city);
        return "guest/home";
    }

    @GetMapping("/guest/hotels/{id}")
    public String guestHotelDetails(@PathVariable Long id, Model model) {
        HotelDTO hotel = hotelService.getHotelById(id, null);
        model.addAttribute("hotel", hotel);
        return "guest/hotel_details";
    }

    @GetMapping("/user/home")
    public String userHome(Model model, @RequestParam(required = false) String city, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        List<HotelDTO> hotels = city != null ? hotelService.getHotelsByCity(city) : hotelService.getAllHotels();
        model.addAttribute("hotels", hotels);
        model.addAttribute("city", city);
        return "user/home";
    }

    @GetMapping("/user/hotels/{id}")
    public String userHotelDetails(@PathVariable Long id, @RequestParam(required = false) String type,
                                   @RequestParam(required = false) String view, @RequestParam(required = false) Double minPrice,
                                   @RequestParam(required = false) Double maxPrice, @RequestParam(required = false) Integer floor,
                                   @RequestParam(required = false) String checkIn, @RequestParam(required = false) String checkOut,
                                   Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            System.out.println("No JWT token found in cookies, redirecting to login");
            return "redirect:/login";
        }
        HotelDTO hotel = (type == null && view == null && minPrice == null && maxPrice == null && floor == null &&
                checkIn == null && checkOut == null)
                ? hotelService.getHotelById(id, token)
                : hotelService.getHotelByIdWithFilters(id, token, type, view, minPrice, maxPrice, floor, checkIn, checkOut);
        model.addAttribute("hotel", hotel);
        model.addAttribute("type", type);
        model.addAttribute("view", view);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("floor", floor);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        return "user/hotel_details";
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    System.out.println("JWT token found: " + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        System.out.println("No JWT token found in cookies");
        return null;
    }
}