package com.example.showtimeservice.service;


import com.example.showtimeservice.client.CinemaClient;
import com.example.showtimeservice.client.dto.CinemaResponse;
import com.example.showtimeservice.dto.response.ShowTimeResponse;
import com.example.showtimeservice.entity.ShowTime;
import com.example.showtimeservice.mapper.ShowTimeMapper;
import com.example.showtimeservice.repository.ShowTimeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowTimeService {
    ShowTimeRepository showTimeRepository;
    ShowTimeMapper showTimeMapper;
    CinemaClient cinemaClient; // Inject Feign Client

    //Lay all show
    public List<ShowTimeResponse> getallshowtimeservice(LocalDate date){
        return showTimeRepository.findByShowDate(date)
                .stream()
                .map(showTimeMapper::toShowTimeResponse)
                .toList();
    }

    // Lấy danh sách Rạp đang chiếu phim X ---
    public List<CinemaResponse> getCinemasShowingMovie(Long movieId) {
        // B1: Hỏi DB: "Phim này đang chiếu ở những rạp nào (ID)?"
        List<Long> cinemaIds = showTimeRepository.findCinemaIdsByMovieId(movieId);

        List<CinemaResponse> results = new ArrayList<>();

        // B2: Với mỗi ID tìm được, gọi sang Cinema Service để lấy tên rạp
        for (Long id : cinemaIds) {
            try {
                // Gọi API sang service bên kia (qua mạng)
                CinemaResponse cinema = cinemaClient.getCinemaById(id);
                if (cinema != null) {
                    results.add(cinema);
                }
            } catch (Exception e) {
                // Nếu gọi lỗi (VD: rạp bị xóa, mạng lag), ta bỏ qua rạp đó để không lỗi cả trang
                System.err.println("Không thể lấy thông tin rạp ID: " + id);
            }
        }
        return results;
    }
    // Lấy danh sách Giờ chiếu ---
    public List<ShowTimeResponse> getShowTimes(Long movieId, Long cinemaId) {
        return showTimeRepository.findByMovieIdAndCinemaId(movieId, cinemaId);
    }
    //Lay suat chieu theo phim, ngay, rap
    public List<ShowTimeResponse> getShowTime(Long movieId, Long cinemaId, LocalDate date){

        // 1. Logic thời gian:
        // Nếu user chọn ngày hôm nay -> Chỉ lấy các suất chiếu TƯƠNG LAI (chưa chiếu)
        // Nếu user chọn ngày mai -> Lấy cả ngày

        LocalDateTime startFilter = date.atStartOfDay();
        LocalDateTime endFilter = date.atTime(LocalTime.MAX);

        LocalDateTime now = LocalDateTime.now();

        // Nếu ngày lọc là hôm nay, startFilter phải là thời điểm hiện tại
        if (date.equals(LocalDate.now())) {
            startFilter = now;
        }

        // 2. Query DB
        return showTimeRepository.findByCinemaIdAndMovieIdAndStartTimeBetween(
                        cinemaId, movieId, startFilter, endFilter)
                .stream()
                .map(showTimeMapper::toShowTimeResponse)
                .toList();
    }
    public ShowTimeResponse getShowTimeById(Long id) {
        ShowTime showTime = showTimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Showtime not found: " + id));
        return showTimeMapper.toShowTimeResponse(showTime);
    }
}
