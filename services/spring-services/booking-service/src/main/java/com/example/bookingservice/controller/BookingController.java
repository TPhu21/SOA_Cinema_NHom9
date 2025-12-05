package com.example.bookingservice.controller;


import com.example.bookingservice.dto.request.BookingRequest;
import com.example.bookingservice.dto.request.UpdateBookingStatusRequest;
import com.example.bookingservice.dto.response.BookingResponse;
import com.example.bookingservice.dto.response.SeatMapResponse;
import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.service.BookingManagementService;
import com.example.bookingservice.service.ReservationService;
import com.example.bookingservice.service.SeatMapService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class BookingController {
    SeatMapService seatMapService;
    ReservationService reservationService;
    BookingManagementService bookingManagementService;

    // 1. Lấy seatmap (gồm status + price) cho FE vẽ
    @GetMapping("/seat-map")
//    public SeatMapResponse getSeatMap(@RequestParam Long showTimeId) {
//
//        return seatMapService.getSeatMap(showTimeId);
//    }
    public ResponseEntity<?> getSeatMap(@RequestParam("showTimeId") Long showTimeId) {
        try {
            SeatMapResponse response = seatMapService.getSeatMap(showTimeId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // log full stacktrace trong console booking-service

            return ResponseEntity.status(500).body(
                    Map.of(
                            "message", "Lỗi server khi load sơ đồ ghế",
                            "detail", e.getMessage()
                    )
            );
        }
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
    @PostMapping("/pending")
    ResponseEntity<?> pendingBooking(@RequestBody BookingRequest request){
        try {
            //ghi giao dịch vĩnh viễn
            BookingResponse response = bookingManagementService.createBooking(request);

            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            // Bắt lỗi hết hạn giữ chỗ hoặc lỗi thanh toán
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Liên quan đến node

    @PostMapping("/update-status")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @RequestBody UpdateBookingStatusRequest request) { // DTO: bookingId và status mới
        try {
            BookingResponse response = bookingManagementService.updateBookingStatus(
                    request.getBookingId(),
                    request.getStatus()
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Log lỗi chi tiết tại đây
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long bookingId) {
        // Gọi Service quản lý giao dịch
        BookingResponse response = bookingManagementService.getBooking(bookingId);
        return ResponseEntity.ok(response);
    }

}
