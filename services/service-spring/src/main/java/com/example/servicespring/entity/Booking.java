package com.example.servicespring.entity;

import com.example.servicespring.enums.BookingStatus;
import com.example.servicespring.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table (name = "bookings")
@Data
@Builder
@FieldDefaults (level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY )
    Long bookingId;
    BigDecimal totalPrice;

    @ElementCollection // Tạo bảng mới độc lập trong DB
    @CollectionTable (name = "booking_seats", joinColumns = @JoinColumn(name = "bookingId"))// Đặt tên và thiết lập FK cho bảng này
    @Column(name =  "seat_code")// Giá trị của List<>
    List<String>  seats;
    LocalDateTime createTime;
    LocalDateTime updateTime;
    Integer quantity;

    @Enumerated (EnumType.STRING)
    BookingStatus bookingStatus;
    @Enumerated (EnumType.STRING)
    PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showTimeId")
    ShowTime showTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    User user;
}
