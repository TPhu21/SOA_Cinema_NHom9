package com.example.cinemaservice.mapper;


import com.example.cinemaservice.dto.request.CinemaRequest;
import com.example.cinemaservice.dto.response.CinemaResponse;
import com.example.cinemaservice.entity.Cinema;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CinemaMapper {
    CinemaResponse toCinemaResponse(Cinema cinema);

    @Mapping(target = "cinemaId", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Cinema toCinema(CinemaRequest request);
    
}

