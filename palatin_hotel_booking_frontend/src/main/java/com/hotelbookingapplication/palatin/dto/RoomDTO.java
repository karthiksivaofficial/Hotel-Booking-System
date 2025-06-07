package com.hotelbookingapplication.palatin.dto;

import lombok.Data;

@Data
public class RoomDTO {
    private Long id;
    private Long hotelId;
    private String roomNumber;
    private String type;
    private double price;
    private String view;
    private String amenities;
    private String status;
    private Integer floor;
}