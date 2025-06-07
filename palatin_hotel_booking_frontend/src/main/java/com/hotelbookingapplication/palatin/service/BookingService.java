package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.BookingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8086/api/bookings";

    public List<BookingDTO> getBookingsByUser(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            BookingDTO[] bookings = restTemplate.exchange(BASE_URL + "/user", HttpMethod.GET, entity, BookingDTO[].class).getBody();
            return Arrays.asList(bookings);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch user bookings: " + e.getMessage(), e);
        }
    }

    public List<BookingDTO> getBookingsByHotel(Long hotelId, LocalDate startDate, LocalDate endDate, String token) {
        String url = BASE_URL + "/hotel/" + hotelId;
        if (startDate != null && endDate != null) {
            url += "?startDate=" + startDate + "&endDate=" + endDate;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            BookingDTO[] bookings = restTemplate.exchange(url, HttpMethod.GET, entity, BookingDTO[].class).getBody();
            return Arrays.asList(bookings);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch hotel bookings: " + e.getMessage(), e);
        }
    }

    public String initiateBooking(BookingDTO bookingDTO, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<BookingDTO> entity = new HttpEntity<>(bookingDTO, headers);
        try {
            return restTemplate.exchange(BASE_URL, HttpMethod.POST, entity, String.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to initiate booking: " + e.getMessage(), e);
        }
    }

    public List<BookingDTO> executeBooking(BookingDTO bookingDTO, String paymentId, String payerId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<BookingDTO> entity = new HttpEntity<>(bookingDTO, headers);
        try {
            BookingDTO[] bookings = restTemplate.exchange(
                    BASE_URL + "/execute?paymentId=" + paymentId + "&payerId=" + payerId,
                    HttpMethod.POST,
                    entity,
                    BookingDTO[].class
            ).getBody();
            if (bookings == null) {
                throw new RuntimeException("No bookings returned from API");
            }
            return Arrays.asList(bookings);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to execute booking: " + e.getMessage(), e);
        }
    }

    public BookingDTO cancelBooking(Long id, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            return restTemplate.exchange(BASE_URL + "/cancel/" + id, HttpMethod.PUT, entity, BookingDTO.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to cancel booking: " + e.getMessage(), e);
        }
    }

    public BookingDTO checkIn(Long id, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            return restTemplate.exchange(BASE_URL + "/check-in/" + id, HttpMethod.PUT, entity, BookingDTO.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to check in: " + e.getMessage(), e);
        }
    }

    public BookingDTO checkOut(Long id, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            return restTemplate.exchange(BASE_URL + "/check-out/" + id, HttpMethod.PUT, entity, BookingDTO.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to check out: " + e.getMessage(), e);
        }
    }

    public BookingDTO markPaid(Long id, String paymentIntentId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(paymentIntentId, headers);
        try {
            return restTemplate.exchange(BASE_URL + "/pay/" + id, HttpMethod.PUT, entity, BookingDTO.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to mark booking as paid: " + e.getMessage(), e);
        }
    }

    public BookingDTO getBookingByRoomNumber(String roomNumber, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            return restTemplate.exchange(BASE_URL + "/room/" + roomNumber, HttpMethod.GET, entity, BookingDTO.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch booking by room number: " + e.getMessage(), e);
        }
    }

    public ByteArrayResource generateBill(Long id, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            byte[] bill = restTemplate.exchange(BASE_URL + "/" + id + "/bill", HttpMethod.GET, entity, byte[].class).getBody();
            return new ByteArrayResource(bill);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to generate bill: " + e.getMessage(), e);
        }
    }
}