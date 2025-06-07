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
public class CabService {
    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8086/api/cabs";

    public List<CabBookingDTO> getCabBookingsByUser(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        CabBookingDTO[] bookings = restTemplate.exchange(BASE_URL + "/user", HttpMethod.GET, entity, CabBookingDTO[].class).getBody();
        return Arrays.asList(bookings);
    }

    public List<CabBookingDTO> getAssignedCabBookings(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        CabBookingDTO[] bookings = restTemplate.exchange(BASE_URL + "/driver", HttpMethod.GET, entity, CabBookingDTO[].class).getBody();
        return Arrays.asList(bookings);
    }

    public CabBookingDTO createCabBooking(CabBookingDTO cabBookingDTO, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<CabBookingDTO> entity = new HttpEntity<>(cabBookingDTO, headers);
        return restTemplate.exchange(BASE_URL, HttpMethod.POST, entity, CabBookingDTO.class).getBody();
    }

    public CabBookingDTO cancelCabBooking(Long id, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(BASE_URL + "/cancel/" + id, HttpMethod.POST, entity, CabBookingDTO.class).getBody();
    }

    public CabBookingDTO assignDriver(Long id, String driverEmail, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(driverEmail, headers);
        return restTemplate.exchange(BASE_URL + "/assign/" + id, HttpMethod.POST, entity, CabBookingDTO.class).getBody();
    }
}