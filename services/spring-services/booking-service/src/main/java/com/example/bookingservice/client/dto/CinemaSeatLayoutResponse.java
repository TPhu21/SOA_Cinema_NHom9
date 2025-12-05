package com.example.bookingservice.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CinemaSeatLayoutResponse {
    private Long cinemaId;
    private String cinemaName;
    private String cinemaAddress;
    private String cinemaCity;
    private Integer rowCount;
    private Integer colCount;
    private List<CinemaSeatLayoutItem> seats;
}
