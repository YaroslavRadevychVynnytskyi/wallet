package com.nerdysoft.dto.response;

import com.nerdysoft.dto.request.ConvertAmountRequestDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ConvertAmountResponseDto {
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal originalAmount;
    private BigDecimal convertedAmount;
    private BigDecimal exchangeRate;
    private LocalDateTime timestamp;

    public ConvertAmountResponseDto(
            ConvertAmountRequestDto requestDto,
            BigDecimal rateValue,
            BigDecimal convertedAmount
    ) {
        fromCurrency = requestDto.fromCurrency();
        toCurrency = requestDto.toCurrency();
        originalAmount = requestDto.amount();
        this.convertedAmount = convertedAmount;
        exchangeRate = rateValue;
        timestamp = LocalDateTime.now();
    }
}
