package com.example.cinemaservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CinemaResponse {
    private Long cinemaId;
    private String cinemaName;
    private String cinemaAddress;
    private String cinemaCity;
    private String cinemaBrand;
    private String posterUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
