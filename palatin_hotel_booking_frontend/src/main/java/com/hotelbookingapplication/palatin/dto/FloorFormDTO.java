package com.hotelbookingapplication.palatin.dto;

import lombok.Data;

import java.util.List;

@Data
public class FloorFormDTO {
    private Long hotelId;
    private Integer floorNumber;
    private Integer numberOfRooms;
    private List<RoomDTO> rooms;
}