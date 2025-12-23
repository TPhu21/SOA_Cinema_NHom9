package com.example.cinemaservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cinemaservice.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    java.util.List<Room> findByCinema_CinemaId(Long cinemaId);
}
