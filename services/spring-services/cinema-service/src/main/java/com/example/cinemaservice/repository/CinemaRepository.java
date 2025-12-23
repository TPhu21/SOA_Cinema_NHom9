package com.example.cinemaservice.repository;

import com.example.cinemaservice.dto.response.CinemaResponse;
import com.example.cinemaservice.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {

    List<Cinema> findByCinemaNameContainingIgnoreCase(String cinemaName);

    List<Cinema> findBycinemaCity(String cinemaCity);

    List<Cinema> findBycinemaBrand(String cinemaBrand);

    List<Cinema> findByCinemaAddressContainingIgnoreCase(String cinemaAddress);

}
