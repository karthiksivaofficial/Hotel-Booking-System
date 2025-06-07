package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.CabBookingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class CabDepartmentService {
    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8086/api/cabs";

    public List<CabBookingDTO> getAssignedCabBookings(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        CabBookingDTO[] bookings = restTemplate.exchange(BASE_URL + "/driver", HttpMethod.GET, entity, CabBookingDTO[].class).getBody();
        return Arrays.asList(bookings);
    }
}