package com.example.servicespring.controller;

import com.example.servicespring.dto.response.CinemaResponse;
import com.example.servicespring.service.CinemaService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/cinemas")
public class CinemaController {
    CinemaService cinemaService;

    @GetMapping("/by-movie/{movieId}")
    List<CinemaResponse> getAllCinemasByMovie(@PathVariable("movieId") Long movieId){
        return cinemaService.findCinemabyMovieId(movieId);
    }

}
