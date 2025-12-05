package com.example.bookingservice.service;


import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.enums.BookingStatus;
import com.example.bookingservice.repository.BookingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//Logic Giữ chỗ
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReservationService {
    BookingRepository bookingRepository;
    StringRedisTemplate redisTemplate;

    long HOLD_TIME = 10;
    static String REDIS_HOLD_PREFIX = "ticket:hold:";

    public String holdSeats (Long showId, List<String> seatCodes, String userId){
        // 1. KIỂM TRA DB (Đảm bảo ghế không bị bán cứng)
        List<Booking> bookingList = bookingRepository.findByShowTimeIdAndBookingStatusNot(showId, BookingStatus.CANCELLED);
        List<String> permanentSeat = bookingList
                .stream()
                .flatMap(b -> b.getSeats().stream())
                .toList();

        List<String> soldSeats = new ArrayList<>();
        for(String seat : seatCodes){
            if(permanentSeat.contains(seat)){
                soldSeats.add(seat);
            }
        }
        if(!soldSeats.isEmpty()){
            throw new RuntimeException(soldSeats + "BOOKED");
        }

        //Logic Redis
        String reservationToken = UUID.randomUUID().toString();
        List<String> successfullyHeldKeys = new ArrayList<>();
        List<String> conflictedSeats = new ArrayList<>();
        try { for (String seat : seatCodes) {
            String key = REDIS_HOLD_PREFIX + showId + ":" + seat;
            String value = userId + ":" + reservationToken;

            // SETNX + TTL:
            Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(key, value, HOLD_TIME, TimeUnit.MINUTES);

            if (Boolean.FALSE.equals(success)) {
                conflictedSeats.add(seat);
            } else {
                successfullyHeldKeys.add(key);
            }
        }

            if (!conflictedSeats.isEmpty()) {
                // Có xung đột => Phải xóa các Key đã khóa thành công (ROLLBACK)
                if (!successfullyHeldKeys.isEmpty()) {
                    // Sử dụng lệnh DELETE để xóa tất cả keys đã giữ thành công
                    redisTemplate.delete(successfullyHeldKeys);
                }

                // Ném ngoại lệ chi tiết với tất cả các ghế bị lỗi
                String seatList = String.join(", ", conflictedSeats);
                throw new IllegalStateException("Các ghế sau đang được người khác giữ: " + seatList + ". Vui lòng chọn ghế khác.");
            }

            // 4. Tất cả ghế đều được khóa thành công
            return reservationToken;
        } catch (Exception e) {
            // Xử lý các lỗi kết nối Redis/lỗi khác, cần rollback các khóa đã giữ
            if (!successfullyHeldKeys.isEmpty()) {
                redisTemplate.delete(successfullyHeldKeys);
            }
            throw new RuntimeException("Lỗi hệ thống khi giữ chỗ.", e);
        }
    }
}

