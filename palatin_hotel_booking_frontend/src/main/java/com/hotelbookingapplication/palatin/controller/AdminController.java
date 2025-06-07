package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.*;
import com.hotelbookingapplication.palatin.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "admin/dashboard";
    }

    @GetMapping("/hotels")
    public String hotelManagement(Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login?error=Please log in to access this page";
        }
        try {
            model.addAttribute("hotelDTO", new HotelDTO());
            model.addAttribute("hotels", adminService.getAllHotels(token));
        } catch (HttpClientErrorException.Forbidden e) {
            model.addAttribute("error", "Access denied: Insufficient permissions to view hotels.");
            model.addAttribute("hotels", Collections.emptyList());
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load hotels: " + e.getMessage());
            model.addAttribute("hotels", Collections.emptyList());
        }
        return "admin/hotel_management";
    }

    @PostMapping("/hotels")
    public String addHotel(@ModelAttribute @Valid HotelDTO hotelDTO, BindingResult bindingResult, HttpServletRequest request, Model model) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login?error=Please log in to access this page";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Invalid hotel data: " + bindingResult.getAllErrors());
            model.addAttribute("hotels", adminService.getAllHotels(token));
            return "admin/hotel_management";
        }
        try {
            adminService.addHotel(hotelDTO, token);
            return "redirect:/admin/hotels?success";
        } catch (HttpClientErrorException.Forbidden e) {
            model.addAttribute("error", "Access denied: Insufficient permissions to add hotel.");
            model.addAttribute("hotels", adminService.getAllHotels(token));
            return "admin/hotel_management";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add hotel: " + e.getMessage());
            model.addAttribute("hotels", adminService.getAllHotels(token));
            return "admin/hotel_management";
        }
    }

    @GetMapping("/staff")
    public String staffManagement(Model model, HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login?error=Please log in to access this page";
        }
        try {
            model.addAttribute("userDTO", new UserDTO());
            model.addAttribute("staffs", adminService.getAllStaff(token));
        } catch (HttpClientErrorException.Forbidden e) {
            model.addAttribute("error", "Access denied: Insufficient permissions to view staff.");
            model.addAttribute("staffs", Collections.emptyList());
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load staff: " + e.getMessage());
            model.addAttribute("staffs", Collections.emptyList());
        }
        return "admin/staff_management";
    }

    @PostMapping("/staff")
    public String addOrUpdateStaff(@ModelAttribute @Valid UserDTO userDTO, BindingResult bindingResult, HttpServletRequest request, Model model) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login?error=Please log in to access this page";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Invalid staff data: " + bindingResult.getAllErrors());
            model.addAttribute("staffs", adminService.getAllStaff(token));
            return "admin/staff_management";
        }
        try {
            if (userDTO.getId() != null) {
                adminService.updateStaff(userDTO, token);
            } else {
                adminService.addStaff(userDTO, token);
            }
            return "redirect:/admin/staff?success";
        } catch (HttpClientErrorException.Forbidden e) {
            model.addAttribute("error", "Access denied: Insufficient permissions to modify staff.");
            model.addAttribute("staffs", adminService.getAllStaff(token));
            return "admin/staff_management";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to modify staff: " + e.getMessage());
            model.addAttribute("staffs", adminService.getAllStaff(token));
            return "admin/staff_management";
        }
    }

    @PostMapping("/staff/deactivate")
    public String deactivateStaff(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String email,
            HttpServletRequest request, Model model) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login?error=Please log in to access this page";
        }
        if (id == null && email == null) {
            model.addAttribute("error", "Either ID or email must be provided.");
            model.addAttribute("staffs", adminService.getAllStaff(token));
            return "admin/staff_management";
        }
        try {
            adminService.deactivateStaff(id, email, token);
            return "redirect:/admin/staff?success";
        } catch (HttpClientErrorException.Forbidden e) {
            model.addAttribute("error", "Access denied: Insufficient permissions to deactivate staff.");
            model.addAttribute("staffs", adminService.getAllStaff(token));
            return "admin/staff_management";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to deactivate staff: " + e.getMessage());
            model.addAttribute("staffs", adminService.getAllStaff(token));
            return "admin/staff_management";
        }
    }

    @PostMapping("/staff/toggle-status")
    public String toggleStaffStatus(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String email,
            HttpServletRequest request, Model model) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login?error=Please log in to access this page";
        }
        if (id == null && email == null) {
            model.addAttribute("error", "Either ID or email must be provided.");
            model.addAttribute("staffs", adminService.getAllStaff(token));
            return "admin/staff_management";
        }
        try {
            adminService.toggleStaffStatus(id, email, token);
            return "redirect:/admin/staff?success";
        } catch (HttpClientErrorException.Forbidden e) {
            model.addAttribute("error", "Access denied: Insufficient permissions to toggle staff status.");
            model.addAttribute("staffs", adminService.getAllStaff(token));
            return "admin/staff_management";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to toggle staff status: " + e.getMessage());
            model.addAttribute("staffs", adminService.getAllStaff(token));
            return "admin/staff_management";
        }
    }

    @GetMapping("/rooms")
    public String roomManagement(Model model) {
        model.addAttribute("roomUpdateForm", new RoomUpdateForm());
        model.addAttribute("roomForm", new RoomForm());
        model.addAttribute("hotelId", "");
        return "admin/room_management";
    }

    @PostMapping("/rooms/add")
    public String addRooms(
            @RequestParam("hotelId") Long hotelId,
            @RequestParam("floorNumber") Integer floorNumber,
            @RequestParam("numberOfRooms") Integer numberOfRooms,
            @RequestParam(value = "rooms[0].roomNumber", required = false) String roomNumber0,
            @RequestParam(value = "rooms[0].type", required = false) String type0,
            @RequestParam(value = "rooms[0].view", required = false) String view0,
            @RequestParam(value = "rooms[0].price", required = false) Double price0,
            @RequestParam(value = "rooms[0].amenities", required = false) String amenities0,
            @RequestParam(value = "rooms[1].roomNumber", required = false) String roomNumber1,
            @RequestParam(value = "rooms[1].type", required = false) String type1,
            @RequestParam(value = "rooms[1].view", required = false) String view1,
            @RequestParam(value = "rooms[1].price", required = false) Double price1,
            @RequestParam(value = "rooms[1].amenities", required = false) String amenities1,
            HttpServletRequest request, Model model) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login?error=Please log in to access this page";
        }
        List<RoomInput> roomsInput = new ArrayList<>();
        if (roomNumber0 != null) {
            RoomInput room0 = new RoomInput();
            room0.setRoomNumber(roomNumber0);
            room0.setType(type0);
            room0.setView(view0);
            room0.setPrice(price0 != null ? price0 : 0.0);
            room0.setAmenities(amenities0 != null ? amenities0 : "");
            roomsInput.add(room0);
        }
        if (roomNumber1 != null) {
            RoomInput room1 = new RoomInput();
            room1.setRoomNumber(roomNumber1);
            room1.setType(type1);
            room1.setView(view1);
            room1.setPrice(price1 != null ? price1 : 0.0);
            room1.setAmenities(amenities1 != null ? amenities1 : "");
            roomsInput.add(room1);
        }

        try {
            if (roomsInput.isEmpty() || roomsInput.size() != numberOfRooms) {
                model.addAttribute("error", "Invalid room data: Number of rooms does not match input data.");
                model.addAttribute("roomUpdateForm", new RoomUpdateForm());
                model.addAttribute("roomForm", new RoomForm());
                return "admin/room_management";
            }
            for (RoomInput room : roomsInput) {
                if (room.getRoomNumber() == null || room.getRoomNumber().isEmpty() ||
                        room.getType() == null || room.getType().isEmpty() ||
                        room.getView() == null || room.getView().isEmpty() ||
                        room.getPrice() == null) {
                    model.addAttribute("error", "Invalid room data: All fields (roomNumber, type, view, price) are required.");
                    model.addAttribute("roomUpdateForm", new RoomUpdateForm());
                    model.addAttribute("roomForm", new RoomForm());
                    return "admin/room_management";
                }
            }

            List<RoomDTO> rooms = new ArrayList<>();
            for (RoomInput input : roomsInput) {
                RoomDTO room = new RoomDTO();
                room.setHotelId(hotelId);
                room.setFloor(floorNumber);
                room.setRoomNumber(input.getRoomNumber());
                room.setType(input.getType());
                room.setView(input.getView());
                room.setPrice(input.getPrice());
                room.setAmenities(input.getAmenities());
                room.setStatus("AVAILABLE");
                rooms.add(room);
            }

            adminService.addFloor(hotelId, floorNumber, rooms, token);
            return "redirect:/admin/rooms?success";
        } catch (HttpClientErrorException.Forbidden e) {
            model.addAttribute("error", "Access denied: Insufficient permissions to add rooms.");
            model.addAttribute("roomUpdateForm", new RoomUpdateForm());
            model.addAttribute("roomForm", new RoomForm());
            return "admin/room_management";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add rooms: " + e.getMessage());
            model.addAttribute("roomUpdateForm", new RoomUpdateForm());
            model.addAttribute("roomForm", new RoomForm());
            return "admin/room_management";
        }
    }

    @PostMapping("/rooms/update")
    public String updateRoom(@ModelAttribute @Valid RoomUpdateForm roomUpdateForm, BindingResult bindingResult,
                             HttpServletRequest request, Model model) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login?error=Please log in to access this page";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Invalid form data: " + bindingResult.getAllErrors());
            model.addAttribute("roomUpdateForm", roomUpdateForm);
            model.addAttribute("roomForm", new RoomForm());
            return "admin/room_management";
        }

        try {
            Long roomId = adminService.getRoomIdByHotelIdAndRoomNumber(roomUpdateForm.getHotelId(),
                    roomUpdateForm.getRoomNumber(), token);
            if (roomId == null) {
                model.addAttribute("error", "Room not found for Hotel ID: " + roomUpdateForm.getHotelId() +
                        " and Room Number: " + roomUpdateForm.getRoomNumber());
                model.addAttribute("roomUpdateForm", roomUpdateForm);
                model.addAttribute("roomForm", new RoomForm());
                return "admin/room_management";
            }

            RoomDTO roomDTO = new RoomDTO();
            roomDTO.setId(roomId);
            roomDTO.setHotelId(roomUpdateForm.getHotelId());
            roomDTO.setRoomNumber(roomUpdateForm.getRoomNumber());
            roomDTO.setType(roomUpdateForm.getType());
            roomDTO.setView(roomUpdateForm.getView());
            roomDTO.setPrice(roomUpdateForm.getPrice());
            roomDTO.setAmenities(roomUpdateForm.getAmenities());
            roomDTO.setStatus(roomUpdateForm.getStatus());
            roomDTO.setFloor(roomUpdateForm.getFloor());

            adminService.updateRoom(roomId, roomDTO, token);
            return "redirect:/admin/rooms?success";
        } catch (HttpClientErrorException.Forbidden e) {
            model.addAttribute("error", "Access denied: Insufficient permissions to update room.");
            model.addAttribute("roomUpdateForm", roomUpdateForm);
            model.addAttribute("roomForm", new RoomForm());
            return "admin/room_management";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update room: " + e.getMessage());
            model.addAttribute("roomUpdateForm", roomUpdateForm);
            model.addAttribute("roomForm", new RoomForm());
            return "admin/room_management";
        }
    }

    @GetMapping("/report")
    public String report(Model model, HttpServletRequest request,
                         @RequestParam(value = "hotelId", required = false) Long hotelId,
                         @RequestParam(value = "startDate", required = false) String startDate,
                         @RequestParam(value = "endDate", required = false) String endDate) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login?error=Please log in to access this page";
        }
        try {
            List<HotelDTO> hotels = adminService.getAllHotels(token);
            model.addAttribute("hotels", hotels);

            if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                try {
                    LocalDate start = LocalDate.parse(startDate, formatter);
                    LocalDate end = LocalDate.parse(endDate, formatter);
                    ReportDTO report = adminService.getReport(hotelId, start, end, token);
                    model.addAttribute("report", report);
                } catch (DateTimeParseException e) {
                    model.addAttribute("error", "Invalid date format. Please use YYYY-MM-DD.");
                }
            }
            // Add filter parameters to model to persist form values
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        } catch (HttpClientErrorException.Forbidden e) {
            model.addAttribute("error", "Access denied: Insufficient permissions to view report.");
            model.addAttribute("hotels", Collections.emptyList());
            // Add filter parameters even in case of error
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to generate report: " + e.getMessage());
            model.addAttribute("hotels", Collections.emptyList());
            // Add filter parameters even in case of error
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        }
        return "admin/report";
    }

    @GetMapping("/hotel-cards")
    public String hotelCards(Model model, HttpServletRequest request,
                             @RequestParam(value = "startDate", required = false) String startDate,
                             @RequestParam(value = "endDate", required = false) String endDate) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login?error=Please log in to access this page";
        }
        try {
            if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                try {
                    LocalDate start = LocalDate.parse(startDate, formatter);
                    LocalDate end = LocalDate.parse(endDate, formatter);
                    List<ReportDTO> summaries = adminService.getAllHotelSummaries(start, end, token);
                    model.addAttribute("summaries", summaries);
                } catch (DateTimeParseException e) {
                    model.addAttribute("error", "Invalid date format. Please use YYYY-MM-DD.");
                }
            }
            // Add filter parameters to model to persist form values
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        } catch (HttpClientErrorException.Forbidden e) {
            model.addAttribute("error", "Access denied: Insufficient permissions to view hotel summaries.");
            model.addAttribute("summaries", Collections.emptyList());
            // Add filter parameters even in case of error
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load hotel summaries: " + e.getMessage());
            model.addAttribute("summaries", Collections.emptyList());
            // Add filter parameters even in case of error
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
        }
        return "admin/hotel_cards";
    }

    @GetMapping("/hotel-details")
    public String hotelDetails(Model model, HttpServletRequest request,
                               @RequestParam("hotelId") Long hotelId,
                               @RequestParam(value = "bookingStartDate", required = false) String bookingStartDate,
                               @RequestParam(value = "bookingEndDate", required = false) String bookingEndDate,
                               @RequestParam(value = "bookingStatus", required = false) String bookingStatus) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login?error=Please log in to access this page";
        }
        try {
            HotelDetailsDTO details = adminService.getHotelDetails(hotelId, token);
            ReportDTO report = adminService.getReport(hotelId, LocalDate.now().minusYears(1), LocalDate.now(), token);
            List<FeedbackDTO> feedback = adminService.getFeedback(hotelId, token);

            List<BookingDTO> bookings;
            if (bookingStartDate != null && bookingEndDate != null && !bookingStartDate.isEmpty() && !bookingEndDate.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                try {
                    LocalDate start = LocalDate.parse(bookingStartDate, formatter);
                    LocalDate end = LocalDate.parse(bookingEndDate, formatter);
                    bookings = adminService.getBookings(hotelId, start, end, bookingStatus, token);
                } catch (DateTimeParseException e) {
                    model.addAttribute("error", "Invalid booking date format. Please use YYYY-MM-DD.");
                    bookings = adminService.getBookings(hotelId, null, null, bookingStatus, token);
                }
            } else {
                bookings = adminService.getBookings(hotelId, null, null, bookingStatus, token);
            }

            model.addAttribute("details", details);
            model.addAttribute("report", report);
            model.addAttribute("feedback", feedback);
            model.addAttribute("bookings", bookings);
            // Add filter parameters to model to persist form values
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("bookingStartDate", bookingStartDate);
            model.addAttribute("bookingEndDate", bookingEndDate);
            model.addAttribute("bookingStatus", bookingStatus);
        } catch (HttpClientErrorException.Forbidden e) {
            model.addAttribute("error", "Access denied: Insufficient permissions to view hotel details.");
            // Add filter parameters even in case of error
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("bookingStartDate", bookingStartDate);
            model.addAttribute("bookingEndDate", bookingEndDate);
            model.addAttribute("bookingStatus", bookingStatus);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load hotel details: " + e.getMessage());
            // Add filter parameters even in case of error
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("bookingStartDate", bookingStartDate);
            model.addAttribute("bookingEndDate", bookingEndDate);
            model.addAttribute("bookingStatus", bookingStatus);
        }
        return "admin/hotel_details";
    }

    @GetMapping("/bookings")
    public String viewBookings(Model model, HttpServletRequest request,
                               @RequestParam(value = "hotelId", required = false) Long hotelId,
                               @RequestParam(value = "startDate", required = false) String startDate,
                               @RequestParam(value = "endDate", required = false) String endDate,
                               @RequestParam(value = "status", required = false) String status) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login?error=Please log in to access this page";
        }
        try {
            List<HotelDTO> hotels = adminService.getAllHotels(token);
            model.addAttribute("hotels", hotels);

            List<BookingDTO> bookings;
            if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                try {
                    LocalDate start = LocalDate.parse(startDate, formatter);
                    LocalDate end = LocalDate.parse(endDate, formatter);
                    bookings = adminService.getBookings(hotelId, start, end, status, token);
                } catch (DateTimeParseException e) {
                    model.addAttribute("error", "Invalid date format. Please use YYYY-MM-DD.");
                    bookings = adminService.getBookings(hotelId, null, null, status, token);
                }
            } else {
                bookings = adminService.getBookings(hotelId, null, null, status, token);
            }
            model.addAttribute("bookings", bookings);
            // Add filter parameters to model to persist form values
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("status", status);
        } catch (HttpClientErrorException.Forbidden e) {
            model.addAttribute("error", "Access denied: Insufficient permissions to view bookings.");
            model.addAttribute("hotels", Collections.emptyList());
            model.addAttribute("bookings", Collections.emptyList());
            // Add filter parameters even in case of error
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("status", status);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load bookings: " + e.getMessage());
            model.addAttribute("hotels", Collections.emptyList());
            model.addAttribute("bookings", Collections.emptyList());
            // Add filter parameters even in case of error
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("status", status);
        }
        return "admin/bookings";
    }

    @GetMapping("/feedback")
    public String viewFeedback(Model model, HttpServletRequest request,
                               @RequestParam(value = "hotelId", required = false) Long hotelId) {
        String token = getTokenFromCookies(request);
        if (token == null) {
            return "redirect:/login?error=Please log in to access this page";
        }
        try {
            List<HotelDTO> hotels = adminService.getAllHotels(token);
            List<FeedbackDTO> feedback = adminService.getFeedback(hotelId, token);
            model.addAttribute("hotels", hotels);
            model.addAttribute("feedback", feedback);
            // Add filter parameter to model to persist form value
            model.addAttribute("hotelId", hotelId);
        } catch (HttpClientErrorException.Forbidden e) {
            model.addAttribute("error", "Access denied: Insufficient permissions to view feedback.");
            model.addAttribute("hotels", Collections.emptyList());
            model.addAttribute("feedback", Collections.emptyList());
            // Add filter parameter even in case of error
            model.addAttribute("hotelId", hotelId);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load feedback: " + e.getMessage());
            model.addAttribute("hotels", Collections.emptyList());
            model.addAttribute("feedback", Collections.emptyList());
            // Add filter parameter even in case of error
            model.addAttribute("hotelId", hotelId);
        }
        return "admin/feedback";
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

    @Data
    public static class RoomForm {
        @NotNull(message = "Hotel ID is required")
        @Min(value = 1, message = "Hotel ID must be positive")
        private Long hotelId;

        @NotNull(message = "Floor number is required")
        @Min(value = 1, message = "Floor number must be 1 or higher")
        private Integer floorNumber;

        @NotNull(message = "Number of rooms is required")
        @Min(value = 1, message = "Number of rooms must be 1 or higher")
        private Integer numberOfRooms;

        @NotEmpty(message = "Room numbers are required")
        private List<String> roomNumber;

        @NotEmpty(message = "Room types are required")
        private List<String> type;

        @NotEmpty(message = "Room views are required")
        private List<String> view;

        @NotEmpty(message = "Room prices are required")
        private List<Double> price;

        private List<String> amenities;
    }

    @Data
    public static class RoomInput {
        private String roomNumber;
        private String type;
        private String view;
        private Double price;
        private String amenities;
    }

    @Data
    public static class RoomUpdateForm {
        @NotNull(message = "Hotel ID is required")
        @Min(value = 1, message = "Hotel ID must be positive")
        private Long hotelId;

        @NotEmpty(message = "Room number is required")
        private String roomNumber;

        @NotEmpty(message = "Room type is required")
        private String type;

        @NotEmpty(message = "Room view is required")
        private String view;

        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price must be 0 or higher")
        private Double price;

        private String amenities;

        @NotEmpty(message = "Status is required")
        private String status;

        @NotNull(message = "Floor number is required")
        @Min(value = 1, message = "Floor number must be 1 or higher")
        private Integer floor;
    }
}