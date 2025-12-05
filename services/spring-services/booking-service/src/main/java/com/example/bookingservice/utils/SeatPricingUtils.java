package com.example.bookingservice.utils;

import com.example.bookingservice.enums.SeatType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class SeatPricingUtils {
    static BigDecimal BASE_PRICE = new BigDecimal("90000.00");

    static BigDecimal VIP_SURCHARGE = new BigDecimal("20000.00");
    static BigDecimal COUPLE_SURCHARGE = new BigDecimal("30000.00");

    static BigDecimal PRIME_TIME_SURCHARGE = new BigDecimal("20000"); // giờ vàng
    static BigDecimal WEEKEND_SURCHARGE = new BigDecimal("10000"); // cuối tuần

    // Hàm tính phụ phí dựa trên tên hàng ghế (A, B, C...)

    public static BigDecimal caculatePrice(SeatType seatType, LocalDate date, LocalTime showTime) {
        BigDecimal price = switch (seatType) {
            case VIP -> BASE_PRICE.add(VIP_SURCHARGE);
            case COUPLE -> BASE_PRICE.add(COUPLE_SURCHARGE);
            default -> BASE_PRICE;
        };
        //18-22h là giờ vàng
        if (!showTime.isBefore(LocalTime.of(18, 0))
                && showTime.isBefore(LocalTime.of(22, 0))) {
            price = price.add(PRIME_TIME_SURCHARGE);
        }
        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            price = price.add(WEEKEND_SURCHARGE);
        }

        return price;


    }
}
