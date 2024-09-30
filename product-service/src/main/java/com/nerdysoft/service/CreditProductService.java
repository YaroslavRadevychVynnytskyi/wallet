package com.nerdysoft.service;

import com.nerdysoft.dto.api.request.CreditProductRequestDto;
import com.nerdysoft.dto.api.response.CreditProductResponseDto;
import java.util.List;
import java.util.UUID;

public interface CreditProductService {
    CreditProductResponseDto create(CreditProductRequestDto requestDto);

    CreditProductResponseDto getById(UUID creditProductId);

    List<CreditProductResponseDto> getAll();

    CreditProductResponseDto updateById(UUID creditProductId, CreditProductRequestDto requestDto);

    boolean deleteById(UUID creditProductId);
}
