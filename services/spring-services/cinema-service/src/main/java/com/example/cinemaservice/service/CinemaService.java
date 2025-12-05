package com.example.cinemaservice.service;


import com.example.cinemaservice.dto.response.CinemaResponse;
import com.example.cinemaservice.entity.Cinema;
import com.example.cinemaservice.mapper.CinemaMapper;
import com.example.cinemaservice.repository.CinemaRepository;
import lombok.AccessLevel;
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


    public List<CinemaResponse> findAll(){
        return cinemaRepository.findAll()
                .stream()
                .map(cinemaMapper::toCinemaResponse)
                .toList();
    }

    public Cinema getCinemaById(Long cinemaId){
        return cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new RuntimeException("Cinema not found"));
    }

}
