package com.hotelbookingapplication.palatin.repository;

import com.hotelbookingapplication.palatin.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserEmail(String email);

    List<Booking> findByHotelId(Long hotelId);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
            "AND (b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn)")
    List<Booking> findBookingsByRoomIdAndDateRange(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
            "AND b.checkInDate <= :date AND b.checkOutDate >= :date " +
            "AND b.status NOT IN ('CANCELLED', 'CHECKED_OUT')")
    Optional<Booking> findActiveBookingByRoomId(
            @Param("roomId") Long roomId,
            @Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.checkInDate = :date " +
            "AND b.status IN ('CONFIRMED', 'CHECKED_IN')")
    List<Booking> findByCheckInDate(@Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.hotel.id = :hotelId " +
            "AND b.checkInDate <= :endDate AND b.checkOutDate >= :startDate " +
            "AND b.status NOT IN ('CANCELLED', 'CHECKED_OUT')")
    List<Booking> findBookingsByHotelIdAndDateRange(
            @Param("hotelId") Long hotelId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM Booking b WHERE b.room.roomNumber = :roomNumber AND b.hotel.id = :hotelId")
    Optional<Booking> findByRoomNumberAndHotelId(@Param("roomNumber") String roomNumber, @Param("hotelId") Long hotelId);

    @Query("SELECT b FROM Booking b WHERE b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    List<Booking> findByDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT b FROM Booking b WHERE b.hotel.id = :hotelId " +
            "AND (:startDate IS NULL OR b.checkInDate >= :startDate) " +
            "AND (:endDate IS NULL OR b.checkOutDate <= :endDate) " +
            "AND (:status IS NULL OR b.status = :status)")
    List<Booking> findBookingsByHotelIdWithFilters(
            @Param("hotelId") Long hotelId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status);
}