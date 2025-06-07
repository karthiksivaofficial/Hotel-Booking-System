package com.hotelbookingapplication.palatin.service;

import com.hotelbookingapplication.palatin.dto.HotelDTO;
import com.hotelbookingapplication.palatin.dto.RoomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
public class HotelService {
    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8086/api/hotels";
    private final String ROOMS_URL = "http://localhost:8086/api/rooms/hotel";

    public List<HotelDTO> getAllHotels() {
        HotelDTO[] hotels = restTemplate.getForObject(BASE_URL + "/public", HotelDTO[].class);
        return Arrays.asList(hotels);
    }

    public List<HotelDTO> getHotelsByCity(String city) {
        HotelDTO[] hotels = restTemplate.getForObject(BASE_URL + "/public/city/" + city, HotelDTO[].class);
        return Arrays.asList(hotels);
    }

    public HotelDTO getHotelById(Long id, String token) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);
        HotelDTO hotel = restTemplate.exchange(BASE_URL + "/" + id, HttpMethod.GET, entity, HotelDTO.class).getBody();
        // Fetch rooms with no filters initially
        RoomDTO[] rooms = getRoomsByHotelId(id, token, null, null, null, null, null, null, null);
        hotel.setRooms(Arrays.asList(rooms));
        return hotel;
    }

    public HotelDTO getHotelByIdWithFilters(Long id, String token, String type, String view, Double minPrice,
                                            Double maxPrice, Integer floor, String checkIn, String checkOut) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);
        HotelDTO hotel = restTemplate.exchange(BASE_URL + "/" + id, HttpMethod.GET, entity, HotelDTO.class).getBody();
        // Fetch rooms with filters
        RoomDTO[] rooms = getRoomsByHotelId(id, token, type, view, minPrice, maxPrice, floor, checkIn, checkOut);
        hotel.setRooms(Arrays.asList(rooms));
        return hotel;
    }

    private RoomDTO[] getRoomsByHotelId(Long hotelId, String token, String type, String view, Double minPrice,
                                        Double maxPrice, Integer floor, String checkIn, String checkOut) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Build URL with query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(ROOMS_URL + "/" + hotelId);
        if (type != null && !type.isEmpty()) {
            builder.queryParam("type", type);
        }
        if (view != null && !view.isEmpty()) {
            builder.queryParam("view", view);
        }
        if (minPrice != null) {
            builder.queryParam("minPrice", minPrice);
        }
        if (maxPrice != null) {
            builder.queryParam("maxPrice", maxPrice);
        }
        if (floor != null) {
            builder.queryParam("floor", floor);
        }
        if (checkIn != null && !checkIn.isEmpty()) {
            builder.queryParam("checkIn", checkIn);
        }
        if (checkOut != null && !checkOut.isEmpty()) {
            builder.queryParam("checkOut", checkOut);
        }

        String url = builder.build().toUriString();
        return restTemplate.exchange(url, HttpMethod.GET, entity, RoomDTO[].class).getBody();
    }
}