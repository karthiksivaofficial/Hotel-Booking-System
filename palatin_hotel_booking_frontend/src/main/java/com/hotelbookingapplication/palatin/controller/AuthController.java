package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.AuthResponseDTO;
import com.hotelbookingapplication.palatin.dto.LoginDTO;
import com.hotelbookingapplication.palatin.dto.RegisterDTO;
import com.hotelbookingapplication.palatin.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginDTO") LoginDTO loginDTO, BindingResult result, HttpServletResponse response, Model model) {
        if (result.hasErrors()) {
            return "auth/login";
        }
        try {
            AuthResponseDTO authResponse = authService.login(loginDTO);
            Cookie cookie = new Cookie("jwt", authResponse.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            switch (authResponse.getRole()) {
                case "USER":
                    return "redirect:/user/home";
                case "RECEPTIONIST":
                    return "redirect:/receptionist/dashboard";
                case "MANAGER":
                    return "redirect:/manager/dashboard";
                case "SUPER_ADMIN":
                    return "redirect:/admin/dashboard";
                case "LAUNDRY":
                    return "redirect:/laundry/dashboard";
                case "CAB":
                    return "redirect:/cab/dashboard";
                default:
                    return "redirect:/login?error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Invalid credentials");
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registerDTO") RegisterDTO registerDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            authService.register(registerDTO);
            return "redirect:/login?success";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/login";
    }
}