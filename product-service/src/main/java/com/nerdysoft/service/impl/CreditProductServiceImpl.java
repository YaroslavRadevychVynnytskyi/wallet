package com.nerdysoft.service.impl;

import com.nerdysoft.dto.api.request.CreditProductRequestDto;
import com.nerdysoft.dto.api.response.CreditProductResponseDto;
import com.nerdysoft.entity.CreditProduct;
import com.nerdysoft.mapper.CreditProductMapper;
import com.nerdysoft.repo.CreditProductRepository;
import com.nerdysoft.service.CreditProductService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditProductServiceImpl implements CreditProductService {
    private final CreditProductRepository creditProductRepository;
    private final CreditProductMapper creditProductMapper;

    @Override
    public CreditProductResponseDto create(CreditProductRequestDto requestDto) {
        CreditProduct creditProduct = creditProductMapper.toModel(requestDto);

        return creditProductMapper.toResponseDto(creditProductRepository.save(creditProduct));
    }

    @Override
    public CreditProductResponseDto getById(UUID creditProductId) {
        CreditProduct creditProduct = retrieveById(creditProductId);

        return creditProductMapper.toResponseDto(creditProduct);
    }

    @Override
    public List<CreditProductResponseDto> getAll() {
        return creditProductRepository.findAll().stream()
                .map(creditProductMapper::toResponseDto)
                .toList();
    }

    @Override
    public CreditProductResponseDto updateById(UUID creditProductId, CreditProductRequestDto requestDto) {
        CreditProduct creditProduct = retrieveById(creditProductId);
        creditProductMapper.updateFromDto(creditProduct, requestDto);

        return creditProductMapper.toResponseDto(creditProductRepository.save(creditProduct));
    }

    @Override
    public boolean deleteById(UUID creditProductId) {
        if (creditProductRepository.existsById(creditProductId)) {
            creditProductRepository.deleteById(creditProductId);
            return true;
        }

        return false;
    }

    private CreditProduct retrieveById(UUID creditProductId) {
        return creditProductRepository.findById(creditProductId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find credit product with ID: " + creditProductId));
    }
}
