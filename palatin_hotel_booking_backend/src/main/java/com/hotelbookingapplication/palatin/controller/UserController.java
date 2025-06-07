package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.UserDTO;
import com.hotelbookingapplication.palatin.model.User;
import com.hotelbookingapplication.palatin.repository.UserRepository;
import com.hotelbookingapplication.palatin.service.UserService;
import com.hotelbookingapplication.palatin.config.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setRole(user.getRole());
        userDTO.setActive(user.isActive());
        userDTO.setProfilePictureUrl(user.getProfilePictureUrl());
        userDTO.setHotelId(user.getHotelId());
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsersByHotelId(@RequestParam Long hotelId) {
        List<User> users = userRepository.findByHotelId(hotelId);
        List<UserDTO> userDTOs = users.stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setName(user.getName());
            dto.setRole(user.getRole());
            dto.setActive(user.isActive());
            dto.setProfilePictureUrl(user.getProfilePictureUrl());
            dto.setHotelId(user.getHotelId());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO updatedUserDTO = userService.updateUser(userDTO, username);
        return ResponseEntity.ok(updatedUserDTO);
    }
}