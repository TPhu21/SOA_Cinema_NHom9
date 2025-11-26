package com.example.servicespring.controller;

import com.example.servicespring.dto.response.ShowTimeResponse;
import com.example.servicespring.service.ShowTimeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/showtimes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowTimeController {
    ShowTimeService showTimeService;

    @GetMapping
    List<ShowTimeResponse>  getAllShowTimes(
            @RequestParam Long movieId,
            @RequestParam Long cinemaId,
            @RequestParam(required = false) // Khong bat buoc truyen vao
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date){
        // Nếu customer không tryền thì mặc định là .now()
        LocalDate effectDate = (date == null) ? LocalDate.now() : date;
        return showTimeService.getShowTime(movieId, cinemaId, effectDate);

    }

}
