package com.hotelbookingapplication.palatin.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "room_status")
@Data
public class RoomStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private String status;
    private String notes;
}