package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.FeedbackDTO;
import com.hotelbookingapplication.palatin.service.FeedbackService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public String feedbackForm(Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }

        List<Long> bookedHotelIds = feedbackService.getBookedHotelIds(token);
        model.addAttribute("feedbackDTO", new FeedbackDTO());
        model.addAttribute("bookedHotelIds", bookedHotelIds);
        model.addAttribute("canSubmitFeedback", !bookedHotelIds.isEmpty());
        return "user/feedback";
    }

    @PostMapping
    public String createFeedback(@ModelAttribute FeedbackDTO feedbackDTO, HttpServletRequest request, Model model) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login";
        }

        try {
            feedbackService.createFeedback(feedbackDTO, token);
            return "redirect:/user/feedback?success";
        } catch (RuntimeException e) {
            List<Long> bookedHotelIds = feedbackService.getBookedHotelIds(token);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("feedbackDTO", feedbackDTO);
            model.addAttribute("bookedHotelIds", bookedHotelIds);
            model.addAttribute("canSubmitFeedback", !bookedHotelIds.isEmpty());
            return "user/feedback";
        }
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}