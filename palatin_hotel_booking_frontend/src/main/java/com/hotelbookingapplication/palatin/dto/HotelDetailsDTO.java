package com.hotelbookingapplication.palatin.dto;

import lombok.Data;

import java.util.List;

@Data
public class HotelDetailsDTO {
    private HotelDTO hotel;
    private List<UserDTO> staff;
    private List<RoomDTO> rooms;
    private List<BookingDTO> recentBookings;
}