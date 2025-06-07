package com.hotelbookingapplication.palatin.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "rooms")
@Data
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    private String roomNumber;
    private String type;
    private double price;
    private String view;
    private String amenities;
    private String status;
    private Integer floor;
}