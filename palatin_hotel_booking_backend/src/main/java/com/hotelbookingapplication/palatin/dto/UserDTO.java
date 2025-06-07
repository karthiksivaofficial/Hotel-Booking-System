package com.hotelbookingapplication.palatin.dto;

import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String role;
    private boolean isActive;
    private String profilePictureUrl;
    private Long hotelId; // Hotel assignment for staff

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}