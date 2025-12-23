package com.example.bookingservice.client;

import com.example.bookingservice.client.dto.CinemaSeatLayoutResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "cinemaClient",
        url = "${cinema.service.url:http://localhost:8080}",
        path = "/api/cinemas"
)
public interface CinemaClient {
    @GetMapping("/{roomId}/seat-layout")
    CinemaSeatLayoutResponse getSeatLayout(@PathVariable("roomId") Long roomId);
}
