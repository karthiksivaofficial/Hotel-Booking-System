package com.hotelbookingapplication.palatin.repository;

import com.hotelbookingapplication.palatin.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByUserEmail(String email);

    List<ServiceRequest> findByHotelId(Long hotelId);

    // Replaced findByAssignedStaffEmail with a query for staff.email
    List<ServiceRequest> findByStaffEmail(String email);
    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.hotel.id = :hotelId AND sr.serviceType = 'LAUNDRY' AND sr.status = :status")
    List<ServiceRequest> findLaundryRequestsByHotelIdAndStatus(@Param("hotelId") Long hotelId, @Param("status") String status);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.hotel.id = :hotelId AND sr.serviceType = 'LAUNDRY'")
    List<ServiceRequest> findAllLaundryRequestsByHotelId(@Param("hotelId") Long hotelId);
}