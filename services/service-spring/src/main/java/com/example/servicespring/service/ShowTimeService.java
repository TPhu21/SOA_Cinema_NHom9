package com.example.servicespring.service;

import com.example.servicespring.dto.response.CinemaResponse;
import com.example.servicespring.dto.response.ShowTimeResponse;
import com.example.servicespring.entity.ShowTime;
import com.example.servicespring.mapper.ShowTimeMapper;
import com.example.servicespring.repository.ShowTimeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowTimeService {
    ShowTimeRepository showTimeRepository;
    ShowTimeMapper showTimeMapper;

    //Lay suat chieu theo phim, ngay, rap
    public List<ShowTimeResponse> getShowTime(Long movieId, Long cinemaId, LocalDate date){

        //Xác định khung thời gian để query
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        //Query
        return showTimeRepository.findByCinema_cinemaIdAndMovie_movieIdAndStartTimeBetween(cinemaId, movieId, startOfDay,endOfDay)
                .stream()
                .map(showTimeMapper::toShowTimeResponse)
                .toList();
    }
}
