package com.nerdysoft.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nerdysoft.dto.generic.ConversionRatesDto;
import com.nerdysoft.dto.request.AddOrUpdateRateRequestDto;
import com.nerdysoft.dto.request.ConvertAmountRequestDto;
import com.nerdysoft.dto.request.ExchangeRateRequestDto;
import com.nerdysoft.dto.response.AddOrUpdateRateResponseDto;
import com.nerdysoft.dto.response.ConvertAmountResponseDto;
import com.nerdysoft.dto.response.ExchangeRateResponseDto;
import com.nerdysoft.entity.ExchangeRate;
import com.nerdysoft.repo.ExchangeRateRepository;
import com.nerdysoft.service.impl.CurrencyExchangeServiceImpl;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class CurrencyExchangeServiceTest {
    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WebClient webClient;
    
    @InjectMocks
    private CurrencyExchangeServiceImpl currencyExchangeService;

    @Test
    void getExchangeRate_AllOk_ShouldReturnCorrectExchangeRateResponseDto() {
        //Given
        ExchangeRateRequestDto requestDto = new ExchangeRateRequestDto(
                "UAH",
                "USD"
        );

        Map<String, BigDecimal> conversionRates = new HashMap<>();
        conversionRates.put("USD", BigDecimal.valueOf(0.02437));

        ExchangeRate exchangeRate = new ExchangeRate(
                "66de9cc56973951c25457a9c",
                "UAH",
                conversionRates,
                LocalDateTime.now().minusHours(1)
        );

        ExchangeRateResponseDto expected = new ExchangeRateResponseDto(exchangeRate, requestDto.toCurrency());

        when(exchangeRateRepository.findByFromCurrencyAndToCurrency(
                requestDto.fromCurrency(),
                requestDto.toCurrency(),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusDays(1))
        )
                .thenReturn(Optional.of(exchangeRate));

        //When
        ExchangeRateResponseDto actual = currencyExchangeService.getExchangeRate(requestDto);

        //Then
        assertEquals(expected, actual);

        verify(exchangeRateRepository, times(1)).findByFromCurrencyAndToCurrency(
                requestDto.fromCurrency(),
                requestDto.toCurrency(),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusDays(1)
        );
    }

    @Test
    void getExchangeRate_NonExistentRate_ShouldThrowNoSuchElementException() {
        //Given
        ExchangeRateRequestDto requestDto = new ExchangeRateRequestDto(
                "UAH",
                "XXX"
        );

        when(exchangeRateRepository.findByFromCurrencyAndToCurrency(
                requestDto.fromCurrency(),
                requestDto.toCurrency(),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusDays(1))
        )
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> currencyExchangeService.getExchangeRate(requestDto));

        verify(exchangeRateRepository, times(1)).findByFromCurrencyAndToCurrency(requestDto.fromCurrency(),
                requestDto.toCurrency(),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusDays(1));
    }

    @Test
    void addOrUpdateExchangeRate_AllOk_ShouldReturnCorrectAddOrUpdateResponseDto() {
        //Given
        AddOrUpdateRateRequestDto requestDto = new AddOrUpdateRateRequestDto(
                "USD",
                "EUR",
                BigDecimal.valueOf(2)
        );

        Map<String, BigDecimal> conversionRates = new HashMap<>();
        conversionRates.put("EUR", BigDecimal.valueOf(0.91));

        ExchangeRate exchangeRate = ExchangeRate.builder()
                .id("66de9cc56973951c25457b2e")
                .baseCode("USD")
                .conversionRates(conversionRates)
                .build();


        when(exchangeRateRepository.findByBaseCode(requestDto.fromCurrency())).thenReturn(Optional.of(exchangeRate));
        when(exchangeRateRepository.save(exchangeRate)).thenReturn(exchangeRate);

        //When
        AddOrUpdateRateResponseDto actual = currencyExchangeService.addOrUpdateExchangeRate(requestDto);

        //Then
        assertEquals("success", actual.status());
        assertEquals(BigDecimal.valueOf(2), exchangeRate.getConversionRates().get(requestDto.toCurrency()));

        verify(exchangeRateRepository, times(1)).findByBaseCode(requestDto.fromCurrency());
        verify(exchangeRateRepository, times(1)).save(exchangeRate);
    }

    @Test
    void addOrUpdateExchangeRate_NonExistentBaseCode_ShouldThrowNoSuchElementException() {
        AddOrUpdateRateRequestDto requestDto = new AddOrUpdateRateRequestDto(
                "USD",
                "EUR",
                BigDecimal.valueOf(2)
        );

        when(exchangeRateRepository.findByBaseCode(requestDto.fromCurrency())).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> currencyExchangeService.addOrUpdateExchangeRate(requestDto));

        assertEquals("No value present", exception.getMessage());

        verify(exchangeRateRepository, times(1)).findByBaseCode(requestDto.fromCurrency());
    }

    @Test
    void convert_AllOk_ShouldReturnCorrectConvertAmountResponseDto() {
        //Given
        ConvertAmountRequestDto requestDto = new ConvertAmountRequestDto(
                "USD",
                "EUR",
                BigDecimal.valueOf(100)
        );

        Map<String, BigDecimal> conversionRates = new HashMap<>();
        conversionRates.put("EUR", BigDecimal.valueOf(0.91));

        ExchangeRate exchangeRate = new ExchangeRate(
                "66de9cc56973951c25457j5d",
                "USD",
                conversionRates,
                LocalDateTime.now().minusHours(1)
        );

        BigDecimal expected = requestDto.amount().multiply(BigDecimal.valueOf(0.91));

        when(exchangeRateRepository.findByFromCurrencyAndToCurrency(
                requestDto.fromCurrency(),
                requestDto.toCurrency(),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusDays(1))
        ).thenReturn(Optional.of(exchangeRate));

        //When
        ConvertAmountResponseDto actual = currencyExchangeService.convert(requestDto);

        //Then
        assertEquals(expected, actual.getConvertedAmount());

        verify(exchangeRateRepository, times(1)).findByFromCurrencyAndToCurrency(
                requestDto.fromCurrency(),
                requestDto.toCurrency(),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusDays(1));
    }

    @Test
    void fetchExchangeRates_AllOk_ShouldReturnCorrectExchangeRate() throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {
        Method fetchExchangeRatesMethod = CurrencyExchangeServiceImpl.class.getDeclaredMethod("fetchExchangeRates", String.class);
        fetchExchangeRatesMethod.setAccessible(true);

        //Given
        String baseCode = "USD";

        ConversionRatesDto ratesDto = new ConversionRatesDto(
                Map.of( "EUR", BigDecimal.valueOf(0.85),
                        "GBP", BigDecimal.valueOf(0.75))
        );

        ExchangeRate expected = ExchangeRate.builder()
                .baseCode(baseCode)
                .conversionRates(ratesDto.conversionRates())
                .timestamp(LocalDateTime.now())
                .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ConversionRatesDto.class)).thenReturn(Mono.just(ratesDto));

        //When
        ExchangeRate actual = (ExchangeRate) fetchExchangeRatesMethod.invoke(currencyExchangeService, baseCode);

        //Then
        assertEquals(ratesDto.conversionRates(), actual.getConversionRates());
        assertEquals(expected, actual);

        verify(webClient, times(1)).get();
        verify(requestHeadersUriSpec, times(1)).uri(anyString());
        verify(requestHeadersSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToMono(ConversionRatesDto.class);
    }
}
