package com.hotelbookingapplication.palatin.repository;

import com.hotelbookingapplication.palatin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByHotelId(Long hotelId);

    List<User> findByRoleAndHotelId(String role, Long hotelId);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.hotelId = :hotelId " +
            "AND u.id NOT IN (SELECT sr.staff.id FROM ServiceRequest sr WHERE sr.status IN ('PENDING', 'ASSIGNED') AND sr.staff.id IS NOT NULL)")
    List<User> findFreeStaffByRoleAndHotelId(@Param("role") String role, @Param("hotelId") Long hotelId);
    Optional<User> findByIdOrEmail(Long id, String email);
}