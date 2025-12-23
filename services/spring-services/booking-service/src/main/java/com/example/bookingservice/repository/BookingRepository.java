package com.example.bookingservice.repository;

import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.enums.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Tìm booking theo mã vé (bookingCode)
     * Dùng cho Staff kiểm vé
     */
    Optional<Booking> findByBookingCode(String bookingCode);
    
//    @Query("SELECT b FROM Booking b JOIN FETCH b.seats s WHERE b.showTime.showId = :showId AND b.bookingStatus <> :bookingStatus")
    List<Booking> findByShowTimeIdAndBookingStatusNot(Long showId, BookingStatus bookingStatus);

    List<Booking> findByShowTimeIdAndBookingStatus(Long showId, BookingStatus bookingStatus);

    List<Booking> findByBookingStatusAndCreateTimeBefore(BookingStatus bookingStatus, LocalDateTime cutOffTime);
    
    // ============================================================
    // ANALYTICS QUERIES
    // ============================================================
    
    /**
     * Daily Sales - Doanh thu theo ngày
     */
    interface DailySalesRow {
        java.sql.Date getDay();
        BigDecimal getRevenue();
        Long getTicketsSold();
        Long getOrders();
    }

    @Query(value = """
        SELECT DATE(b.update_time) AS day,
               COALESCE(SUM(b.total_price), 0) AS revenue,
               COALESCE(SUM(b.quantity), 0) AS ticketsSold,
               COUNT(b.booking_id) AS orders
        FROM bookings b
        WHERE b.booking_status = 'CONFIRMED'
          AND b.update_time >= :fromTime
          AND b.update_time < :toTime
        GROUP BY DATE(b.update_time)
        ORDER BY day
        """, nativeQuery = true)
    List<DailySalesRow> sumDailySales(
            @Param("fromTime") LocalDateTime fromTime,
            @Param("toTime") LocalDateTime toTime
    );
    
    /**
     * KPI Statistics - Tổng quan
     */
    @Query(value = """
        SELECT 
            COALESCE(SUM(b.total_price), 0) AS totalRevenue,
            COALESCE(SUM(b.quantity), 0) AS totalTickets,
            COUNT(b.booking_id) AS totalBookings,
            COUNT(CASE WHEN b.booking_status = 'USED' THEN 1 END) AS checkedIn,
            COUNT(CASE WHEN b.booking_status = 'CANCELLED' THEN 1 END) AS cancelled
        FROM bookings b
        WHERE b.booking_status IN ('CONFIRMED', 'USED', 'CANCELLED')
          AND b.create_time >= :fromTime
          AND b.create_time < :toTime
        """, nativeQuery = true)
    KPIStatsRow getKPIStats(
            @Param("fromTime") LocalDateTime fromTime,
            @Param("toTime") LocalDateTime toTime
    );
    
    interface KPIStatsRow {
        BigDecimal getTotalRevenue();
        Long getTotalTickets();
        Long getTotalBookings();
        Long getCheckedIn();
        Long getCancelled();
    }
    
    /**
     * Payment Method Distribution - Phân bố theo phương thức thanh toán
     */
    @Query(value = """
        SELECT 
            b.payment_method AS paymentMethod,
            COUNT(b.booking_id) AS transactionCount,
            COALESCE(SUM(b.total_price), 0) AS revenue
        FROM bookings b
        WHERE b.booking_status = 'CONFIRMED'
          AND b.create_time >= :fromTime
          AND b.create_time < :toTime
        GROUP BY b.payment_method
        ORDER BY revenue DESC
        """, nativeQuery = true)
    List<PaymentMethodStatsRow> getPaymentMethodDistribution(
            @Param("fromTime") LocalDateTime fromTime,
            @Param("toTime") LocalDateTime toTime
    );
    
    interface PaymentMethodStatsRow {
        String getPaymentMethod();
        Long getTransactionCount();
        BigDecimal getRevenue();
    }
    
    /**
     * Top Movies - Top phim bán chạy nhất
     * 
     * Trả về showtime stats, cần join với showtime-service để lấy movieId
     */
    @Query(value = """
        SELECT 
            b.show_time_id AS showTimeId,
            COUNT(b.booking_id) AS ticketsSold,
            COALESCE(SUM(b.total_price), 0) AS revenue,
            COUNT(DISTINCT b.booking_id) AS showtimeCount
        FROM bookings b
        WHERE b.booking_status IN ('CONFIRMED', 'USED')
          AND b.create_time >= :fromTime
          AND b.create_time < :toTime
        GROUP BY b.show_time_id
        ORDER BY revenue DESC
        """, nativeQuery = true)
    List<ShowTimeStatsRow> getTopShowTimes(
            @Param("fromTime") LocalDateTime fromTime,
            @Param("toTime") LocalDateTime toTime
    );
    
    interface ShowTimeStatsRow {
        Long getShowTimeId();
        Long getTicketsSold();
        BigDecimal getRevenue();
        Long getShowtimeCount();
    }
}
