package com.example.bookingservice.mapper;


import com.example.bookingservice.dto.request.BookingRequest;
import com.example.bookingservice.dto.response.BookingResponse;
import com.example.bookingservice.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking toBookingRequest(BookingRequest request);
    BookingResponse toBookingResponse(Booking booking);
}
