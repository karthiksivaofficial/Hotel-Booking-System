package com.hotelbookingapplication.palatin.repository;

import com.hotelbookingapplication.palatin.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByHotelId(Long hotelId);
    List<Feedback> findByUserEmail(String email);
}