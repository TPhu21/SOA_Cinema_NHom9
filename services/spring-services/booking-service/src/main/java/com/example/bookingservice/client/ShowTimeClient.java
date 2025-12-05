package com.example.bookingservice.client;

import com.example.bookingservice.client.dto.ShowTimeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "showTimeClient",
        url = "${showtime.service.url}",
        path = "/api/showtimes"
)
public interface ShowTimeClient {
    @GetMapping("/{id}")
    ShowTimeDto getShowTime(@PathVariable("id") Long id);
}
