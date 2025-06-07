//package com.hotelbookingapplication.palatin.model;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "cab_bookings")
//@Data
//public class CabBooking {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    private String pickupLocation;
//    private String dropLocation;
//    private LocalDateTime pickupTime;
//    private String vehicleType;
//    private String status;
//    private String driverEmail;
//    private Double additionalCharges;
//}