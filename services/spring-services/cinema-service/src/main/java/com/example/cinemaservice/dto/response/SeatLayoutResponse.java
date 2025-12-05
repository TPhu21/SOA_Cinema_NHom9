package com.example.cinemaservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder

public class SeatLayoutResponse {
    Long roomId;
    String roomName;
    String cinemaName;

    Integer rowCount;
    Integer colCount;

    List<SeatLayoutItemResponse> seats;
}
