package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.BookingDTO;
//import com.hotelbookingapplication.palatin.dto.CabBookingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendBookingConfirmation(String to, BookingDTO bookingDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Booking Confirmation - Palatin Hotels");
            String content = String.format(
                    "Dear %s,\n\nYour booking has been confirmed!\n\nDetails:\n" +
                            "Booking ID: %d\nHotel ID: %d\nRoom Number: %s\nCheck-In: %s\nCheck-Out: %s\nTotal Price: $%.2f\n\n" +
                            "Thank you for choosing Palatin Hotels!",
                    bookingDTO.getUserName(), bookingDTO.getId(), bookingDTO.getHotelId(),
                    bookingDTO.getRoomNumber(), bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate(),
                    bookingDTO.getTotalPrice());
            helper.setText(content);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send booking confirmation email", e);
        }
    }

    public void sendCancellationEmail(String to, BookingDTO bookingDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Booking Cancellation - Palatin Hotels");
            String content = String.format(
                    "Dear %s,\n\nYour booking has been cancelled.\n\nDetails:\n" +
                            "Booking ID: %d\nHotel ID: %d\nRoom Number: %s\n\n" +
                            "We hope to serve you again at Palatin Hotels!",
                    bookingDTO.getUserName(), bookingDTO.getId(), bookingDTO.getHotelId(),
                    bookingDTO.getRoomNumber());
            helper.setText(content);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send cancellation email", e);
        }
    }

    public void sendCheckoutEmail(String to, BookingDTO bookingDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Check-Out Confirmation - Palatin Hotels");
            String content = String.format(
                    "Dear %s,\n\nThank you for staying with us!\n\nCheck-Out Details:\n" +
                            "Booking ID: %d\nHotel ID: %d\nRoom Number: %s\nTotal Price: $%.2f\n\n" +
                            "We hope to see you again at Palatin Hotels!",
                    bookingDTO.getUserName(), bookingDTO.getId(), bookingDTO.getHotelId(),
                    bookingDTO.getRoomNumber(), bookingDTO.getTotalPrice());
            helper.setText(content);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send checkout email", e);
        }
    }

    public void sendGuestNotification(String to, BookingDTO bookingDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Notification - Palatin Hotels");
            String content = String.format(
                    "Dear %s,\n\nThis is a notification regarding your booking.\n\nDetails:\n" +
                            "Booking ID: %d\nHotel ID: %d\nRoom Number: %s\nStatus: %s\n\n" +
                            "Please contact us if you have any questions.",
                    bookingDTO.getUserName(), bookingDTO.getId(), bookingDTO.getHotelId(),
                    bookingDTO.getRoomNumber(), bookingDTO.getStatus());
            helper.setText(content);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send guest notification email", e);
        }
    }

//    public void sendCabBookingConfirmation(String to, CabBookingDTO cabBookingDTO) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setTo(to);
//            helper.setSubject("Cab Booking Confirmation - Palatin Hotels");
//            String content = String.format(
//                    "Dear Guest,\n\nYour cab booking has been confirmed!\n\nDetails:\n" +
//                            "Booking ID: %d\nPickup Location: %s\nDrop Location: %s\nPickup Time: %s\nVehicle Type: %s\n\n" +
//                            "Thank you for choosing Palatin Hotels!",
//                    cabBookingDTO.getId(), cabBookingDTO.getPickupLocation(), cabBookingDTO.getDropLocation(),
//                    cabBookingDTO.getPickupTime(), cabBookingDTO.getVehicleType());
//            helper.setText(content);
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            throw new RuntimeException("Failed to send cab booking confirmation email", e);
//        }
//    }
//
//    public void sendCabCancellationEmail(String to, CabBookingDTO cabBookingDTO) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setTo(to);
//            helper.setSubject("Cab Booking Cancellation - Palatin Hotels");
//            String content = String.format(
//                    "Dear Guest,\n\nYour cab booking has been cancelled.\n\nDetails:\n" +
//                            "Booking ID: %d\nPickup Location: %s\nDrop Location: %s\n\n" +
//                            "We hope to serve you again at Palatin Hotels!",
//                    cabBookingDTO.getId(), cabBookingDTO.getPickupLocation(), cabBookingDTO.getDropLocation());
//            helper.setText(content);
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            throw new RuntimeException("Failed to send cab cancellation email", e);
//        }
//    }
public void sendPaymentConfirmation(String to, BookingDTO booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Payment Confirmation - Palatin Hotels");
        message.setText(
                "Dear " + booking.getUserName() + ",\n\n" +
                        "Your payment has been successfully processed.\n" +
                        "Booking ID: " + booking.getId() + "\n" +
                        "Rooms: " + booking.getRoomNumber() + "\n" +
                        "Total Price: $" + booking.getTotalPrice() + "\n" +
                        "Transaction ID: " + booking.getPaymentDetails().getPaymentIntentId() + "\n\n" +
                        "Thank you for your payment!"
        );
        mailSender.send(message);
    }
}