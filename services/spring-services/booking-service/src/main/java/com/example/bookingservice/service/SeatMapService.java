package com.example.bookingservice.service;

import com.example.bookingservice.client.CinemaClient;
import com.example.bookingservice.client.ShowTimeClient;
import com.example.bookingservice.client.dto.CinemaSeatLayoutItem;
import com.example.bookingservice.client.dto.CinemaSeatLayoutResponse;
import com.example.bookingservice.client.dto.ShowTimeDto;
import com.example.bookingservice.dto.response.SeatMapResponse;
import com.example.bookingservice.dto.response.SeatResponse;
import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.enums.BookingStatus;
import com.example.bookingservice.enums.SeatStatus;
import com.example.bookingservice.enums.SeatType;
import com.example.bookingservice.repository.BookingRepository;
import com.example.bookingservice.utils.SeatPricingUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class SeatMapService {
    ShowTimeClient showTimeClient;
    CinemaClient cinemaClient;
    BookingRepository bookingRepository;
    StringRedisTemplate redisTemplate;

    static String REDIS_HOLD_PREFIX = "ticket:hold:";

    public SeatMapResponse getSeatMap (Long showId){
        //log check, có thể xóa
        if (showId == null) {
            throw new IllegalArgumentException("showId không được null (check @RequestParam showTimeId)");
        }
        // 1. Lấy info showtime
        ShowTimeDto showTime = showTimeClient.getShowTime(showId);
        if (showTime == null) {
            throw new IllegalStateException("Không tìm thấy showtime với id = " + showId);
        }
        Long roomId = showTime.getRoomId();
        if (roomId == null) {
            throw new IllegalStateException(
                    "Showtime " + showId + " không có roomId (check ShowTimeDto & JSON của showtime-service)"
            );
        }

        LocalDate showDate = showTime.getShowDate();
        LocalTime startTime = showTime.getStartTime();

        // 2. Lấy layout ghế
        CinemaSeatLayoutResponse layout = cinemaClient.getSeatLayout(roomId);

        // 3. Lấy TẤT CẢ booking chưa hủy (Gồm cả CONFIRMED và PENDING)
        // Lưu ý: Bạn cần thêm hàm findByShowTimeIdAndBookingStatusNot vào BookingRepository
        List<Booking> activeBookings = bookingRepository.findByShowTimeIdAndBookingStatusNot(showId, BookingStatus.CANCELLED);

        // Tách ra: Ghế đã bán cứng (CONFIRMED)
        Set<String> soldSeatCodes = activeBookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.CONFIRMED)
                .flatMap(b -> b.getSeats().stream())
                .collect(Collectors.toSet());

        // Tách ra: Ghế đang chờ thanh toán (PENDING)
        Set<String> pendingSeatCodes = activeBookings.stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.PENDING)
                .flatMap(b -> b.getSeats().stream())
                .collect(Collectors.toSet());

        // 4. Ghế đang giữ tạm bằng Redis (User đang click chọn nhưng chưa bấm thanh toán)
        Set<String> redisHeldSeatCodes = findHeldSeatsFromRedis(showId);

        // Gộp ghế Pending và ghế Redis lại thành nhóm "Đang bị khóa"
        Set<String> lockedSeatCodes = new java.util.HashSet<>(pendingSeatCodes);
        lockedSeatCodes.addAll(redisHeldSeatCodes);

        // 5. Map từng seat (Truyền sold và locked vào)
        List<SeatResponse> seatList = layout.getSeats()
                .stream()
                .map(item -> mapSeat(item, soldSeatCodes, lockedSeatCodes, showDate, startTime))
                .toList();

        return SeatMapResponse.builder()
                .showTimeId(showId)
                .roomId(roomId)
                .seats(seatList)
                .rowCount(layout.getRowCount())
                .colCount(layout.getColCount())
                .build();
    }
    SeatResponse mapSeat(CinemaSeatLayoutItem s,
                         Set<String> soldSeatCodes,
                         Set<String> lockedSeatCodes, // Tham số này thay cho heldSeatCodes cũ
                         LocalDate showDate,
                         LocalTime startTime) {

        String seatCode = s.getSeatCode();
        SeatType seatType = s.getSeatType();
        SeatStatus status;

        // Ưu tiên 1: Đã bán -> BOOKED
        if (soldSeatCodes.contains(seatCode)) {
            status = SeatStatus.BOOKED;
        }
        // Ưu tiên 2: Đang giữ (Redis) hoặc Đang chờ thanh toán (Pending) -> LOCKED (hoặc RESERVED)
        else if (lockedSeatCodes.contains(seatCode)) {
            status = SeatStatus.RESERVED; // Bạn cần thêm giá trị LOCKED vào Enum SeatStatus, hoặc dùng RESERVED
        }
        // Còn lại là trống
        else {
            status = SeatStatus.AVAILABLE;
        }

        BigDecimal price = SeatPricingUtils.caculatePrice(seatType, showDate, startTime);

        return SeatResponse.builder()
                .seatCode(seatCode)
                .rowLabel(s.getRowLabel())
                .seatNumber(s.getSeatNumber())
                .seatType(seatType)
                .status(status)
                .price(price)
                .build();
    }
    Set<String> findHeldSeatsFromRedis (Long showId) {
        String pattern = REDIS_HOLD_PREFIX + showId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty())
            return Collections.emptySet();

        String prefix = REDIS_HOLD_PREFIX + showId + ":";
        return keys.stream()
                .map(k -> k.substring(prefix.length()))
                .collect(Collectors.toSet());
    }
}
