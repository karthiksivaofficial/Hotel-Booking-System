package com.hotelbookingapplication.palatin.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReportDTO {
    private Long hotelId;
    private String hotelName; // Existing field
    private HotelDTO hotel; // New field to store hotel details
    private Double totalRevenue;
    private Long totalBookings;
    private Long totalUsers;
    private Double occupancyRate;
    private List<Double> monthlyRevenue;
    private List<Integer> monthlyBookings; // Added for bar graph
    private List<Integer> monthlyUsers;// Changed to List<Double> to match AdminService
}