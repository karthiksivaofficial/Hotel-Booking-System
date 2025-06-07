package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.AuthResponseDTO;
import com.hotelbookingapplication.palatin.dto.LoginDTO;
import com.hotelbookingapplication.palatin.dto.RegisterDTO;
import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
import com.hotelbookingapplication.palatin.model.User;
import com.hotelbookingapplication.palatin.repository.UserRepository;
import com.hotelbookingapplication.palatin.config.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthResponseDTO login(LoginDTO loginDTO) {
        System.out.println("Login attempt with email: " + loginDTO.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);
            User user = userRepository.findByEmail(loginDTO.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            System.out.println("User found: " + user);
            AuthResponseDTO response = new AuthResponseDTO();
            response.setToken(token);
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setHotelId(user.getHotelId());
            System.out.println("Login successful: " + response);
            return response;
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            throw e;
        }
    }

    public void register(RegisterDTO registerDTO) {
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new ResourceNotFoundException("Email already exists");
        }
        User user = new User();
        user.setName(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole("USER");
        user.setActive(true);
        userRepository.save(user);
    }
}