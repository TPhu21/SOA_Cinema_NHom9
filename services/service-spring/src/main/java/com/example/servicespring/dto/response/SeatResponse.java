package com.example.servicespring.dto.response;

import com.example.servicespring.enums.SeatStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatResponse {
    String seatCode;
    String row;
    Integer column;
    SeatStatus status;
    BigDecimal price;
}
