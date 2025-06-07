package com.hotelbookingapplication.palatin.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BookingDTO {
    private Long id;
    private Long hotelId;
    private List<Long> roomIds;
    private String userEmail;
    private String userName;
    private String roomNumber;
    private String roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalPrice;
    private String status;
    private String paymentStatus;
    private PaymentDetailsDTO paymentDetails;

    @Data
    public static class PaymentDetailsDTO {
        private String paymentIntentId; // Changed from paypalTransactionId
        private double totalPrice;
    }
}