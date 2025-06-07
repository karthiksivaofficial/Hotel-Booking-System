package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.UserDTO;
import com.hotelbookingapplication.palatin.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/profile")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @GetMapping
    public String profilePage(Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        UserDTO userDTO = profileService.getCurrentUser(token);
        model.addAttribute("userDTO", userDTO);
        return "user/profile";
    }

    @PostMapping
    public String updateProfile(@ModelAttribute UserDTO userDTO, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        profileService.updateUser(userDTO, token);
        return "redirect:/user/profile?success";
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