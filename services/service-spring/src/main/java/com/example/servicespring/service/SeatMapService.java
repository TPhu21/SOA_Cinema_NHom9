package com.example.servicespring.service;

import com.example.servicespring.dto.response.SeatMapResponse;
import com.example.servicespring.dto.response.SeatResponse;
import com.example.servicespring.entity.Booking;
import com.example.servicespring.entity.ShowTime;
import com.example.servicespring.enums.BookingStatus;
import com.example.servicespring.enums.SeatStatus;
import com.example.servicespring.repository.BookingRepository;
import com.example.servicespring.repository.ShowTimeRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeatMapService {
    ShowTimeRepository showTimeRepository;
    BookingRepository bookingRepository;
    StringRedisTemplate redisTemplate;

    static String REDIS_HOLD_PRE = "ticket:hold:";

    public SeatMapResponse getSeatMap(Long showId){
        ShowTime showTime = showTimeRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("ShowTime not found"));
    //Lấy kích thước
    int rows = showTime.getCinema().getRowCount();
    int cols = showTime.getCinema().getColCount();

    //Lấy ghế đã đặt (DB)
        List<String> commitBookings = bookingRepository.findCommittedSeatCodesNative(showId);


        Set<String> permanentSeat = new HashSet<>(commitBookings);


    //Lấy ghế đang giữ
    String holdKeyPattern = REDIS_HOLD_PRE + showId + ":*"; // Mẫu
    Set<String> heldKeys = redisTemplate.keys(holdKeyPattern); //Lấy key KHỚP VỚI mẫu
    Set<String> tempSeat = heldKeys
            .stream()
            .map(key -> key.substring(key.lastIndexOf(":")+1))
            .collect(Collectors.toSet());
    //Tạo map ghế và gán status
    List<SeatResponse> seatList = new ArrayList<>();
        for(int row = 0; row < rows; row++){
            char rowChar = (char) ('A' + row);
            String rowName = String.valueOf(rowChar);

            for(int col = 1; col <= cols; col++){
                String seatCode = rowName + col;
                SeatStatus status;

                if(permanentSeat.contains(seatCode)){
                    status = SeatStatus.BOOKED;
                } else if (tempSeat.contains(seatCode)) {
                    status = SeatStatus.RESERVED;
                } else {
                    status = SeatStatus.AVAILABLE;
                }
                seatList.add(SeatResponse.builder()
                        .seatCode(seatCode)
                        .row(rowName)
                        .column(col)
                        .status(status)
                        .build());
            }
        }
        return SeatMapResponse.builder()
                .showId(showId)
                .rowCount(rows)
                .columnCount(cols)
                .seats(seatList)
                .build();
    }

}
