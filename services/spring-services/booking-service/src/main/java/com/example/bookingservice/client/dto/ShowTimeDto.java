package com.example.bookingservice.client.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
public class ShowTimeDto {
    private Long showTimeId;
    private Long cinemaId;
    private Long roomId;
    private LocalDate showDate;
    private LocalTime startTime;
}
