package com.hotelbookingapplication.palatin.repository;

import com.hotelbookingapplication.palatin.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelIdAndStatus(Long hotelId, String status);
    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findByHotelIdAndTypeAndViewAndPriceBetweenAndFloor(
            Long hotelId, String type, String view, Double minPrice, Double maxPrice, Integer floor);

    List<Room> findByHotelId(Long hotelId);

    Optional<Room> findByHotelIdAndRoomNumber(Long hotelId, String roomNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.id = :id")
    Optional<Room> findByIdForUpdate(@Param("id") Long id);
}