package com.example.servicespring.mapper;

import com.example.servicespring.dto.response.ShowTimeResponse;
import com.example.servicespring.entity.ShowTime;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShowTimeMapper {
    ShowTimeResponse toShowTimeResponse(ShowTime showTime);
}
