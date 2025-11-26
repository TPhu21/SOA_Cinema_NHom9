package com.example.servicespring.mapper;

import com.example.servicespring.dto.request.BookingRequest;
import com.example.servicespring.dto.response.BookingResponse;
import com.example.servicespring.entity.Booking;
import com.example.servicespring.entity.Cinema;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking toBookingRequest(BookingRequest request);
    BookingResponse toBookingResponse(Booking booking);
}
