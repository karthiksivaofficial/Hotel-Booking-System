package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.ServiceRequestDTO;
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
public class ServiceRequestService {
    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8086/api/service-requests";

    public List<ServiceRequestDTO> getRequestsByUser(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ServiceRequestDTO[] requests = restTemplate.exchange(BASE_URL + "/user", HttpMethod.GET, entity, ServiceRequestDTO[].class).getBody();
        return Arrays.asList(requests);
    }

    public List<ServiceRequestDTO> getRequestsByHotel(Long hotelId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ServiceRequestDTO[] requests = restTemplate.exchange(BASE_URL + "/hotel/" + hotelId, HttpMethod.GET, entity, ServiceRequestDTO[].class).getBody();
        return Arrays.asList(requests);
    }

    public ServiceRequestDTO createRequest(ServiceRequestDTO requestDTO, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<ServiceRequestDTO> entity = new HttpEntity<>(requestDTO, headers);
        try {
            return restTemplate.exchange(BASE_URL, HttpMethod.POST, entity, ServiceRequestDTO.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Failed to create service request: " + e.getResponseBodyAsString(), e);
        }
    }

    public ServiceRequestDTO updateRequestStatus(Long id, String status, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(status, headers);
        return restTemplate.exchange(BASE_URL + "/status/" + id, HttpMethod.PUT, entity, ServiceRequestDTO.class).getBody();
    }

    public List<String> getCheckedInRoomNumbers(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String[] roomNumbers = restTemplate.exchange(BASE_URL + "/user/rooms", HttpMethod.GET, entity, String[].class).getBody();
        return Arrays.asList(roomNumbers);
    }
}