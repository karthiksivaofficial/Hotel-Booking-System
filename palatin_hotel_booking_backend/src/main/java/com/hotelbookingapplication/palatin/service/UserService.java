package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.UserDTO;
import com.hotelbookingapplication.palatin.model.User;
import com.hotelbookingapplication.palatin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDTO updateUser(UserDTO userDTO, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update only name and profile picture URL
        user.setName(userDTO.getName());
        user.setProfilePictureUrl(userDTO.getProfilePictureUrl());

        // Save the updated user
        User updatedUser = userRepository.save(user);

        // Map to UserDTO
        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setId(updatedUser.getId());
        updatedUserDTO.setEmail(updatedUser.getEmail());
        updatedUserDTO.setName(updatedUser.getName());
        updatedUserDTO.setRole(updatedUser.getRole());
        updatedUserDTO.setActive(updatedUser.isActive());
        updatedUserDTO.setProfilePictureUrl(updatedUser.getProfilePictureUrl());
        updatedUserDTO.setHotelId(updatedUser.getHotelId());

        return updatedUserDTO;
    }
}