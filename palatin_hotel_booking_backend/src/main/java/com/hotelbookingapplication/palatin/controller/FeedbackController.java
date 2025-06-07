package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.FeedbackDTO;
import com.hotelbookingapplication.palatin.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbackByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(feedbackService.getFeedbackByHotel(hotelId));
    }

    @PostMapping
    public ResponseEntity<FeedbackDTO> createFeedback(
            @RequestBody FeedbackDTO feedbackDTO,
            @AuthenticationPrincipal String email) {
        feedbackDTO.setUserEmail(email);
        return ResponseEntity.ok(feedbackService.createFeedback(feedbackDTO));
    }

    @GetMapping("/user/hotels")
    public ResponseEntity<List<Long>> getBookedHotelIds(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(feedbackService.getBookedHotelIds(email));
    }
}