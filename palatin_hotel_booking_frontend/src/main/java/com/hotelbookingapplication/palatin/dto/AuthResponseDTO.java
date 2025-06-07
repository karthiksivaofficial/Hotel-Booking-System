package com.hotelbookingapplication.palatin.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private String email;
    private String role;
    private Long hotelId;
}