package com.example.showtimeservice.mapper;


import com.example.showtimeservice.dto.response.ShowTimeResponse;
import com.example.showtimeservice.entity.ShowTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShowTimeMapper {
    @Mapping(target = "cinema", ignore = true)
    @Mapping(target = "room", ignore = true)
    ShowTimeResponse toShowTimeResponse(ShowTime showTime);
}
