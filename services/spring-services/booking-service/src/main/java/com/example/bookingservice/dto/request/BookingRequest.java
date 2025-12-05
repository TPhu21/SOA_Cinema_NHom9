package com.example.bookingservice.dto.request;


import com.example.bookingservice.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingRequest {
    BigDecimal totalPrice;
    List<String> seatCodes;
    PaymentMethod paymentMethod;
    Long showTimeId;
    String userId;
}
