package com.nerdysoft.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import com.nerdysoft.dto.api.response.CommissionResponseDto;
import com.nerdysoft.dto.feign.ConvertAmountRequestDto;
import com.nerdysoft.dto.feign.ConvertAmountResponseDto;
import com.nerdysoft.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.service.impl.CommissionServiceImpl;
import com.nerdysoft.service.strategy.CommissionStrategy;
import com.nerdysoft.service.strategy.handler.HighAmountCommissionHandler;
import com.nerdysoft.service.strategy.handler.LowAmountCommissionHandler;
import com.nerdysoft.service.strategy.handler.MediumAmountCommissionHandler;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class CommissionServiceTest {
    @Mock
    private CurrencyExchangeFeignClient currencyExchangeFeignClient;

    @Mock
    private CommissionStrategy commissionStrategy;

    @Mock
    private LowAmountCommissionHandler lowAmountCommissionHandler;

    @Mock
    private MediumAmountCommissionHandler mediumAmountCommissionHandler;

    @Mock
    private HighAmountCommissionHandler highAmountCommissionHandler;

    @InjectMocks
    private CommissionServiceImpl commissionService;

    @Test
    void calculateCommission_SameCurrencyAndUnder100Usd_ShouldReturnCorrectCommission() {
        //Given
        CalcCommissionRequestDto requestDto = new CalcCommissionRequestDto(
                UUID.fromString("a1e059d3-5854-4a64-a02c-1dfe1c154606"),
                BigDecimal.valueOf(90),
                "USD",
                "USD",
                "USD"
        );

        BigDecimal expected = BigDecimal.valueOf(0.9);

        when(commissionStrategy.get(requestDto.amount())).thenReturn(lowAmountCommissionHandler);
        when(lowAmountCommissionHandler.getCommission(
                requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                requestDto.amount())
        )
                .thenReturn(expected);

        //When
        CommissionResponseDto actual = commissionService.calculateCommission(requestDto);

        //Then
        assertEquals(expected, actual.commissionAmount());

        verify(commissionStrategy, times(1)).get(requestDto.amount());

        verify(lowAmountCommissionHandler, times(1)).getCommission(
                requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                requestDto.amount());
    }

    @Test
    void calculateCommission_DifferentFromAndToCurrencyAndUnder100Usd_ShouldReturnCorrectCommission() {
        //Given
        CalcCommissionRequestDto requestDto = new CalcCommissionRequestDto(
                UUID.fromString("a1e059d3-5854-4a64-a02c-1dfe1c154606"),
                BigDecimal.valueOf(90),
                "USD",
                "UAH",
                "USD"
        );

        BigDecimal expected = BigDecimal.valueOf(0.04);

        when(commissionStrategy.get(requestDto.amount())).thenReturn(lowAmountCommissionHandler);
        when(lowAmountCommissionHandler.getCommission(
                requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                requestDto.amount())
        )
                .thenReturn(expected);

        //When
        CommissionResponseDto actual = commissionService.calculateCommission(requestDto);

        //Then
        assertEquals(expected, actual.commissionAmount());

        verify(commissionStrategy, times(1)).get(requestDto.amount());
        verify(lowAmountCommissionHandler, times(1)).getCommission(
                requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                requestDto.amount());
    }

    @Test
    void calculateCommission_DifferentFromAndTransactionCurrencyAndUnder100Usd_ShouldReturnCorrectCommission() {
        //Given
        CalcCommissionRequestDto requestDto = new CalcCommissionRequestDto(
                UUID.fromString("a1e059d3-5854-4a64-a02c-1dfe1c154606"),
                BigDecimal.valueOf(2000),
                "USD",
                "UAH",
                "UAH"
        );

        BigDecimal expectedInUsd = BigDecimal.valueOf(0.97);

        ConvertAmountRequestDto convertRequestDto = new ConvertAmountRequestDto(
                "UAH",
                "USD",
                requestDto.amount()
        );

        ConvertAmountResponseDto convertResponseDto = new ConvertAmountResponseDto(
                "UAH",
                "USD",
                requestDto.amount(),
                BigDecimal.valueOf(48.5),
                BigDecimal.valueOf(0.02416),
                LocalDateTime.now()
        );

        when(currencyExchangeFeignClient.convert(convertRequestDto))
                .thenReturn(ResponseEntity.ofNullable(convertResponseDto));
        when(commissionStrategy.get(convertResponseDto.convertedAmount())).thenReturn(lowAmountCommissionHandler);
        when(lowAmountCommissionHandler.getCommission(
                requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                convertResponseDto.convertedAmount()
        )).thenReturn(expectedInUsd);

        //When
        CommissionResponseDto actual = commissionService.calculateCommission(requestDto);

        //Then
        assertEquals(expectedInUsd, actual.commissionAmount());

        verify(currencyExchangeFeignClient, times(1)).convert(convertRequestDto);
        verify(commissionStrategy, times(1)).get(convertResponseDto.convertedAmount());
        verify(lowAmountCommissionHandler, times(1)).getCommission(
                requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                convertResponseDto.convertedAmount());
    }
}
