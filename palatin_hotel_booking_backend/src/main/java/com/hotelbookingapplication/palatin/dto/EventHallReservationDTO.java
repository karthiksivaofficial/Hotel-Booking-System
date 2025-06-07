package com.hotelbookingapplication.palatin.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EventHallReservationDTO {
    private Long id;
    private Long hotelId;
    private String userEmail;
    private String occasionType;
    private int guests;
    private LocalDate eventDate;
    private String addOns;
    private String status;
}