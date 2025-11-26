package com.example.servicespring.entity;

import com.example.servicespring.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Fetch;

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
    Long showId;
    LocalDate createdAt;
    LocalDateTime updatedAt;
    LocalDateTime startTime;
    LocalDateTime endTime;
//    Integer seat;  Dùng cho việc thống kê số lượng ghế bán được

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "movieId")  // Chi ra foreign_key
    Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinemaId")
    Cinema cinema;

    @OneToMany(mappedBy = "showTime" , cascade = CascadeType.ALL)
    List<Booking> bookingList;



}
