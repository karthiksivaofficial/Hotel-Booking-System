//package com.hotelbookingapplication.palatin.repository;
//
//import com.hotelbookingapplication.palatin.model.CabBooking;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface CabBookingRepository extends JpaRepository<CabBooking, Long> {
//    List<CabBooking> findByUserEmail(String userEmail);
//    List<CabBooking> findByDriverEmail(String driverEmail);
//    List<CabBooking> findByHotelId(Long hotelId);
//}