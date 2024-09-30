package com.nerdysoft.mapper;

import com.nerdysoft.config.MapperConfig;
import com.nerdysoft.dto.api.request.CreditProductRequestDto;
import com.nerdysoft.dto.api.response.CreditProductResponseDto;
import com.nerdysoft.entity.CreditProduct;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CreditProductMapper {
    CreditProduct toModel(CreditProductRequestDto requestDto);

    CreditProductResponseDto toResponseDto(CreditProduct creditProduct);

    void updateFromDto(@MappingTarget CreditProduct creditProduct, CreditProductRequestDto requestDto);
}
