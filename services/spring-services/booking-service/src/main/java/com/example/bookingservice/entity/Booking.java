package com.example.bookingservice.entity;


import com.example.bookingservice.enums.BookingStatus;
import com.example.bookingservice.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY )
    Long bookingId;
    
    /**
     * Mã vé duy nhất để kiểm vé (VD: BK20241220001)
     * Staff sử dụng mã này để scan/nhập khi khách đến rạp
     */
    @Column(unique = true, length = 20)
    String bookingCode;
    
    String userId;
    Long showTimeId;
    BigDecimal totalPrice;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "booking_seats", joinColumns = @JoinColumn(name = "booking_id", nullable = false))
    @Column(name =  "seat_code", nullable = false)// Giá trị của List<>
    private List<String> seats = new ArrayList<>();
    LocalDateTime createTime;
    LocalDateTime updateTime;
    
    /** Thời điểm Staff check-in (kiểm vé) */
    LocalDateTime checkInTime;
    
    Integer quantity;

    @Enumerated (EnumType.STRING)
    BookingStatus bookingStatus;
    @Enumerated (EnumType.STRING)
    PaymentMethod paymentMethod;

    @PrePersist
    void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = this.createTime;
        if (this.bookingStatus == null) {
            this.bookingStatus = BookingStatus.PENDING;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }


}
