package com.example.bookingservice.dto.request;


import com.example.bookingservice.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Builder
@Data
public class BookingRequest {
    BigDecimal totalPrice;
    @Builder.Default
    List<String> seatCodes = new ArrayList<>();
    PaymentMethod paymentMethod;
    Long showTimeId;
    String userId;
}
