package com.nerdysoft.mapper;

import com.nerdysoft.config.MapperConfig;
import com.nerdysoft.dto.AddOrUpdateRateRequestDto;
import com.nerdysoft.dto.ExchangeRateResponseDto;
import com.nerdysoft.entity.ExchangeRate;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ExchangeRateMapper {
    ExchangeRateResponseDto toDto(ExchangeRate exchangeRate);

    ExchangeRate toModel(AddOrUpdateRateRequestDto requestDto);
}
