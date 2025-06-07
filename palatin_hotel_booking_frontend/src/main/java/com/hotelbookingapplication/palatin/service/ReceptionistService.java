package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.BookingDTO;
import com.hotelbookingapplication.palatin.dto.CabBookingDTO;
import com.hotelbookingapplication.palatin.dto.RoomDTO;
import com.hotelbookingapplication.palatin.dto.ServiceRequestDTO;
import com.hotelbookingapplication.palatin.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ReceptionistService {

    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8086/api/receptionist";
    private final String USER_URL = "http://localhost:8086/api/users";

    public List<BookingDTO> getTodayBookings(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        BookingDTO[] bookings = restTemplate.exchange(BASE_URL + "/bookings/today", HttpMethod.GET, entity, BookingDTO[].class).getBody();
        return Arrays.asList(bookings);
    }

    public List<BookingDTO> getBookingsByDateRange(String token, LocalDate startDate, LocalDate endDate) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = BASE_URL + "/bookings/date-range?startDate=" + startDate + "&endDate=" + endDate;
        BookingDTO[] bookings = restTemplate.exchange(url, HttpMethod.GET, entity, BookingDTO[].class).getBody();
        return Arrays.asList(bookings);
    }

    public BookingDTO checkIn(Long bookingId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(BASE_URL + "/check-in/" + bookingId, HttpMethod.POST, entity, BookingDTO.class).getBody();
    }

    public BookingDTO checkOut(Long bookingId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(BASE_URL + "/check-out/" + bookingId, HttpMethod.POST, entity, BookingDTO.class).getBody();
    }

    public String initiateOfflineBooking(BookingDTO bookingDTO, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<BookingDTO> entity = new HttpEntity<>(bookingDTO, headers);
        try {
            return restTemplate.exchange(BASE_URL + "/offline-booking/initiate", HttpMethod.POST, entity, String.class).getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initiate offline booking: " + e.getMessage());
        }
    }

    public List<BookingDTO> executeOfflineBooking(BookingDTO bookingDTO, String paymentId, String payerId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<BookingDTO> entity = new HttpEntity<>(bookingDTO, headers);
        try {
            BookingDTO[] bookings = restTemplate.exchange(
                    BASE_URL + "/offline-booking/execute?paymentId=" + paymentId + "&payerId=" + payerId,
                    HttpMethod.POST,
                    entity,
                    BookingDTO[].class
            ).getBody();
            return Arrays.asList(bookings);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute offline booking: " + e.getMessage());
        }
    }

    public Map<String, Long> getRoomStats(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(BASE_URL + "/room-stats", HttpMethod.GET, entity, Map.class).getBody();
    }

    public List<ServiceRequestDTO> getServiceRequests(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ServiceRequestDTO[] requests = restTemplate.exchange(BASE_URL + "/service-requests", HttpMethod.GET, entity, ServiceRequestDTO[].class).getBody();
        return Arrays.asList(requests);
    }

    public List<UserDTO> getStaffByRole(String token, String role) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String staffRole = role;
        if ("LAUNDRY".equalsIgnoreCase(role) || "LUGGAGE".equalsIgnoreCase(role)) {
            staffRole = role.toUpperCase();
        } else if ("CAB".equalsIgnoreCase(role)) {
            staffRole = "CAB";
        }
        String url = BASE_URL + "/staff-by-role?role=" + staffRole;
        UserDTO[] staff = restTemplate.exchange(url, HttpMethod.GET, entity, UserDTO[].class).getBody();
        return Arrays.asList(staff);
    }

    public ServiceRequestDTO assignStaff(Long id, String staffEmail, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(staffEmail, headers);
        try {
            return restTemplate.exchange(BASE_URL + "/service-requests/assign/" + id, HttpMethod.POST, entity, ServiceRequestDTO.class).getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign staff: " + e.getMessage());
        }
    }

    public List<CabBookingDTO> getCabRequests(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        CabBookingDTO[] bookings = restTemplate.exchange(BASE_URL + "/cab-requests", HttpMethod.GET, entity, CabBookingDTO[].class).getBody();
        return Arrays.asList(bookings);
    }

    public CabBookingDTO assignDriver(Long id, String driverEmail, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(driverEmail, headers);
        return restTemplate.exchange(BASE_URL + "/cab-requests/assign/" + id, HttpMethod.POST, entity, CabBookingDTO.class).getBody();
    }

    public RoomDTO getRoomByRoomNumber(String roomNumber, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = BASE_URL + "/room-status?roomNumber=" + roomNumber;
        try {
            return restTemplate.exchange(url, HttpMethod.GET, entity, RoomDTO.class).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public BookingDTO getBookingByRoomNumber(String roomNumber, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = BASE_URL + "/booking-by-room?roomNumber=" + roomNumber;
        try {
            return restTemplate.exchange(url, HttpMethod.GET, entity, BookingDTO.class).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public List<RoomDTO> getAvailableRooms(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = BASE_URL + "/available-rooms";
        RoomDTO[] rooms = restTemplate.exchange(url, HttpMethod.GET, entity, RoomDTO[].class).getBody();
        return Arrays.asList(rooms);
    }

    public UserDTO getCurrentUser(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(USER_URL + "/me", HttpMethod.GET, entity, UserDTO.class).getBody();
    }
}