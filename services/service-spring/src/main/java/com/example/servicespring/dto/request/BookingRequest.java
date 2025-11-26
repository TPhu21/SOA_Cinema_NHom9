package com.example.servicespring.dto.request;

import com.example.servicespring.entity.ShowTime;
import com.example.servicespring.entity.User;
import com.example.servicespring.enums.BookingStatus;
import com.example.servicespring.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingRequest {
    BigDecimal totalPrice;
    List<String> seats;
    PaymentMethod paymentMethod;
    Long showTimeId;
    String userId;
}
