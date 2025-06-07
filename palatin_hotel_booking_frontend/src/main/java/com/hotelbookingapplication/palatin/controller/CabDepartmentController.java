package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.service.CabDepartmentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cab")
public class CabDepartmentController {
    @Autowired
    private CabDepartmentService cabDepartmentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        model.addAttribute("cabBookings", cabDepartmentService.getAssignedCabBookings(token));
        return "cab/dashboard";
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