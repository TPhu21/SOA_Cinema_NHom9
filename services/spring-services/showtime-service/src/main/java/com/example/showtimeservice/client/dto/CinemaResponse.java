package com.example.showtimeservice.client.dto;

import lombok.Data;

/**
 * CinemaResponse - DTO Rạp chiếu từ Cinema Service
 * 
 * Chứa thông tin chi tiết của một rạp chiếu
 * 
 * @author Admin
 */
@Data
public class CinemaResponse {
    Long cinemaId;
    String cinemaName;
    String cinemaAddress;
}
