package com.example.servicespring.service;

import com.example.servicespring.dto.response.CinemaResponse;
import com.example.servicespring.mapper.CinemaMapper;
import com.example.servicespring.repository.CinemaRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class CinemaService {
    CinemaRepository cinemaRepository;
    CinemaMapper cinemaMapper;

    public List<CinemaResponse> findCinemabyMovieId(Long movieId){
        return cinemaRepository.findByMovieId(movieId)
                .stream()
                .map(cinemaMapper::toCinemaResponse)
                .toList();
    }

}
