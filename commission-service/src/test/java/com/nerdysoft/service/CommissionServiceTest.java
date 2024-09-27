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

    @Test
    void calculateCommission_SameCurrencyAndOver100Usd_ShouldReturnCorrectCommission() {
        //Given
        CalcCommissionRequestDto requestDto = new CalcCommissionRequestDto(
                UUID.fromString("a1e059d3-5854-4a64-a02c-1dfe1c154606"),
                BigDecimal.valueOf(200),
                "USD",
                "USD",
                "USD"
        );

        BigDecimal expected = BigDecimal.valueOf(1.5);

        when(commissionStrategy.get(requestDto.amount())).thenReturn(mediumAmountCommissionHandler);
        when(mediumAmountCommissionHandler.getCommission(requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                requestDto.amount())).thenReturn(expected);

        //When
        CommissionResponseDto actual = commissionService.calculateCommission(requestDto);

        //Then
        assertEquals(expected, actual.commissionAmount());

        verify(commissionStrategy, times(1)).get(requestDto.amount());
        verify(mediumAmountCommissionHandler, times(1)).getCommission(requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                requestDto.amount());
    }

    @Test
    void calculateCommission_DifferentFromAndTransactionCurrenciesAndOver100Usd_ShouldReturnCorrectCommission() {
        CalcCommissionRequestDto requestDto = new CalcCommissionRequestDto(
                UUID.fromString("a1e059d3-5854-4a64-a02c-1dfe1c154606"),
                BigDecimal.valueOf(200),
                "USD",
                "USD",
                "EUR"
        );

        ConvertAmountRequestDto covertRequestDto = new ConvertAmountRequestDto(
                "EUR",
                "USD",
                requestDto.amount()
        );

        ConvertAmountResponseDto convertResponseDto = new ConvertAmountResponseDto(
                "EUR",
                "USD",
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(223),
                BigDecimal.valueOf(1.11),
                LocalDateTime.now()
        );

        BigDecimal expected = BigDecimal.valueOf(3.35);

        when(currencyExchangeFeignClient.convert(covertRequestDto)).thenReturn(ResponseEntity.ofNullable(convertResponseDto));
        when(commissionStrategy.get(convertResponseDto.convertedAmount())).thenReturn(mediumAmountCommissionHandler);
        when(mediumAmountCommissionHandler.getCommission(
                requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                convertResponseDto.convertedAmount()
                )).thenReturn(expected);

        //When
        CommissionResponseDto actual = commissionService.calculateCommission(requestDto);

        //Then
        assertEquals(expected, actual.commissionAmount());

        verify(currencyExchangeFeignClient, times(1)).convert(covertRequestDto);
        verify(commissionStrategy, times(1)).get(convertResponseDto.convertedAmount());
        verify(mediumAmountCommissionHandler, times(1)).getCommission(
                requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                convertResponseDto.convertedAmount()
        );
    }

    @Test
    void calculateCommission_SameCurrencyAndOver1000Usd_ShouldReturnCorrectCommission() {
        CalcCommissionRequestDto requestDto = new CalcCommissionRequestDto(
                UUID.fromString("a1e059d3-5854-4a64-a02c-1dfe1c154606"),
                BigDecimal.valueOf(3000),
                "USD",
                "USD",
                "USD"
        );

        BigDecimal expected = BigDecimal.valueOf(15);

        when(commissionStrategy.get(requestDto.amount())).thenReturn(highAmountCommissionHandler);
        when(highAmountCommissionHandler.getCommission(requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                requestDto.amount())).thenReturn(expected);

        //When
        CommissionResponseDto actual = commissionService.calculateCommission(requestDto);

        //Then
        assertEquals(expected, actual.commissionAmount());

        verify(commissionStrategy, times(1)).get(requestDto.amount());
        verify(highAmountCommissionHandler, times(1)).getCommission(requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                requestDto.amount());
    }

    @Test
    void calculateCommission_DifferentFromAndToCurrencyAndOver1000Usd_ShouldReturnCorrectCommission() {
        CalcCommissionRequestDto requestDto = new CalcCommissionRequestDto(
                UUID.fromString("a1e059d3-5854-4a64-a02c-1dfe1c154606"),
                BigDecimal.valueOf(50_000),
                "UAH",
                "USD",
                "UAH"
        );

        ConvertAmountRequestDto covertRequestDto = new ConvertAmountRequestDto(
                "UAH",
                "USD",
                requestDto.amount()
        );

        ConvertAmountResponseDto convertResponseDto = new ConvertAmountResponseDto(
                "UAH",
                "USD",
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(1.207),
                BigDecimal.valueOf(0.024),
                LocalDateTime.now()
        );

        BigDecimal expectedInUsd = BigDecimal.valueOf(12.07);
        BigDecimal expectedInOriginalCurrency = BigDecimal.valueOf(499.98);

        when(currencyExchangeFeignClient.convert(covertRequestDto)).thenReturn(ResponseEntity.ofNullable(convertResponseDto));
        when(commissionStrategy.get(convertResponseDto.convertedAmount())).thenReturn(highAmountCommissionHandler);
        when(highAmountCommissionHandler.getCommission(
                requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                convertResponseDto.convertedAmount()
                )).thenReturn(expectedInUsd);

        ConvertAmountRequestDto convertToOriginalCurrencyRequestDto = new ConvertAmountRequestDto(
                "USD",
                "UAH",
                BigDecimal.valueOf(12.07)
        );

        ConvertAmountResponseDto convertToOriginalCurrencyResponseDto = new ConvertAmountResponseDto(
                "USD",
                "UAH",
                BigDecimal.valueOf(12.07),
                BigDecimal.valueOf(499.98),
                BigDecimal.valueOf(41.42),
                LocalDateTime.now()
        );

        when(currencyExchangeFeignClient.convert(convertToOriginalCurrencyRequestDto))
                .thenReturn(ResponseEntity.ofNullable(convertToOriginalCurrencyResponseDto));

        //When
        CommissionResponseDto actual = commissionService.calculateCommission(requestDto);

        //Then
        assertEquals(expectedInOriginalCurrency, actual.commissionAmount());

        verify(currencyExchangeFeignClient, times(1)).convert(covertRequestDto);
        verify(commissionStrategy, times(1)).get(convertResponseDto.convertedAmount());
        verify(highAmountCommissionHandler, times(1)).getCommission(
                requestDto.fromWalletCurrency(),
                requestDto.toWalletCurrency(),
                requestDto.transactionCurrency(),
                convertResponseDto.convertedAmount()
        );
        verify(currencyExchangeFeignClient, times(1)).convert(convertToOriginalCurrencyRequestDto);
    }
}
