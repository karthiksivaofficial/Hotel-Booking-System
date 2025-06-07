package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.AuthResponseDTO;
import com.hotelbookingapplication.palatin.dto.LoginDTO;
import com.hotelbookingapplication.palatin.dto.RegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {
    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8086/api/auth";

    public AuthResponseDTO login(LoginDTO loginDTO) {
        return restTemplate.postForObject(BASE_URL + "/login", loginDTO, AuthResponseDTO.class);
    }

    public String register(RegisterDTO registerDTO) {
        return restTemplate.postForObject(BASE_URL + "/register", registerDTO, String.class);
    }

    public String getRole(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(BASE_URL + "/role", HttpMethod.GET, entity, String.class).getBody();
    }
}