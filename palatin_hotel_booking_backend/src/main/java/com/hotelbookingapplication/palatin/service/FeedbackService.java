package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.FeedbackDTO;
import com.hotelbookingapplication.palatin.exception.ResourceNotFoundException;
import com.hotelbookingapplication.palatin.exception.UnauthorizedException;
import com.hotelbookingapplication.palatin.model.Booking;
import com.hotelbookingapplication.palatin.model.Feedback;
import com.hotelbookingapplication.palatin.model.Hotel;
import com.hotelbookingapplication.palatin.model.User;
import com.hotelbookingapplication.palatin.repository.BookingRepository;
import com.hotelbookingapplication.palatin.repository.FeedbackRepository;
import com.hotelbookingapplication.palatin.repository.HotelRepository;
import com.hotelbookingapplication.palatin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public List<FeedbackDTO> getFeedbackByHotel(Long hotelId) {
        try {
            return feedbackRepository.findByHotelId(hotelId).stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch feedback: " + e.getMessage());
        }
    }

    public List<FeedbackDTO> getAllFeedback() {
        try {
            return feedbackRepository.findAll().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch feedback: " + e.getMessage());
        }
    }

    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
        try {
            if (feedbackDTO.getUserEmail() == null) {
                throw new IllegalArgumentException("User email must not be null");
            }
            if (feedbackDTO.getHotelId() == null) {
                throw new IllegalArgumentException("Hotel ID must not be null");
            }
            if (feedbackDTO.getRating() < 1 || feedbackDTO.getRating() > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
            if (feedbackDTO.getComment() == null || feedbackDTO.getComment().isEmpty()) {
                throw new IllegalArgumentException("Comment must not be empty");
            }

            User user = userRepository.findByEmail(feedbackDTO.getUserEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + feedbackDTO.getUserEmail()));
            Hotel hotel = hotelRepository.findById(feedbackDTO.getHotelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + feedbackDTO.getHotelId()));

            List<Booking> bookings = bookingRepository.findByUserEmail(user.getEmail()).stream()
                    .filter(booking -> booking.getHotel().getId().equals(feedbackDTO.getHotelId()))
                    .filter(booking -> List.of("CONFIRMED", "CHECKED_IN", "CHECKED_OUT").contains(booking.getStatus()))
                    .collect(Collectors.toList());

            if (bookings.isEmpty()) {
                throw new UnauthorizedException("User has no bookings for hotel ID: " + feedbackDTO.getHotelId());
            }

            Feedback feedback = new Feedback();
            feedback.setHotel(hotel);
            feedback.setUser(user);
            feedback.setRating(feedbackDTO.getRating());
            feedback.setComment(feedbackDTO.getComment());
            feedback.setCreatedAt(LocalDateTime.now());
            return toDTO(feedbackRepository.save(feedback));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create feedback: " + e.getMessage(), e);
        }
    }

    public List<Long> getBookedHotelIds(String email) {
        return bookingRepository.findByUserEmail(email).stream()
                .filter(booking -> List.of("CONFIRMED", "CHECKED_IN", "CHECKED_OUT").contains(booking.getStatus()))
                .map(booking -> booking.getHotel().getId())
                .distinct()
                .collect(Collectors.toList());
    }

    private FeedbackDTO toDTO(Feedback feedback) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(feedback.getId());
        dto.setHotelId(feedback.getHotel().getId());
        dto.setUserEmail(feedback.getUser().getEmail());
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }
}