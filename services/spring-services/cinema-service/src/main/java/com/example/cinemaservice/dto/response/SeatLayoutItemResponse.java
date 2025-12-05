package com.example.cinemaservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatLayoutItemResponse {
    String seatCode;
    String rowLabel;
    Integer seatNumber;
    String seatType;
}
