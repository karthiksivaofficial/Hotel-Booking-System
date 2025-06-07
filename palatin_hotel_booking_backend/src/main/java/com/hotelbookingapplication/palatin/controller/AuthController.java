package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.config.JwtTokenProvider;
import com.hotelbookingapplication.palatin.dto.AuthResponseDTO;
import com.hotelbookingapplication.palatin.dto.LoginDTO;
import com.hotelbookingapplication.palatin.dto.RegisterDTO;
import com.hotelbookingapplication.palatin.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        System.out.println(loginDTO);
        AuthResponseDTO authResponse = authService.login(loginDTO);
        System.out.println(authResponse);
        Cookie cookie = new Cookie("jwt", authResponse.getToken());
        System.out.println(cookie);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/role")
    public ResponseEntity<String> getRole(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String role = jwtTokenProvider.getRoleFromToken(token);
            return ResponseEntity.ok(role);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
    }
}