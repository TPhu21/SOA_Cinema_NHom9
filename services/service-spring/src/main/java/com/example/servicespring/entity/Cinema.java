package com.example.servicespring.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cinemas")
@Data
@FieldDefaults (level = AccessLevel.PRIVATE)
public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long cinemaId;
    String cinemaName;
    String cinemaAddress;
    String cinemaCity;
    String cinemaBrand;
    String posterUrl;

    Integer rowCount;
    Integer colCount;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL)
    List<ShowTime> showTimeList;

}
