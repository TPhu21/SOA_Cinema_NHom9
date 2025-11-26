package com.example.servicespring.controller;

import com.example.servicespring.dto.request.BookingRequest;
import com.example.servicespring.dto.response.SeatMapResponse;
import com.example.servicespring.entity.Booking;
import com.example.servicespring.service.ConfirmBookingService;
import com.example.servicespring.service.ReservationService;
import com.example.servicespring.service.SeatMapService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class BookingController {
    SeatMapService seatMapService;
    ReservationService reservationService;
    ConfirmBookingService confirmBookingService;

    // Lấy sơ đồ ghế
    @GetMapping("/showtime/{showId}/seats")
    SeatMapResponse getAllSeats(@PathVariable("showId") Long showId){
        return seatMapService.getSeatMap(showId);
    }

    //HOld seat
    @PostMapping("/hold")
    ResponseEntity holdSeats(@RequestBody Map<String, Object> payload){
        // Trích xuất và transfer dât từ JSON BODY

        Long showId = Long.valueOf(payload.get("showId").toString());
        String userId =  payload.get("userId").toString();
        @SuppressWarnings("unchecked")
        List<String> seats = (List<String>) payload.get("seats");

        try{
            //Điều phối đến reservation để lock ghế
            String reservationToken = reservationService.holdSeats(showId, seats, userId);

            return ResponseEntity.ok(java.util.Map.<String, Object>of(
                    "message", "Giữ chỗ thành công. Vui lòng thanh toán trong 10 phút.",
                    "reservationToken", reservationToken));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());        }

    }

    //Confirm
    @PostMapping("/confirm")
    ResponseEntity confirmBooking(@RequestBody BookingRequest request){
        try {
            //ghi giao dịch vĩnh viễn
            Booking booking = confirmBookingService.confirmBooking(request);

            return ResponseEntity.ok(booking);
        } catch (IllegalStateException e) {
            // Bắt lỗi hết hạn giữ chỗ hoặc lỗi thanh toán
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
