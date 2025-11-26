package com.example.servicespring.dto.response;

import lombok.Data;

@Data
public class CinemaResponse {
    Long cinemaId;
    String cinemaName;
    String cinemaAddress;
    String cinemaCity;
}
