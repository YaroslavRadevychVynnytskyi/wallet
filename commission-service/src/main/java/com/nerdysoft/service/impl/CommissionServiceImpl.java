package com.nerdysoft.service.impl;

import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import com.nerdysoft.dto.api.request.SaveCommissionRequestDto;
import com.nerdysoft.dto.api.response.CalcCommissionResponseDto;
import com.nerdysoft.dto.feign.ConvertAmountRequestDto;
import com.nerdysoft.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.mapper.CommissionMapper;
import com.nerdysoft.entity.Commission;
import com.nerdysoft.repo.CommissionRepository;
import com.nerdysoft.service.CommissionService;
import com.nerdysoft.service.strategy.CommissionStrategy;
import com.nerdysoft.service.strategy.handler.CommissionHandler;
import com.nerdysoft.service.strategy.handler.LoanCommissionHandler;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionServiceImpl implements CommissionService {
    private final CurrencyExchangeFeignClient currencyExchangeFeignClient;
    private final LoanCommissionHandler loanCommissionHandler;
    private final CommissionRepository commissionRepository;
    private final CommissionStrategy commissionStrategy;
    private final CommissionMapper commissionMapper;

    @Override
    public CalcCommissionResponseDto calculateCommission(CalcCommissionRequestDto requestDto) {
        BigDecimal walletAmount = convertToUsd(requestDto, CalcCommissionRequestDto::getWalletAmount);
        BigDecimal loanLimitAmount = convertToUsd(requestDto, CalcCommissionRequestDto::getLoanLimitAmount);

        List<CommissionHandler> commissionHandlers = commissionStrategy.get(walletAmount, requestDto.isLoanLimitUsed());

        BigDecimal totalCommissionAmount = commissionHandlers.stream()
                .map(ch -> calculateCommissionForHandler(ch, requestDto, loanLimitAmount, walletAmount))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal originalCurrencyCommission = convertCommissionToOriginalCurrency(requestDto.getFromWalletCurrency(), totalCommissionAmount);

        return new CalcCommissionResponseDto(totalCommissionAmount, originalCurrencyCommission, requestDto);
    }

    @Override
    public Commission saveCommission(SaveCommissionRequestDto requestDto) {
        Commission commission = commissionMapper.toCommission(requestDto);
        return commissionRepository.save(commission);
    }

    @Override
    public Commission findById(UUID id) {
        return commissionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Commission not found"));
    }

    private BigDecimal convertToUsd(CalcCommissionRequestDto requestDto,
                                    Function<CalcCommissionRequestDto, BigDecimal> amountResolver) {
        BigDecimal amount = amountResolver.apply(requestDto);

        if (!requestDto.getFromWalletCurrency().equals("USD")) {
            return currencyExchangeFeignClient.convert(new ConvertAmountRequestDto(
                    requestDto.getFromWalletCurrency(),
                    "USD",
                    amount)
            ).getBody().convertedAmount();
        }
        return amount;
    }

    private BigDecimal calculateCommissionForHandler(CommissionHandler handler,
                                                     CalcCommissionRequestDto requestDto,
                                                     BigDecimal loanLimitAmount,
                                                     BigDecimal walletAmount) {
        return handler.equals(loanCommissionHandler)
                ? handler.getCommission(requestDto, loanLimitAmount)
                : handler.getCommission(requestDto, walletAmount);
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
