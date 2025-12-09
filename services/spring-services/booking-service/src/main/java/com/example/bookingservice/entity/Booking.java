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
@Table (name = "bookings")
@Data
@Builder // để tạo builder mới khi cần@FieldDefaults (level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY )
    Long bookingId;
    String userId;
    Long showTimeId;
    BigDecimal totalPrice;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "booking_seats", joinColumns = @JoinColumn(name = "booking_id", nullable = false))
    @Column(name =  "seat_code", nullable = false)// Giá trị của List<>
    private List<String> seats = new ArrayList<>();
    LocalDateTime createTime;
    LocalDateTime updateTime;
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
