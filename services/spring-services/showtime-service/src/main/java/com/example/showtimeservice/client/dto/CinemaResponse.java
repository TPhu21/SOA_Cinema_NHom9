package com.example.showtimeservice.client.dto;

import lombok.Data;

@Data
public class CinemaResponse {
    Long cinemaId;
    String cinemaName;
    String cinemaAddress;
}
