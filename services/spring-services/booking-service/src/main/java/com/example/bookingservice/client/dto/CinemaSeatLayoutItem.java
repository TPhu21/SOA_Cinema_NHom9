package com.example.bookingservice.client.dto;

import com.example.bookingservice.enums.SeatStatus;
import com.example.bookingservice.enums.SeatType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CinemaSeatLayoutItem {
    private String seatCode;
    private String rowLabel;
    private Integer seatNumber;
    private SeatType seatType;
    private SeatStatus seatStatus;
}
