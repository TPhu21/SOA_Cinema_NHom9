package com.example.showtimeservice.client;

import com.example.showtimeservice.client.dto.ApiResponse;
import com.example.showtimeservice.client.dto.CinemaResponse;
import com.example.showtimeservice.client.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * CinemaClient - Gọi Cinema Service qua Gateway
 * 
 * Dùng để lấy thông tin rạp & phòng chiếu từ Cinema Service
 * 
 * @author Admin
 */
@FeignClient(name = "cinema-service", url = "${cinema.service.url:http://localhost:8080}")
public interface CinemaClient {

    /**
     * Lấy tất cả rạp chiếu
     * 
     * @return Danh sách rạp
     */
    @GetMapping("/api/cinemas")
    List<CinemaResponse> getAllCinemas();

    /**
     * Lấy thông tin rạp theo ID
     * 
     * @param id ID rạp
     * @return Thông tin rạp
     */
    @GetMapping("/api/cinemas/{id}")
    CinemaResponse getCinemaById(@PathVariable("id") Long id);

    /**
     * Lấy thông tin phòng chiếu theo ID (Customer endpoint)
     * 
     * @param roomId ID phòng
     * @return Thông tin phòng
     */
    @GetMapping("/api/cinemas/rooms/{id}")
    Map<String, Object> getRoomById(@PathVariable("id") Long roomId);

    /**
     * Lấy danh sách phòng theo rạp (Customer endpoint)
     * GET /api/cinemas/rooms/cinema/{cinemaId}
     */
    @GetMapping("/api/cinemas/rooms/cinema/{cinemaId}")
    Map<String, Object> getRoomsByCinema(@PathVariable("cinemaId") Long cinemaId);

    /**
     * Kiểm tra phòng có thuộc rạp không (Customer endpoint)
     * GET /api/cinemas/rooms/{roomId}/belongs-to-cinema/{cinemaId}
     */
    @GetMapping("/api/cinemas/rooms/{roomId}/belongs-to-cinema/{cinemaId}")
    Map<String, Object> checkRoomBelongsToCinema(
            @PathVariable("roomId") Long roomId,
            @PathVariable("cinemaId") Long cinemaId);
}
