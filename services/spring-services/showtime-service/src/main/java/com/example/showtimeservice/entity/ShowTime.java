package com.example.showtimeservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table (name = "showtimes")
@Data
@FieldDefaults (level = AccessLevel.PRIVATE)
public class ShowTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long showTimeId;

    @Column(nullable = false)
    Long movieId;
    @Column(nullable = false)
    Long cinemaId;
    @Column( nullable = false)
    Long roomId; // Để fe vẽ ghế

    LocalDateTime startTime;
    LocalDateTime endTime;

    BigDecimal basePrice;

    LocalDate showDate;

//    BigDecimal discountPrice;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
//    Integer seat;  Dùng cho việc thống kê số lượng ghế bán được



    // Logic: Khi tạo mới, set createdAt
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }



}
