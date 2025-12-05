package com.example.showtimeservice.client;

import com.example.showtimeservice.client.dto.CinemaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cinema-service", url = "http://localhost:8081")
public interface CinemaClient {
    @GetMapping("/api/cinemas/{id}")
    CinemaResponse getCinemaById(@PathVariable("id") Long id);
}
