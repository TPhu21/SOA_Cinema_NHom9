package com.example.bookingservice.client;

import com.example.bookingservice.client.dto.ShowTimeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "showTimeClient", url = "${showtime.service.url:http://localhost:8080}")
public interface ShowTimeClient {

    /**
     * Lấy showtime theo ID (Customer endpoint)
     */
    @GetMapping("/api/showtimes/{id}")
    ShowTimeDto getShowTime(@PathVariable("id") Long id);
    
    /**
     * Lấy tất cả showtimes từ public endpoint
     * Dùng cho data initialization
     * Không cần authentication
     */
    @GetMapping("/api/showtimes/all-for-seed")
    List<ShowTimeDto> getAllShowTimes();
}
