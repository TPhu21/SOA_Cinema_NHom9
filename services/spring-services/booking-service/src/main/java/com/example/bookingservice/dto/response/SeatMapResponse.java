package com.example.bookingservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class SeatMapResponse {
    Long showTimeId;
    Long cinemaId;
    Long roomId;
    Integer rowCount;
    Integer colCount;
    List<SeatResponse> seats;
}
