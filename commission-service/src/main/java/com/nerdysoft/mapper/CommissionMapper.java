package com.nerdysoft.mapper;

import com.nerdysoft.config.MapperConfig;
import com.nerdysoft.dto.api.request.SaveCommissionRequestDto;
import com.nerdysoft.dto.api.response.SaveCommissionResponseDto;
import com.nerdysoft.model.Commission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CommissionMapper {
    @Mapping(target = "usdCommissionAmount", source = "usdCommission")
    @Mapping(target = "senderCurrencyCommissionAmount", source = "originalCurrencyCommission")
    Commission toCommission(SaveCommissionRequestDto requestDto);

    @Mapping(target = "usdCommission", source = "usdCommissionAmount")
    @Mapping(target = "originalCurrencyCommission", source = "senderCurrencyCommissionAmount")
    SaveCommissionResponseDto toResponseDto(Commission commission);
}
