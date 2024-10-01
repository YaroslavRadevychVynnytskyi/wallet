package com.nerdysoft.service.impl;

import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import com.nerdysoft.dto.api.response.CommissionResponseDto;
import com.nerdysoft.dto.feign.ConvertAmountRequestDto;
import com.nerdysoft.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.service.CommissionService;
import com.nerdysoft.service.strategy.CommissionStrategy;
import com.nerdysoft.service.strategy.handler.CommissionHandler;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionServiceImpl implements CommissionService {
    private final CurrencyExchangeFeignClient currencyExchangeFeignClient;
    private final CommissionStrategy commissionStrategy;

    @Override
    public CommissionResponseDto calculateCommission(CalcCommissionRequestDto requestDto) {
        BigDecimal amount = convertAmountToUsd(requestDto);
        CommissionHandler commissionHandler = commissionStrategy.get(amount);

        BigDecimal commission = commissionHandler.getCommission(
                requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                amount);

        BigDecimal originalCurrencyCommission = convertCommissionToOriginalCurrency(requestDto.fromWalletCurrency(), commission);
        return new CommissionResponseDto(requestDto.transactionId(), originalCurrencyCommission);
    }

    private BigDecimal convertAmountToUsd(CalcCommissionRequestDto requestDto) {
        BigDecimal amount = requestDto.amount();

        if (!requestDto.transactionCurrency().equals("USD")) {
            amount = currencyExchangeFeignClient.convert(new ConvertAmountRequestDto(
                    requestDto.transactionCurrency(),
                    "USD",
                    requestDto.amount()
            )).getBody().convertedAmount();
        }

        return amount;
    }

    private BigDecimal convertCommissionToOriginalCurrency(String originalCurrency, BigDecimal commission) {
        if (!originalCurrency.equals("USD")) {
            commission = currencyExchangeFeignClient.convert(new ConvertAmountRequestDto(
                    "USD",
                    originalCurrency,
                    commission
            )).getBody().convertedAmount();
        }

        return commission;
    }
}
