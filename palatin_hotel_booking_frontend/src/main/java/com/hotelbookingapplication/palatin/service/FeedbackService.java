package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.FeedbackDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8086/api/feedback";

    public List<FeedbackDTO> getFeedbackByHotel(Long hotelId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        FeedbackDTO[] feedback = restTemplate.exchange(BASE_URL + "/hotel/" + hotelId, HttpMethod.GET, entity, FeedbackDTO[].class).getBody();
        return Arrays.asList(feedback);
    }

    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<FeedbackDTO> entity = new HttpEntity<>(feedbackDTO, headers);
        try {
            return restTemplate.exchange(BASE_URL, HttpMethod.POST, entity, FeedbackDTO.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Failed to create feedback: " + e.getResponseBodyAsString(), e);
        }
    }

    public List<Long> getBookedHotelIds(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Long[] hotelIds = restTemplate.exchange(BASE_URL + "/user/hotels", HttpMethod.GET, entity, Long[].class).getBody();
        return Arrays.asList(hotelIds);
    }
}