package com.example.bookingservice.repository;

import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.enums.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
//    @Query("SELECT b FROM Booking b JOIN FETCH b.seats s WHERE b.showTime.showId = :showId AND b.bookingStatus <> :bookingStatus")
    List<Booking> findByShowTimeIdAndBookingStatusNot(Long showId, BookingStatus bookingStatus);

    List<Booking> findByShowTimeIdAndBookingStatus(Long showId, BookingStatus bookingStatus);
}
