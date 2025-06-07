package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProfileService {
    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8086/api/users";

    public UserDTO getCurrentUser(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(BASE_URL + "/me", HttpMethod.GET, entity, UserDTO.class).getBody();
    }

    public UserDTO updateUser(UserDTO userDTO, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<UserDTO> entity = new HttpEntity<>(userDTO, headers);
        return restTemplate.exchange(BASE_URL, HttpMethod.PUT, entity, UserDTO.class).getBody();
    }
}