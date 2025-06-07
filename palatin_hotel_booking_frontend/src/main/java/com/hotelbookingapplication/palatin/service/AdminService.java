package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.BookingDTO;
import com.hotelbookingapplication.palatin.dto.FeedbackDTO;
import com.hotelbookingapplication.palatin.dto.HotelDTO;
import com.hotelbookingapplication.palatin.dto.HotelDetailsDTO;
import com.hotelbookingapplication.palatin.dto.ReportDTO;
import com.hotelbookingapplication.palatin.dto.RoomDTO;
import com.hotelbookingapplication.palatin.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Service
public class AdminService {

    private static final Logger LOGGER = Logger.getLogger(AdminService.class.getName());

    private final String backendApiUrl = "http://localhost:8086/api";

    @Value("${room.api.url:http://localhost:8086/api/rooms}")
    private String roomApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    public List<HotelDTO> getAllHotels(String token) {
        try {
            HttpEntity<String> entity = createHttpEntity(token);
            HotelDTO[] hotels = restTemplate.exchange(
                    backendApiUrl + "/admin/hotels",
                    HttpMethod.GET,
                    entity,
                    HotelDTO[].class
            ).getBody();
            LOGGER.info("Fetched hotels: " + (hotels != null ? hotels.length : 0));
            return hotels != null ? Arrays.asList(hotels) : Collections.emptyList();
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error fetching hotels: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error fetching hotels: " + e.getMessage());
            throw e;
        }
    }

    public HotelDTO addHotel(HotelDTO hotelDTO, String token) {
        try {
            HttpEntity<HotelDTO> entity = new HttpEntity<>(hotelDTO, createHttpHeaders(token));
            HotelDTO response = restTemplate.exchange(
                    backendApiUrl + "/admin/hotels",
                    HttpMethod.POST,
                    entity,
                    HotelDTO.class
            ).getBody();
            LOGGER.info("Added hotel: " + (response != null ? response.getName() : "null"));
            return response;
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error adding hotel: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error adding hotel: " + e.getMessage());
            throw e;
        }
    }

    public List<UserDTO> getAllStaff(String token) {
        try {
            HttpEntity<String> entity = createHttpEntity(token);
            UserDTO[] staff = restTemplate.exchange(
                    backendApiUrl + "/admin/users",
                    HttpMethod.GET,
                    entity,
                    UserDTO[].class
            ).getBody();
            LOGGER.info("Fetched staff: " + (staff != null ? staff.length : 0));
            return staff != null ? Arrays.asList(staff) : Collections.emptyList();
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error fetching staff: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error fetching staff: " + e.getMessage());
            throw e;
        }
    }

    public List<UserDTO> getUsersByHotel(Long hotelId, String token) {
        try {
            String url = backendApiUrl + "/admin/users/hotel";
            if (hotelId != null) {
                url += "?hotelId=" + hotelId;
            }
            HttpEntity<String> entity = createHttpEntity(token);
            UserDTO[] users = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    UserDTO[].class
            ).getBody();
            LOGGER.info("Fetched users for hotelId " + hotelId + ": " + (users != null ? users.length : 0));
            return users != null ? Arrays.asList(users) : Collections.emptyList();
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error fetching users by hotel: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error fetching users by hotel: " + e.getMessage());
            throw e;
        }
    }

    public UserDTO addStaff(UserDTO userDTO, String token) {
        try {
            HttpEntity<UserDTO> entity = new HttpEntity<>(userDTO, createHttpHeaders(token));
            UserDTO response = restTemplate.exchange(
                    backendApiUrl + "/admin/users",
                    HttpMethod.POST,
                    entity,
                    UserDTO.class
            ).getBody();
            LOGGER.info("Added staff: " + (response != null ? response.getEmail() : "null"));
            return response;
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error adding staff: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error adding staff: " + e.getMessage());
            throw e;
        }
    }

    public UserDTO updateStaff(UserDTO userDTO, String token) {
        try {
            HttpEntity<UserDTO> entity = new HttpEntity<>(userDTO, createHttpHeaders(token));
            UserDTO response = restTemplate.exchange(
                    backendApiUrl + "/admin/users",
                    HttpMethod.PUT,
                    entity,
                    UserDTO.class
            ).getBody();
            LOGGER.info("Updated staff: " + (response != null ? response.getEmail() : "null"));
            return response;
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error updating staff: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error updating staff: " + e.getMessage());
            throw e;
        }
    }

    public void deactivateStaff(Long id, String email, String token) {
        try {
            HttpHeaders headers = createHttpHeaders(token);
            String url = backendApiUrl + "/admin/users/deactivate";
            if (id != null) {
                url += "?id=" + id;
            } else if (email != null) {
                url += "?email=" + email;
            }
            HttpEntity<String> entity = new HttpEntity<>(headers);
            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Void.class
            );
            LOGGER.info("Deactivated staff: id=" + id + ", email=" + email);
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error deactivating staff: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error deactivating staff: " + e.getMessage());
            throw e;
        }
    }

    public void toggleStaffStatus(Long id, String email, String token) {
        try {
            HttpHeaders headers = createHttpHeaders(token);
            String url = backendApiUrl + "/admin/users/toggle-status";
            if (id != null) {
                url += "?id=" + id;
            } else if (email != null) {
                url += "?email=" + email;
            }
            HttpEntity<String> entity = new HttpEntity<>(headers);
            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Void.class
            );
            LOGGER.info("Toggled staff status: id=" + id + ", email=" + email);
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error toggling staff status: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error toggling staff status: " + e.getMessage());
            throw e;
        }
    }

    public void addFloor(Long hotelId, Integer floorNumber, List<RoomDTO> rooms, String token) {
        try {
            HttpHeaders headers = createHttpHeaders(token);
            for (RoomDTO room : rooms) {
                room.setHotelId(hotelId);
                room.setFloor(floorNumber);
                HttpEntity<RoomDTO> entity = new HttpEntity<>(room, headers);
                LOGGER.info("Adding room: " + room);
                restTemplate.exchange(
                        roomApiUrl,
                        HttpMethod.POST,
                        entity,
                        RoomDTO.class
                );
            }
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error adding rooms: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error adding rooms: " + e.getMessage());
            throw e;
        }
    }

    public Long getRoomIdByHotelIdAndRoomNumber(Long hotelId, String roomNumber, String token) {
        try {
            HttpEntity<?> entity = createHttpEntity(token);
            LOGGER.info("Fetching room ID for hotelId: " + hotelId + ", roomNumber: " + roomNumber);
            RoomDTO room = restTemplate.exchange(
                    roomApiUrl + "/hotel/" + hotelId + "/room/" + roomNumber,
                    HttpMethod.GET,
                    entity,
                    RoomDTO.class
            ).getBody();
            if (room != null) {
                LOGGER.info("Room found: " + room);
                return room.getId();
            }
            LOGGER.warning("No room found for hotelId: " + hotelId + ", roomNumber: " + roomNumber);
            return null;
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error fetching room ID: " + e.getStatusCode() + " - " + e.getMessage());
            return null;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error fetching room ID: " + e.getMessage());
            return null;
        }
    }

    public void updateRoom(Long id, RoomDTO roomDTO, String token) {
        try {
            HttpEntity<RoomDTO> entity = new HttpEntity<>(roomDTO, createHttpHeaders(token));
            LOGGER.info("Updating room with ID: " + id + ", data: " + roomDTO);
            restTemplate.exchange(
                    roomApiUrl + "/" + id,
                    HttpMethod.PUT,
                    entity,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error updating room: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error updating room: " + e.getMessage());
            throw e;
        }
    }

    public ReportDTO getReport(Long hotelId, LocalDate startDate, LocalDate endDate, String token) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            String formattedStartDate = startDate.format(formatter);
            String formattedEndDate = endDate.format(formatter);
            String url = backendApiUrl + "/admin/report?startDate=" + formattedStartDate + "&endDate=" + formattedEndDate;
            if (hotelId != null) {
                url += "&hotelId=" + hotelId;
            }
            HttpEntity<String> entity = createHttpEntity(token);
            ReportDTO report = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ReportDTO.class
            ).getBody();
            if (report != null) {
                // Initialize lists if null to prevent frontend issues
                if (report.getMonthlyRevenue() == null) report.setMonthlyRevenue(new ArrayList<>());
                if (report.getMonthlyBookings() == null) report.setMonthlyBookings(new ArrayList<>());
                if (report.getMonthlyUsers() == null) report.setMonthlyUsers(new ArrayList<>());
                LOGGER.info("Fetched report for hotelId: " + hotelId + ", monthlyRevenue: " + report.getMonthlyRevenue());
            } else {
                LOGGER.warning("No report data received for hotelId: " + hotelId);
            }
            return report;
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error fetching report: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error fetching report: " + e.getMessage());
            throw e;
        }
    }

    public List<ReportDTO> getAllHotelSummaries(LocalDate startDate, LocalDate endDate, String token) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            String formattedStartDate = startDate.format(formatter);
            String formattedEndDate = endDate.format(formatter);
            String url = backendApiUrl + "/admin/hotel-summaries?startDate=" + formattedStartDate + "&endDate=" + formattedEndDate;
            HttpEntity<String> entity = createHttpEntity(token);
            ReportDTO[] summaries = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ReportDTO[].class
            ).getBody();
            LOGGER.info("Fetched hotel summaries: " + (summaries != null ? summaries.length : 0));
            return summaries != null ? Arrays.asList(summaries) : Collections.emptyList();
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error fetching hotel summaries: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error fetching hotel summaries: " + e.getMessage());
            throw e;
        }
    }

    public HotelDetailsDTO getHotelDetails(Long hotelId, String token) {
        try {
            HttpEntity<String> entity = createHttpEntity(token);
            HotelDetailsDTO details = restTemplate.exchange(
                    backendApiUrl + "/admin/hotel-details/" + hotelId,
                    HttpMethod.GET,
                    entity,
                    HotelDetailsDTO.class
            ).getBody();
            LOGGER.info("Fetched hotel details for hotelId: " + hotelId);
            return details;
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error fetching hotel details: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error fetching hotel details: " + e.getMessage());
            throw e;
        }
    }

    public List<BookingDTO> getBookings(Long hotelId, LocalDate startDate, LocalDate endDate, String status, String token) {
        try {
            String url = backendApiUrl + "/admin/bookings";
            List<String> params = new ArrayList<>();
            if (hotelId != null) {
                params.add("hotelId=" + hotelId);
            }
            if (startDate != null) {
                params.add("startDate=" + startDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
            if (endDate != null) {
                params.add("endDate=" + endDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
            if (status != null && !status.isEmpty()) {
                params.add("status=" + status);
            }
            if (!params.isEmpty()) {
                url += "?" + String.join("&", params);
            }
            HttpEntity<String> entity = createHttpEntity(token);
            BookingDTO[] bookings = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    BookingDTO[].class
            ).getBody();
            LOGGER.info("Fetched bookings for hotelId: " + hotelId + ": " + (bookings != null ? bookings.length : 0));
            return bookings != null ? Arrays.asList(bookings) : Collections.emptyList();
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error fetching bookings: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error fetching bookings: " + e.getMessage());
            throw e;
        }
    }

    public List<FeedbackDTO> getFeedback(Long hotelId, String token) {
        try {
            String url = backendApiUrl + "/admin/feedback";
            if (hotelId != null) {
                url += "?hotelId=" + hotelId;
            }
            else{
                url+="?hotelId=" +0;
            }
            HttpEntity<String> entity = createHttpEntity(token);
            FeedbackDTO[] feedback = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    FeedbackDTO[].class
            ).getBody();
            LOGGER.info("Fetched feedback for hotelId: " + hotelId + ": " + (feedback != null ? feedback.length : 0));
            return feedback != null ? Arrays.asList(feedback) : Collections.emptyList();
        } catch (HttpClientErrorException e) {
            LOGGER.severe("Error fetching feedback: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error fetching feedback: " + e.getMessage());
            throw e;
        }
    }

    private HttpHeaders createHttpHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }

    private HttpEntity<String> createHttpEntity(String token) {
        return new HttpEntity<>(createHttpHeaders(token));
    }
}