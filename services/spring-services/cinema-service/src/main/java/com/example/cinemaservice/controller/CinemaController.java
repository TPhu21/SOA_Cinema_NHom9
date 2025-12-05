package com.example.cinemaservice.controller;

import com.example.cinemaservice.dto.response.CinemaResponse;
import com.example.cinemaservice.dto.response.SeatLayoutResponse;
import com.example.cinemaservice.entity.Cinema;
import com.example.cinemaservice.service.CinemaService;
import com.example.cinemaservice.service.SeatLayoutService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/cinemas")
public class CinemaController {
    CinemaService cinemaService;
    SeatLayoutService seatLayoutService;

    @GetMapping
    public List<CinemaResponse> getAllCinemas() {
        return cinemaService.findAll();
    }

    @GetMapping("/{id}")
    public Cinema getCinemaById(@PathVariable Long id) {
        return cinemaService.getCinemaById(id);
    }

    @GetMapping("/{roomId}/seat-layout")
    public SeatLayoutResponse getSeatLayout(@PathVariable("roomId") Long roomId) {
        return seatLayoutService.getSeatLayout(roomId);
    }

}
