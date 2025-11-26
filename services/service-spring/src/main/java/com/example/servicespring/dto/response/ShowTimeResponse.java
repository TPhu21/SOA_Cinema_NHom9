package com.example.servicespring.dto.response;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShowTimeResponse {
    Long showTimeId;
    LocalDateTime startTime;
    LocalDateTime endTime;
//    Integer seat;
}
