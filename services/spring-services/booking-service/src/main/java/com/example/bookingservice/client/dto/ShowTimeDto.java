package com.example.bookingservice.client.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ShowTimeDto {
    private Long showTimeId;
    private Long cinemaId;
    private Long roomId;
    private Long movieId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate showDate;
    private BigDecimal basePrice;
}
