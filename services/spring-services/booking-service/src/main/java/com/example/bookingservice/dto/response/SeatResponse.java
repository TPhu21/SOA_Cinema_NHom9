package com.example.bookingservice.dto.response;

import com.example.bookingservice.enums.SeatStatus;
import com.example.bookingservice.enums.SeatType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
public class SeatResponse {
    String seatCode;
    String rowLabel;
    Integer seatNumber;
    SeatType seatType;
    SeatStatus status;
    BigDecimal price;
}
