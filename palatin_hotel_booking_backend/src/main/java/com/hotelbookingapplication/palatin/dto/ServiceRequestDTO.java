package com.hotelbookingapplication.palatin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServiceRequestDTO {
    private Long id;
    private String userEmail;
    private Long hotelId;
    private String serviceType;
    private String notes;
    private LocalDateTime requestTime;
    private String status;
    private String roomNumber;
    private Long staffId;
    private String staffName; // Added for displaying assigned staff's name
}