package com.example.servicespring.dto.response;

import com.example.servicespring.enums.SeatStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatMapResponse {
    Long showId;
    int rowCount;
    int columnCount;
    List<SeatResponse>  seats;
}
