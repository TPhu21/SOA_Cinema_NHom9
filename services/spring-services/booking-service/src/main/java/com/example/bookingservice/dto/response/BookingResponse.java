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
    
    /** Mã vé để kiểm vé (VD: BK20241220001) */
    String bookingCode;
    
    Long showTimeId;
    String userId;
    BookingStatus bookingStatus;
    List<String> seatCodes;
    Integer quantity;
    PaymentMethod paymentMethod;
    LocalDateTime createTime;
    LocalDateTime checkInTime;
    BigDecimal totalPrice;
}
