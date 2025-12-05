package com.example.cinemaservice.service;

import com.example.cinemaservice.entity.Room;
import com.example.cinemaservice.enums.SeatType;
import com.example.cinemaservice.dto.response.SeatLayoutItemResponse;
import com.example.cinemaservice.dto.response.SeatLayoutResponse;
import com.example.cinemaservice.entity.Cinema;
import com.example.cinemaservice.repository.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeatLayoutService {
    RoomRepository roomRepository;

    public SeatLayoutResponse getSeatLayout(Long roomId) {

        // 1. Tìm phòng chiếu
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        int rows = room.getRowCount() != null ? room.getRowCount() : 10;
        int cols = room.getColCount() != null ? room.getColCount() : 10;

        List<SeatLayoutItemResponse> seats = new ArrayList<>();

        for(int row = 0; row < rows; row++) {
            char rowChar = (char) ('A' + row);
            String rowName = String.valueOf(rowChar);

            for (int col = 1; col <= cols; col++) {
                String seatCode = rowName + col;
                SeatType seatType = resolveSeat(row, col, rows, cols);

                seats.add(SeatLayoutItemResponse.builder()
                        .seatCode(seatCode)
                        .rowLabel(rowName)
                        .seatNumber(col)
                        .seatType(seatType.name())
                        .build());
            }
        }
        return SeatLayoutResponse.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .cinemaName(room.getCinema().getCinemaName())
                .rowCount(rows)
                .colCount(cols)
                .seats(seats)
                .build();
    }
    public SeatType resolveSeat(int rowI, int colI, int totalR, int totalC){
        int centerLeft = totalC / 2;
        int centerRight = centerLeft + 1;
        if (colI == centerLeft || colI == centerRight)
            return SeatType.COUPLE;

        if(rowI < 6 && rowI > 2)
            return SeatType.VIP;
        return SeatType.STANDARD;
    }

}
