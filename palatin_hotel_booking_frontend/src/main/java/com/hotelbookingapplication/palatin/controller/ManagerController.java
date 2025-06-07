package com.hotelbookingapplication.palatin.controller;

import com.hotelbookingapplication.palatin.dto.*;
import com.hotelbookingapplication.palatin.service.ManagerService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);

    @Autowired
    private ManagerService managerService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        Long hotelId = managerService.getManagerHotelId(request);
        model.addAttribute("managerHotelId", hotelId);
        return "manager/dashboard";
    }

    @GetMapping("/report")
    public String report(Model model,
                         @RequestParam(required = false) String startDate,
                         @RequestParam(required = false) String endDate,
                         @RequestParam(required = false) String reset,
                         HttpServletRequest request) {
        Long hotelId = managerService.getManagerHotelId(request);

        // Handle reset action
        if ("true".equals(reset)) {
            startDate = null;
            endDate = null;
        } else if (startDate != null && endDate != null && !startDate.trim().isEmpty() && !endDate.trim().isEmpty()) {
            try {
                ReportDTO report = managerService.getReport(LocalDate.parse(startDate), LocalDate.parse(endDate), request);
                model.addAttribute("report", report);
            } catch (HttpClientErrorException e) {
                model.addAttribute("error", "Failed to fetch report: " + e.getMessage());
            } catch (Exception e) {
                model.addAttribute("error", "Invalid date format for start or end date.");
            }
        }

        // Retain filter values
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("managerHotelId", hotelId);
        return "manager/report";
    }

    @GetMapping("/feedback")
    public String feedback(Model model, HttpServletRequest request) {
        Long hotelId = managerService.getManagerHotelId(request);
        try {
            List<FeedbackDTO> feedbacks = managerService.getFeedback(request);
            model.addAttribute("feedbacks", feedbacks);
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Failed to fetch feedback: " + e.getMessage());
        }
        model.addAttribute("managerHotelId", hotelId);
        return "manager/feedback";
    }

    @GetMapping("/staff")
    public String staffManagement(Model model, HttpServletRequest request) {
        Long hotelId = managerService.getManagerHotelId(request);
        try {
            List<UserDTO> staffList = managerService.getStaffByHotelId(hotelId, request);
            model.addAttribute("staffList", staffList);
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Failed to fetch staff list: " + e.getMessage());
        }
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("managerHotelId", hotelId);
        return "manager/staff_management";
    }

    @PostMapping("/staff")
    public String addStaff(@ModelAttribute UserDTO userDTO, HttpServletRequest request, Model model) {
        Long hotelId = managerService.getManagerHotelId(request);
        userDTO.setHotelId(hotelId);
        try {
            managerService.addStaff(userDTO, request);
            return "redirect:/manager/staff?success";
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Failed to add staff: " + e.getMessage());
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("managerHotelId", hotelId);
            try {
                List<UserDTO> staffList = managerService.getStaffByHotelId(hotelId, request);
                model.addAttribute("staffList", staffList);
            } catch (HttpClientErrorException ex) {
                model.addAttribute("error", "Failed to fetch staff list: " + ex.getMessage());
            }
            return "manager/staff_management";
        }
    }

    @PostMapping("/staff/deactivate-by-email")
    public String deactivateStaffByEmail(@RequestParam String email, HttpServletRequest request, Model model) {
        Long hotelId = managerService.getManagerHotelId(request);
        try {
            managerService.deactivateStaffByEmail(email, request);
            return "redirect:/manager/staff?success";
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Failed to deactivate staff: " + e.getMessage());
            model.addAttribute("userDTO", new UserDTO());
            model.addAttribute("managerHotelId", hotelId);
            try {
                List<UserDTO> staffList = managerService.getStaffByHotelId(hotelId, request);
                model.addAttribute("staffList", staffList);
            } catch (HttpClientErrorException ex) {
                model.addAttribute("error", "Failed to fetch staff list: " + ex.getMessage());
            }
            return "manager/staff_management";
        }
    }

    @PostMapping("/staff/status/{id}")
    public String updateStaffStatus(@PathVariable Long id, @RequestParam boolean active, HttpServletRequest request, Model model) {
        Long hotelId = managerService.getManagerHotelId(request);
        try {
            managerService.updateStaffStatus(id, active, request);
            return "redirect:/manager/staff?success";
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Failed to update staff status: " + e.getMessage());
            model.addAttribute("userDTO", new UserDTO());
            model.addAttribute("managerHotelId", hotelId);
            try {
                List<UserDTO> staffList = managerService.getStaffByHotelId(hotelId, request);
                model.addAttribute("staffList", staffList);
            } catch (HttpClientErrorException ex) {
                model.addAttribute("error", "Failed to fetch staff list: " + ex.getMessage());
            }
            return "manager/staff_management";
        }
    }

    @GetMapping("/rooms")
    public String roomManagement(Model model, HttpServletRequest request) {
        Long hotelId = managerService.getManagerHotelId(request);
        List<RoomDTO> rooms = managerService.getRoomsByHotelId(hotelId, request);
        model.addAttribute("rooms", rooms);
        model.addAttribute("managerHotelId", hotelId);
        return "manager/room_management";
    }

    @PostMapping("/rooms/status")
    public String updateRoomStatus(@RequestParam String roomNumber, @RequestParam String status, HttpServletRequest request, Model model) {
        Long hotelId = managerService.getManagerHotelId(request);
        try {
            managerService.updateRoomStatusByRoomNumber(roomNumber, hotelId, status, request);
            return "redirect:/manager/rooms?success";
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Failed to update room status: " + e.getMessage());
            model.addAttribute("rooms", managerService.getRoomsByHotelId(hotelId, request));
            model.addAttribute("managerHotelId", hotelId);
            return "manager/room_management";
        }
    }

    @GetMapping("/room-floor")
    public String roomFloorManagement(Model model, HttpServletRequest request) {
        Long hotelId = managerService.getManagerHotelId(request);
        List<RoomDTO> rooms = managerService.getRoomsByHotelId(hotelId, request);
        long totalFloors = rooms.stream()
                .map(RoomDTO::getFloor)
                .filter(floor -> floor != null)
                .distinct()
                .count();
        model.addAttribute("rooms", rooms);
        model.addAttribute("totalFloors", totalFloors);
        model.addAttribute("managerHotelId", hotelId);
        model.addAttribute("roomDTO", new RoomDTO());
        return "manager/room_floor_management";
    }

    @PostMapping("/hotels/{hotelId}/floors")
    public String addFloor(@PathVariable Long hotelId, @ModelAttribute FloorFormDTO floorForm, HttpServletRequest request, Model model) {
        try {
            managerService.addFloor(hotelId, floorForm.getFloorNumber(), floorForm.getRooms(), request);
            return "redirect:/manager/room-floor?success";
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Failed to add floor/rooms: " + e.getMessage());
            List<RoomDTO> roomsList = managerService.getRoomsByHotelId(hotelId, request);
            long totalFloors = roomsList.stream()
                    .map(RoomDTO::getFloor)
                    .filter(floor -> floor != null)
                    .distinct()
                    .count();
            model.addAttribute("rooms", roomsList);
            model.addAttribute("totalFloors", totalFloors);
            model.addAttribute("managerHotelId", hotelId);
            model.addAttribute("roomDTO", new RoomDTO());
            return "manager/room_floor_management";
        }
    }

    @PostMapping("/rooms/hotel/{hotelId}/room/{roomNumber:.+}")
    public String updateRoomByHotelIdAndRoomNumber(
            @PathVariable Long hotelId,
            @PathVariable String roomNumber,
            @ModelAttribute RoomDTO roomDTO,
            HttpServletRequest request,
            Model model) {
        if (roomNumber == null || roomNumber.trim().isEmpty() || "placeholder".equals(roomNumber)) {
            model.addAttribute("error", "Room Number cannot be empty.");
            Long managerHotelId = managerService.getManagerHotelId(request);
            List<RoomDTO> rooms = managerService.getRoomsByHotelId(managerHotelId, request);
            long totalFloors = rooms.stream()
                    .map(RoomDTO::getFloor)
                    .filter(floor -> floor != null)
                    .distinct()
                    .count();
            model.addAttribute("rooms", rooms);
            model.addAttribute("totalFloors", totalFloors);
            model.addAttribute("managerHotelId", managerHotelId);
            model.addAttribute("roomDTO", roomDTO);
            return "manager/room_floor_management";
        }
        Long managerHotelId = managerService.getManagerHotelId(request);
        if (!hotelId.equals(managerHotelId)) {
            model.addAttribute("error", "You are not authorized to update rooms for this hotel.");
            List<RoomDTO> rooms = managerService.getRoomsByHotelId(managerHotelId, request);
            long totalFloors = rooms.stream()
                    .map(RoomDTO::getFloor)
                    .filter(floor -> floor != null)
                    .distinct()
                    .count();
            model.addAttribute("rooms", rooms);
            model.addAttribute("totalFloors", totalFloors);
            model.addAttribute("managerHotelId", managerHotelId);
            model.addAttribute("roomDTO", roomDTO);
            return "manager/room_floor_management";
        }
        try {
            managerService.updateRoomByHotelIdAndRoomNumber(hotelId, roomNumber, roomDTO, request);
            return "redirect:/manager/room-floor?success";
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Failed to update room: " + e.getMessage());
            List<RoomDTO> rooms = managerService.getRoomsByHotelId(hotelId, request);
            long totalFloors = rooms.stream()
                    .map(RoomDTO::getFloor)
                    .filter(floor -> floor != null)
                    .distinct()
                    .count();
            model.addAttribute("rooms", rooms);
            model.addAttribute("totalFloors", totalFloors);
            model.addAttribute("managerHotelId", hotelId);
            model.addAttribute("roomDTO", roomDTO);
            return "manager/room_floor_management";
        }
    }

    @GetMapping("/service-requests")
    public String serviceRequests(Model model, HttpServletRequest request) {
        Long hotelId = managerService.getManagerHotelId(request);
        try {
            List<ServiceRequestDTO> requests = managerService.getServiceRequestsByHotelId(hotelId, request);
            List<UserDTO> staffs = managerService.getStaffByHotelId(hotelId, request);
            model.addAttribute("requests", requests);
            model.addAttribute("staffs", staffs);
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Failed to fetch service requests or staff: " + e.getMessage());
        }
        model.addAttribute("managerHotelId", hotelId);
        return "manager/service_requests";
    }

    @PostMapping("/service-requests/assign/{id}")
    public String assignServiceRequest(@PathVariable Long id, @RequestParam Long staffId, HttpServletRequest request, Model model) {
        try {
            managerService.assignServiceRequest(id, staffId, request);
            return "redirect:/manager/service-requests?success";
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Failed to assign service request: " + e.getMessage());
            Long hotelId = managerService.getManagerHotelId(request);
            model.addAttribute("requests", managerService.getServiceRequestsByHotelId(hotelId, request));
            model.addAttribute("staffs", managerService.getStaffByHotelId(hotelId, request));
            model.addAttribute("managerHotelId", hotelId);
            return "manager/service_requests";
        }
    }

    @PostMapping("/service-requests/complete/{id}")
    public String completeServiceRequest(@PathVariable Long id, HttpServletRequest request, Model model) {
        try {
            managerService.completeServiceRequest(id, request);
            return "redirect:/manager/service-requests?success";
        } catch (HttpClientErrorException e) {
            model.addAttribute("error", "Failed to complete service request: " + e.getMessage());
            Long hotelId = managerService.getManagerHotelId(request);
            model.addAttribute("requests", managerService.getServiceRequestsByHotelId(hotelId, request));
            model.addAttribute("staffs", managerService.getStaffByHotelId(hotelId, request));
            model.addAttribute("managerHotelId", hotelId);
            return "manager/service_requests";
        }
    }

    @GetMapping("/bookings")
    public String bookings(Model model,
                           @RequestParam(required = false) String startDate,
                           @RequestParam(required = false) String endDate,
                           @RequestParam(required = false) String status,
                           @RequestParam(required = false) String reset,
                           HttpServletRequest request) {
        Long hotelId = managerService.getManagerHotelId(request);
        LocalDate parsedStartDate = null;
        LocalDate parsedEndDate = null;

        // Handle reset action
        if ("true".equals(reset)) {
            startDate = null;
            endDate = null;
            status = null;
        } else {
            // Parse dates only if provided and non-empty
            try {
                if (startDate != null && !startDate.trim().isEmpty()) {
                    parsedStartDate = LocalDate.parse(startDate);
                }
                if (endDate != null && !endDate.trim().isEmpty()) {
                    parsedEndDate = LocalDate.parse(endDate);
                }
            } catch (Exception e) {
                logger.error("Invalid date format - startDate: {}, endDate: {}", startDate, endDate, e);
                model.addAttribute("error", "Invalid date format for start or end date.");
            }
        }

        try {
            List<BookingDTO> bookings = managerService.getBookingsByHotelWithFilters(
                    hotelId, parsedStartDate, parsedEndDate, status, request
            );
            logger.info("Fetched {} bookings for hotelId: {}", bookings.size(), hotelId);

            // Calculate monthly revenue for the bar graph
            double[] monthlyRevenue = new double[12];
            for (BookingDTO booking : bookings) {
                if (booking != null &&
                        booking.getPaymentStatus() != null &&
                        "PAID".equals(booking.getPaymentStatus()) &&
                        booking.getCheckInDate() != null) {
                    int month = booking.getCheckInDate().getMonthValue() - 1; // 0-based index for months
                    monthlyRevenue[month] += booking.getTotalPrice();
                } else {
                    logger.warn("Skipping invalid booking: paymentStatus={}, checkInDate={}",
                            booking != null ? booking.getPaymentStatus() : null,
                            booking != null ? booking.getCheckInDate() : null);
                }
            }
            double totalRevenue = 0;
            for (double revenue : monthlyRevenue) {
                totalRevenue += revenue;
            }
            logger.info("Monthly Revenue: {}", Arrays.toString(monthlyRevenue));
            logger.info("Total Revenue: {}", totalRevenue);

            model.addAttribute("bookings", bookings);
            model.addAttribute("monthlyRevenue", monthlyRevenue);
            model.addAttribute("totalRevenue", totalRevenue);
            // Retain filter values
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("status", status);
        } catch (HttpClientErrorException e) {
            logger.error("Failed to fetch bookings: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to fetch bookings: " + e.getMessage());
            model.addAttribute("monthlyRevenue", new double[12]); // Fallback empty array
            model.addAttribute("totalRevenue", 0);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching bookings: {}", e.getMessage(), e);
            model.addAttribute("error", "Unexpected error: " + e.getMessage());
            model.addAttribute("monthlyRevenue", new double[12]); // Fallback empty array
            model.addAttribute("totalRevenue", 0);
        }
        model.addAttribute("managerHotelId", hotelId);
        return "manager/bookings";
    }
}