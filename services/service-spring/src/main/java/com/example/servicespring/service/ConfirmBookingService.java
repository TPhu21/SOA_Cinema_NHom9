package com.example.servicespring.service;

import com.example.servicespring.dto.request.BookingRequest;
import com.example.servicespring.entity.Booking;
import com.example.servicespring.entity.ShowTime;
import com.example.servicespring.entity.User;
import com.example.servicespring.enums.BookingStatus;
import com.example.servicespring.mapper.BookingMapper;
import com.example.servicespring.repository.BookingRepository;
import com.example.servicespring.repository.ShowTimeRepository;
import com.example.servicespring.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

//Logic ghi vĩnh viễn vào SQL
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfirmBookingService {
    BookingRepository bookingRepository;
    ShowTimeRepository showTimeRepository;
    UserRepository userRepository;
    StringRedisTemplate redisTemplate;
    BookingMapper bookingMapper;

    static String REDIS_HOLD_PREFIX = "ticket:hold:";

    @Transactional
    public Booking confirmBooking(BookingRequest request) {
        Long showId = request.getShowTimeId();
        String userId = request.getUserId();
        List<String> seats = request.getSeats();

        //Check seat
        for(String seat : seats){
            String key = REDIS_HOLD_PREFIX+ showId + ":" + seat;
            String hoderValue = redisTemplate.opsForValue().get(key);
            if(hoderValue == null || !hoderValue.startsWith(userId)){
                throw new RuntimeException("Hết hạn xác nhận, hoặc ghế đã bị giữ bởi người khác");
            }
        }
        //Luu vao db
        ShowTime showTime = showTimeRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Showtime not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingMapper.toBookingRequest(request);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setCreateTime(LocalDateTime.now());

        //Delete key redis
        for(String seat : seats){
            redisTemplate.delete(REDIS_HOLD_PREFIX + showId + ":" + seat);
        }

        return bookingRepository.saveAndFlush(booking);
    }
}
