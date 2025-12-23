package com.example.showtimeservice.dto.response;


import com.example.showtimeservice.client.dto.CinemaResponse;
import com.example.showtimeservice.client.dto.RoomDto;
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

    /** Thông tin rạp (dùng cho response chi tiết) */
    CinemaResponse cinema;

    /** Thông tin phòng (dùng cho response chi tiết) */
    RoomDto room;
}
