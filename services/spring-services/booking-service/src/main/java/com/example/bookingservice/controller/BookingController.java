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
    ResponseEntity<String> pendingBooking(@RequestBody BookingRequest request){
        try {
            // Ghi giao dịch vĩnh viễn
            BookingResponse response = bookingManagementService.createBooking(request);

            // Trả về kết quả tối thiểu để Frontend xử lý tiếp (Chỉ cần ID và Status 200)
            // Nếu bạn muốn gửi ID booking về FE, hãy dùng:
            return ResponseEntity.ok(
                    "{\"bookingId\":" + response.getBookingId() + "}"
            );

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi server khi tạo vé chờ: " + e.getMessage());
        }
    }

    //Liên quan đến node

    @PostMapping("/update-status")
// ⚠️ THAY ĐỔI: Thay ResponseEntity<BookingResponse> thành ResponseEntity<String>
    public ResponseEntity<String> updateBookingStatus(
            @RequestBody UpdateBookingStatusRequest request) {
        try {
            // Vẫn gọi Service để update, nhưng KHÔNG CẦN hứng kết quả trả về
            bookingManagementService.updateBookingStatus(
                    request.getBookingId(),
                    request.getStatus()
            );

            // Trả về HTTP 200 (OK) với một thông báo đơn giản (hoặc không cần body)
            return ResponseEntity.ok("Booking status updated successfully.");

        } catch (RuntimeException e) {
            // Giữ nguyên đoạn này để debug logic
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error updating booking status: " + e.getMessage());
        }
    }
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long bookingId) {
        // Gọi Service quản lý giao dịch
        BookingResponse response = bookingManagementService.getBooking(bookingId);
        return ResponseEntity.ok(response);
    }

}
