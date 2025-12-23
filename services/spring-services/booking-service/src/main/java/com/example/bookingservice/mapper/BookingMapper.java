package com.example.bookingservice.mapper;


import com.example.bookingservice.dto.request.BookingRequest;
import com.example.bookingservice.dto.response.BookingResponse;
import com.example.bookingservice.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking toBookingRequest(BookingRequest request);
    
    @Mapping(source = "seats", target = "seatCodes")
    @Mapping(source = "bookingCode", target = "bookingCode")
    @Mapping(source = "checkInTime", target = "checkInTime")
    BookingResponse toBookingResponse(Booking booking);
}
