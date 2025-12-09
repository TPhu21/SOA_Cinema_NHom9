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

    long HOLD_TIME = 5;
    static String REDIS_HOLD_PREFIX = "ticket:hold:";

    public String holdSeats (Long showId, List<String> seatCodes, String userId){
        // 1. KIỂM TRA DB (Đảm bảo ghế không bị bán cứng)
        List<Booking> bookingList = bookingRepository.findByShowTimeIdAndBookingStatusNot(showId, BookingStatus.CANCELLED);
        List<String> permanentSeat = bookingList.stream()
                //  Bỏ qua các vé do chính user này đang giữ (PENDING)
                // Để cho phép họ đặt lại hoặc thanh toán tiếp
                .filter(b -> !b.getUserId().equals(userId))
                .flatMap(b -> b.getSeats().stream())
                .toList();

        List<String> soldSeats = new ArrayList<>();
        for(String seat : seatCodes){
            if(permanentSeat.contains(seat)){
                soldSeats.add(seat);
            }
        }
        if(!soldSeats.isEmpty()){
            throw new IllegalStateException("Ghế đã bán: " + String.join(", ", soldSeats));
        }

        // 2. TRƯỚC KHI GIỮ GHẾ MỚI: release các ghế cũ của chính user KHÔNG CÒN trong seatCodes
        //    => đảm bảo những ghế đã bỏ chọn sẽ AVAILABLE
        String keyPrefix = REDIS_HOLD_PREFIX + showId + ":";
        var keys = redisTemplate.keys(keyPrefix + "*");             // tất cả ghế của showId này

        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                String value = redisTemplate.opsForValue().get(key);
                if (value == null) continue;

                // tách userId ra khỏi "userId:token"
                String[] parts = value.split(":", 2);
                String holderUserId = parts[0];

                if (!holderUserId.equals(userId)) {
                    // ghế người khác giữ -> không đụng vào
                    continue;
                }

                // Lấy seatCode từ key: ticket:hold:{showId}:{seatCode}
                String seatCode = key.substring(keyPrefix.length());

                // Nếu seatCode KHÔNG còn trong danh sách mới -> release
                if (!seatCodes.contains(seatCode)) {
                    redisTemplate.delete(key);
                }
            }
        }
        //Logic Redis
        String reservationToken = UUID.randomUUID().toString();
        List<String> successfullyHeldKeys = new ArrayList<>();
        List<String> conflictedSeats = new ArrayList<>();
        try {
            for (String seat : seatCodes) {
                String key = REDIS_HOLD_PREFIX + showId + ":" + seat;
                String value = userId + ":" + reservationToken;

                // SETNX + TTL:
                Boolean success = redisTemplate.opsForValue()
                        .setIfAbsent(key, value, HOLD_TIME, TimeUnit.MINUTES);

                if (Boolean.TRUE.equals(success)) {
                    // Giữ thành công (Key chưa tồn tại)
                    successfullyHeldKeys.add(key);
                } else {
                    // B. Nếu thất bại -> Kiểm tra xem CÓ PHẢI CHÍNH MÌNH ĐANG GIỮ KHÔNG?
                    String currentOwnerValue = redisTemplate.opsForValue().get(key);
                    // Value format: "userId:token" -> Lấy phần userId để so sánh
                    if (currentOwnerValue != null && currentOwnerValue.startsWith(userId + ":")) {
                        // Đúng là tôi đang giữ -> Gia hạn thời gian (Renew TTL)
                        redisTemplate.expire(key, HOLD_TIME, TimeUnit.MINUTES);

                        // (Tùy chọn) Cập nhật token mới nếu cần thiết, hoặc giữ nguyên token cũ.
                        // Ở đây ta cứ coi như thành công để tiếp tục quy trình.
                        successfullyHeldKeys.add(key);
                    } else {
                        // Là người KHÁC giữ -> Lỗi thật sự
                        conflictedSeats.add(seat);
                    }
                }
            }


            if (!conflictedSeats.isEmpty()) {
                // Có xung đột với người khác => Phải rollback các ghế mình vừa giữ mới
                // (Lưu ý: Không rollback các ghế cũ mình đang giữ, để tránh mất chỗ của chính mình)
                if (!successfullyHeldKeys.isEmpty()) {
                    // Logic rollback an toàn: Chỉ xóa những key nào mang token mới này (nếu cần kỹ hơn)
                    // Nhưng ở mức độ đơn giản, ta xóa các key vừa thêm vào list successfullyHeldKeys
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

