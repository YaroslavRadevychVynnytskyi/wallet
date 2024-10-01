package com.nerdysoft.feign;

import com.nerdysoft.dto.feign.ConvertAmountRequestDto;
import com.nerdysoft.dto.feign.ConvertAmountResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "api-gateway")
public interface CurrencyExchangeFeignClient {
    @PostMapping("currency-exchange-service/exchange-rate/convert")
    ResponseEntity<ConvertAmountResponseDto> convert(@RequestBody ConvertAmountRequestDto requestDto);
}
