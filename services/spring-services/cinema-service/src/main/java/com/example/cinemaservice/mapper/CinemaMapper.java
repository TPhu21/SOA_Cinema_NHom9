package com.example.cinemaservice.mapper;


import com.example.cinemaservice.dto.response.CinemaResponse;
import com.example.cinemaservice.entity.Cinema;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CinemaMapper {
    CinemaResponse toCinemaResponse(Cinema cinema);
}
