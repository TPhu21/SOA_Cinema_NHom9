package com.example.bookingservice.service;


import com.example.bookingservice.dto.request.BookingRequest;
import com.example.bookingservice.dto.response.BookingResponse;
import com.example.bookingservice.dto.response.SeatMapResponse;
import com.example.bookingservice.dto.response.SeatResponse;
import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.enums.BookingStatus;
import com.example.bookingservice.mapper.BookingMapper;
import com.example.bookingservice.repository.BookingRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//Logic ghi vĩnh viễn vào SQL
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingManagementService {
    BookingRepository bookingRepository;
    SeatMapService seatMapService;
    StringRedisTemplate redisTemplate;
    BookingMapper bookingMapper;

    static String REDIS_HOLD_PREFIX = "ticket:hold:";

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        Long showId = request.getShowTimeId();
        String userId = request.getUserId();
        List<String> seats = request.getSeatCodes();

        // 1. Validate
        if (seats == null || seats.isEmpty()) {
            throw new IllegalStateException("Vui lòng chọn ít nhất một ghế.");
        }

        // 2. Check quyền sở hữu trong Redis (Re-entrant check)
        for(String seat : seats){
            String key = REDIS_HOLD_PREFIX + showId + ":" + seat;
            String hoderValue = redisTemplate.opsForValue().get(key);
            // Nếu không có key hoặc key không phải của user này -> Lỗi
            if(hoderValue == null || !hoderValue.startsWith(userId)){
                throw new RuntimeException("Hết hạn xác nhận, hoặc ghế đã bị giữ bởi người khác");
            }
        }

        // 3. Tính lại tổng tiền từ Server (Bảo mật giá)
        SeatMapResponse seatMap = seatMapService.getSeatMap(showId);
        Map<String, SeatResponse> seatMapByCode = seatMap.getSeats()
                .stream()
                .collect(Collectors.toMap(SeatResponse::getSeatCode, s -> s));

        BigDecimal totalPrice = seats.stream()
                .map(code -> {
                    SeatResponse s = seatMapByCode.get(code);
                    if (s == null) throw new IllegalStateException("Ghế " + code + " không hợp lệ.");
                    return s.getPrice();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Tạo và Lưu Booking (QUAN TRỌNG: Lưu DB trước)
        Booking booking = Booking.builder()
                .showTimeId(showId)
                .userId(userId)
                .seats(request.getSeatCodes()) // Truyền list ghế
                .quantity(seats.size())
                .totalPrice(totalPrice)
                .bookingStatus(BookingStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .createTime(LocalDateTime.now())
                .build();

        // Lưu xuống DB. Nếu lỗi tại đây -> Transaction rollback -> Redis vẫn còn nguyên (An toàn)
        booking = bookingRepository.saveAndFlush(booking);
        return bookingMapper.toBookingResponse(booking);
    }
    // Hàm tiện ích để cập nhật trạng thái
    public BookingResponse updateBookingStatus(Long bookingId, BookingStatus newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        // 2. Logic kiểm tra chuyển đổi trạng thái an toàn (Rất quan trọng)
        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            // Không cho phép CONFIRM hoặc CANCEL một booking đã hoàn tất
            throw new IllegalStateException("Không thể cập nhật trạng thái từ "
                    + booking.getBookingStatus() + " sang " + newStatus);
        }

        // 3. Cập nhật trạng thái và thời gian update
        booking.setBookingStatus(newStatus);
        booking.setUpdateTime(LocalDateTime.now());
        booking = bookingRepository.save(booking);
        if (newStatus == BookingStatus.CONFIRMED || newStatus == BookingStatus.CANCELLED) {
            for (String seat : booking.getSeats()) {
                redisTemplate.delete(REDIS_HOLD_PREFIX + booking.getShowTimeId() + ":" + seat);
            }
        }
        return bookingMapper.toBookingResponse(booking);
    }

    public BookingResponse getBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        // Đảm bảo chỉ trả về giao dịch đang chờ thanh toán
        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Booking ID: " + bookingId + " không ở trạng thái PENDING.");
        }

        return bookingMapper.toBookingResponse(booking);
    }
}
