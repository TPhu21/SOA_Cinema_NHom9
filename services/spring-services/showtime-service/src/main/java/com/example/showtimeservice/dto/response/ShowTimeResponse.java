package com.example.showtimeservice.dto.response;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ShowTimeResponse {
    Long showTimeId;
    Long cinemaId;
    Long roomId;
    Long movieId;
    LocalDateTime startTime;
    LocalDateTime endTime;

    LocalDate showDate;

    BigDecimal basePrice;


}
