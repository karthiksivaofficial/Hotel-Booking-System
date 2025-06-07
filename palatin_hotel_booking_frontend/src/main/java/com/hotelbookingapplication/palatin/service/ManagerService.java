package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.BookingDTO;
import com.hotelbookingapplication.palatin.dto.FeedbackDTO;
import com.hotelbookingapplication.palatin.dto.ReportDTO;
import com.hotelbookingapplication.palatin.dto.RoomDTO;
import com.hotelbookingapplication.palatin.dto.ServiceRequestDTO;
import com.hotelbookingapplication.palatin.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class ManagerService {

    @Value("${backend.api.url}")
    private String BASE_URL;

    @Autowired
    private RestTemplate restTemplate;

    public Long getManagerHotelId(HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = BASE_URL + "/users/me";
        UserDTO user = restTemplate.exchange(url, HttpMethod.GET, entity, UserDTO.class).getBody();
        return user.getHotelId();
    }

    public ReportDTO getReport(LocalDate startDate, LocalDate endDate, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        String url = BASE_URL + "/manager/report?startDate=" + startDate + "&endDate=" + endDate;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, ReportDTO.class).getBody();
    }

    public List<FeedbackDTO> getFeedback(HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        FeedbackDTO[] feedback = restTemplate.exchange(BASE_URL + "/manager/feedback", HttpMethod.GET, entity, FeedbackDTO[].class).getBody();
        return Arrays.asList(feedback);
    }

    public UserDTO addStaff(UserDTO userDTO, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<UserDTO> entity = new HttpEntity<>(userDTO, headers);
        return restTemplate.exchange(BASE_URL + "/manager/staff", HttpMethod.POST, entity, UserDTO.class).getBody();
    }

    public void deactivateStaff(Long id, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(BASE_URL + "/manager/staff/deactivate/" + id, HttpMethod.PUT, entity, Void.class);
    }

    public void deactivateStaffByEmail(String email, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(BASE_URL + "/manager/staff/deactivate/email/" + email, HttpMethod.PUT, entity, Void.class);
    }

    public void updateStaffStatus(Long id, boolean active, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Boolean> entity = new HttpEntity<>(active, headers);
        restTemplate.exchange(BASE_URL + "/manager/staff/status/" + id, HttpMethod.PUT, entity, Void.class);
    }

    public void updateRoomStatus(Long id, String status, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(status, headers);
        restTemplate.exchange(BASE_URL + "/manager/rooms/" + id + "/status", HttpMethod.PUT, entity, Void.class);
    }

    public void updateRoomStatusByRoomNumber(String roomNumber, Long hotelId, String status, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(status, headers);
        String url = BASE_URL + "/manager/rooms/roomNumber/" + roomNumber + "/status?hotelId=" + hotelId;
        restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }

    public List<RoomDTO> getRoomsByHotelId(Long hotelId, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = BASE_URL + "/manager/rooms/hotel/" + hotelId;
        RoomDTO[] rooms = restTemplate.exchange(url, HttpMethod.GET, entity, RoomDTO[].class).getBody();
        return Arrays.asList(rooms);
    }

    public void addFloor(Long hotelId, Integer floorNumber, List<RoomDTO> rooms, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<List<RoomDTO>> entity = new HttpEntity<>(rooms, headers);
        String url = BASE_URL + "/manager/rooms/floor/" + hotelId + "?floorNumber=" + floorNumber;
        restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
    }

    public void updateRoomByHotelIdAndRoomNumber(Long hotelId, String roomNumber, RoomDTO roomDTO, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<RoomDTO> entity = new HttpEntity<>(roomDTO, headers);
        String url = BASE_URL + "/manager/rooms/hotel/" + hotelId + "/room/" + roomNumber;
        restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }

    public List<ServiceRequestDTO> getServiceRequestsByHotelId(Long hotelId, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ServiceRequestDTO[] requests = restTemplate.exchange(BASE_URL + "/service-requests/hotel/" + hotelId, HttpMethod.GET, entity, ServiceRequestDTO[].class).getBody();
        return Arrays.asList(requests);
    }

    public List<UserDTO> getStaffByHotelId(Long hotelId, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UserDTO[] staff = restTemplate.exchange(BASE_URL + "/users?hotelId=" + hotelId, HttpMethod.GET, entity, UserDTO[].class).getBody();
        return Arrays.asList(staff);
    }

    public void assignServiceRequest(Long id, Long staffId, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Long> entity = new HttpEntity<>(staffId, headers);
        restTemplate.exchange(BASE_URL + "/service-requests/assign/" + id, HttpMethod.POST, entity, ServiceRequestDTO.class);
    }

    public void completeServiceRequest(Long id, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>("COMPLETED", headers);
        restTemplate.exchange(BASE_URL + "/service-requests/status/" + id, HttpMethod.PUT, entity, ServiceRequestDTO.class);
    }

    public List<BookingDTO> getBookingsByHotelWithFilters(Long hotelId, LocalDate startDate, LocalDate endDate, String status, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        StringBuilder url = new StringBuilder(BASE_URL + "/manager/bookings/hotel/" + hotelId);
        url.append("?");
        if (startDate != null) {
            url.append("startDate=").append(startDate).append("&");
        }
        if (endDate != null) {
            url.append("endDate=").append(endDate).append("&");
        }
        if (status != null && !status.isEmpty()) {
            url.append("status=").append(status);
        }
        BookingDTO[] bookings = restTemplate.exchange(url.toString(), HttpMethod.GET, entity, BookingDTO[].class).getBody();
        return Arrays.asList(bookings);
    }

    public double getRevenueByHotelWithFilters(Long hotelId, LocalDate startDate, LocalDate endDate, String status, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        StringBuilder url = new StringBuilder(BASE_URL + "/manager/bookings/hotel/" + hotelId + "/revenue");
        url.append("?");
        if (startDate != null) {
            url.append("startDate=").append(startDate).append("&");
        }
        if (endDate != null) {
            url.append("endDate=").append(endDate).append("&");
        }
        if (status != null && !status.isEmpty()) {
            url.append("status=").append(status);
        }
        return restTemplate.exchange(url.toString(), HttpMethod.GET, entity, Double.class).getBody();
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