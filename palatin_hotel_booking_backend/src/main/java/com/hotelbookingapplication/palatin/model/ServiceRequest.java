package com.hotelbookingapplication.palatin.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_requests")
@Data
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @Column(name = "service_type")
    private String serviceType;

    private String notes;

    @Column(name = "request_time")
    private LocalDateTime requestTime;
    @Column(name = "room_number")
    private String roomNumber;
    private String status;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;
}