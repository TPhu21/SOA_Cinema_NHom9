package com.example.showtimeservice.repository;

import com.example.showtimeservice.entity.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Long> {

    List<ShowTime> findByShowDate(LocalDate date);

    List<ShowTime> findByMovieIdAndCinemaId(Long movieId, Long cinemaId);

    List<ShowTime> findByMovieIdAndCinemaIdAndShowDate(Long movieId, Long cinemaId, LocalDate showDate);

    List<ShowTime> findByMovieIdAndCinemaIdAndStartTimeBetween(Long movieId,
                                                               Long cinemaId,
                                                               LocalDateTime start,
                                                               LocalDateTime end);

    @Query("SELECT DISTINCT s.cinemaId FROM ShowTime s WHERE s.movieId = :movieId")
    List<Long> findCinemaIdsByMovieId(Long movieId);

    boolean existsByRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(Long roomId,
                                                                     LocalDateTime endTime,
                                                                     LocalDateTime startTime);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END "
            + "FROM ShowTime s "
            + "WHERE s.roomId = :roomId "
            + "AND s.showTimeId <> :showTimeId "
            + "AND s.startTime < :endTime "
            + "AND s.endTime > :startTime")
    boolean existsOverlapWithOtherShowTimes(Long roomId,
                                            Long showTimeId,
                                            LocalDateTime startTime,
                                            LocalDateTime endTime);

    // For Excel import duplicate check
    boolean existsByMovieIdAndCinemaIdAndRoomIdAndStartTime(Long movieId,
                                                            Long cinemaId,
                                                            Long roomId,
                                                            LocalDateTime startTime);
}
