package com.example.bookingservice.dto.response;

import com.example.bookingservice.enums.BookingStatus;
import com.example.bookingservice.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class BookingResponse {
    String bookingId;
    Long showTimeId;
    String userId;
    BookingStatus bookingStatus;
    List<String> seatCodes;
    Integer quantity;
    PaymentMethod paymentMethod;
    LocalDateTime createTime;
    BigDecimal totalPrice;


}
