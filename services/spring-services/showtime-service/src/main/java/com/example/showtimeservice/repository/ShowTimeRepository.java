package com.example.showtimeservice.repository;

import com.example.showtimeservice.dto.response.ShowTimeResponse;
import com.example.showtimeservice.entity.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Long> {
    List<ShowTime> findByShowDate(LocalDate date);
//    List<ShowTime> findByCinemaIdAndMovieId(Long cinemaId, Long movieId);
    //Loc theo ngay
    List<ShowTime> findByCinemaIdAndMovieIdAndStartTimeBetween(
            Long cinemaId,
            Long movieId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);
    @Query("SELECT DISTINCT s.cinemaId FROM ShowTime s WHERE s.movieId = :movieId")
    List<Long> findCinemaIdsByMovieId(Long movieId);
    List<ShowTimeResponse> findByMovieIdAndCinemaId(Long movieId, Long cinemaId);


}
