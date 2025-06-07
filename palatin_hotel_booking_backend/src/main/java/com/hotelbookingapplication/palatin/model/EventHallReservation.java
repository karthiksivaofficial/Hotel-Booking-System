package com.hotelbookingapplication.palatin.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "event_hall_reservations")
@Data
public class EventHallReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String occasionType;
    private int guests;
    private LocalDate eventDate;
    private String addOns;
    private String status;
}