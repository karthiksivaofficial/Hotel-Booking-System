package com.hotelbookingapplication.palatin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RestaurantReservationDTO {
    private Long id;
    private Long hotelId;
    private String userEmail;
    private LocalDateTime reservationTime;
    private int seats;
    private String specialRequests;
    private String status;
}