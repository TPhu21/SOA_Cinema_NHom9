package com.example.cinemaservice.dto.response;

import lombok.Data;

@Data
public class CinemaResponse {
    Long cinemaId;
    String cinemaName;
    String cinemaAddress;
    String cinemaCity;


    Integer rowCount;
    Integer colCount;
}
