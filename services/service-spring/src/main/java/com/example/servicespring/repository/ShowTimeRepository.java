package com.example.servicespring.repository;

import com.example.servicespring.entity.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Long> {
//    List<ShowTime> findByCinemaIdAndMovieId(Long cinemaId, Long movieId);
    Optional<ShowTime> findById(Long showId);
    //Loc theo ngay
    List<ShowTime> findByCinema_cinemaIdAndMovie_movieIdAndStartTimeBetween(
            Long cinemaId,
            Long movieId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);


}
