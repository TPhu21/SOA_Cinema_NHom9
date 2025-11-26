package com.example.servicespring.repository;

import com.example.servicespring.entity.Booking;
import com.example.servicespring.enums.BookingStatus;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
//    @Query("SELECT b FROM Booking b JOIN FETCH b.seats s WHERE b.showTime.showId = :showId AND b.bookingStatus <> :bookingStatus")
    List<Booking> findByShowTime_ShowIdAndBookingStatusNot(Long showId, BookingStatus bookingStatus);
@Query(value = "SELECT bs.seat_code " +
        "FROM booking_seats bs " +
        "JOIN bookings b ON bs.booking_id = b.booking_id " +
        "WHERE b.show_time_id = :showId AND b.booking_status <> 'CANCELLED'",
        nativeQuery = true)
List<String> findCommittedSeatCodesNative(@Param("showId") Long showId);
}
