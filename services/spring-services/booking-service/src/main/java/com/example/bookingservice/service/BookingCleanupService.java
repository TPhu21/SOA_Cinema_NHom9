package com.example.bookingservice.service;

import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.enums.BookingStatus;
import com.example.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingCleanupService {

    private final BookingRepository bookingRepository;
    private final StringRedisTemplate redisTemplate;

    // Prefix key redis của bạn
    static String REDIS_HOLD_PREFIX = "ticket:hold:";

    // Chạy mỗi 60 giây (60000ms)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelExpiredBookings() {
        // 1. Xác định mốc thời gian quá hạn (ví dụ: tạo cách đây hơn 10 phút)
        LocalDateTime cutOffTime = LocalDateTime.now().minusMinutes(10);

        // 2. Tìm các đơn PENDING cũ hơn mốc thời gian này
        List<Booking> expiredBookings = bookingRepository.findByBookingStatusAndCreateTimeBefore(
                BookingStatus.PENDING,
                cutOffTime
        );

        if (!expiredBookings.isEmpty()) {
            log.info("Tìm thấy {} đơn hàng hết hạn. Đang tiến hành hủy...", expiredBookings.size());

            for (Booking booking : expiredBookings) {
                // 3. Chuyển trạng thái sang CANCELLED (hoặc xóa hẳn tùy bạn)
                booking.setBookingStatus(BookingStatus.CANCELLED);
                booking.setUpdateTime(LocalDateTime.now());

                // 4. (Quan trọng) Xóa key Redis tương ứng để chắc chắn giải phóng ghế
                // Dù Redis có TTL tự hết hạn, nhưng xóa thủ công ở đây cho sạch sẽ
                for (String seat : booking.getSeats()) {
                    String key = REDIS_HOLD_PREFIX + booking.getShowTimeId() + ":" + seat;
                    redisTemplate.delete(key);
                }
            }

            // Lưu thay đổi xuống DB
            bookingRepository.saveAll(expiredBookings);
            log.info("Đã hủy thành công {} đơn hàng.", expiredBookings.size());
        }
    }
}