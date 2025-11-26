package com.example.servicespring.mapper;

import com.example.servicespring.dto.response.CinemaResponse;
import com.example.servicespring.entity.Cinema;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CinemaMapper {
    CinemaResponse toCinemaResponse(Cinema cinema);
}
