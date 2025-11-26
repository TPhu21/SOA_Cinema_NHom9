package com.example.servicespring.dto.response;

import com.example.servicespring.entity.ShowTime;
import com.example.servicespring.enums.BookingStatus;
import com.example.servicespring.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponse {
    String bookingId;
    BigDecimal totalPrice;
    List<String> seats;
    LocalDateTime createTime;
    LocalDateTime updateTime;
    Integer quantity;
    BookingStatus bookingStatus;
    PaymentMethod paymentMethod;
    ShowTime showTime;
}
