package com.example.bookingservice.dto.request;

import com.example.bookingservice.enums.BookingStatus;
import lombok.Data;

@Data

public class UpdateBookingStatusRequest {
    private Long bookingId;
    private BookingStatus status;
}
