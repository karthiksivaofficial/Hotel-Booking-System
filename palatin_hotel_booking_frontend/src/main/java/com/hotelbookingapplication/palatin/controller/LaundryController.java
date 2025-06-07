package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.service.LaundryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/laundry")
public class LaundryController {
    @Autowired
    private LaundryService laundryService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }
        model.addAttribute("requests", laundryService.getAllLaundryRequests(token));
        return "laundry/dashboard";
    }

    @PostMapping("/complete/{id}")
    public String completeRequest(@PathVariable Long id, HttpServletRequest request, Model model) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }
        try {
            laundryService.completeRequest(id, token);
            return "redirect:/laundry/dashboard?success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("requests", laundryService.getAllLaundryRequests(token));
            return "laundry/dashboard";
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