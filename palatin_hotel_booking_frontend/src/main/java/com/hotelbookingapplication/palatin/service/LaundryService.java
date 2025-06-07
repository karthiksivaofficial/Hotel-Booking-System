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
public class LaundryService {
    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8086/api/laundry";

    public List<ServiceRequestDTO> getLaundryRequests(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ServiceRequestDTO[] requests = restTemplate.exchange(BASE_URL + "/requests/assigned", HttpMethod.GET, entity, ServiceRequestDTO[].class).getBody();
        return Arrays.asList(requests);
    }

    public List<ServiceRequestDTO> getAllLaundryRequests(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ServiceRequestDTO[] requests = restTemplate.exchange(BASE_URL + "/requests/all", HttpMethod.GET, entity, ServiceRequestDTO[].class).getBody();
            return Arrays.asList(requests);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Failed to fetch laundry requests: " + e.getResponseBodyAsString(), e);
        }
    }

    public ServiceRequestDTO completeRequest(Long id, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            return restTemplate.exchange(BASE_URL + "/requests/complete/" + id, HttpMethod.POST, entity, ServiceRequestDTO.class).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Failed to complete request: " + e.getResponseBodyAsString(), e);
        }
    }
}