package com.hotelbookingapplication.palatin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackDTO {
    private Long id;
    private Long hotelId;
    private String userEmail;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}