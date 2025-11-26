package com.example.servicespring.repository;

import com.example.servicespring.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    @Query("SELECT DISTINCT c FROM Cinema c JOIN ShowTime s ON s.cinema = c WHERE s.movie.movieId = :movieId")
    List<Cinema> findByMovieId(Long movieId);
}
