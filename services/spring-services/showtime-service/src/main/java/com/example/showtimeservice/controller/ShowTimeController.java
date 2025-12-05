package com.example.showtimeservice.controller;


import com.example.showtimeservice.client.dto.CinemaResponse;
import com.example.showtimeservice.dto.response.ShowTimeResponse;
import com.example.showtimeservice.service.ShowTimeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/showtimes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowTimeController {
    ShowTimeService showTimeService;

    @GetMapping
    public List<ShowTimeResponse> getAllShowTimesByDate(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {
        // Nếu customer không tryền thì mặc định là .now()
        LocalDate effectDate = (date == null) ? LocalDate.now() : date;
        return showTimeService.getallshowtimeservice(effectDate);
    }

    // API : Frontend gọi API này ngay khi vào trang Đặt Vé
    // Mục đích: Hiển thị danh sách Rạp cho khách chọn
    @GetMapping("/cinemas-by-movie/{movieId}")
    public List<CinemaResponse> getCinemas(@PathVariable Long movieId) {
        return showTimeService.getCinemasShowingMovie(movieId);
    }

    @GetMapping("/details")
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
    @GetMapping("/{showTimeId}")
    public ShowTimeResponse getShowTimeById(@PathVariable("showTimeId") Long showTimeId) {
        return showTimeService.getShowTimeById(showTimeId);
    }

//@GetMapping("/details")
//public List<ShowTimeResponse> getShowTimes(
//        @RequestParam Long movieId,
//        @RequestParam Long cinemaId) {
//    return showTimeService.getShowTimes(movieId, cinemaId);
//}

}
