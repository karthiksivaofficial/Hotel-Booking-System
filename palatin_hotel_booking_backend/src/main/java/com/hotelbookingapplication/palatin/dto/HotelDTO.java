package com.hotelbookingapplication.palatin.dto;

import lombok.Data;

import java.util.List;

@Data
public class HotelDTO {
    private Long id;
    private String name;
    private String city;
    private String address;
    private String managerEmail;
    private List<RoomDTO> rooms;
    private Integer numberOfFloors;
}