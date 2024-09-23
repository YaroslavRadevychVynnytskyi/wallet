package com.nerdysoft.walletservice.feign;

import com.nerdysoft.walletservice.dto.request.ConvertAmountRequestDto;
import com.nerdysoft.walletservice.dto.response.ConvertAmountResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "api-gateway")
public interface CurrencyExchangeFeignClient {
  @PostMapping("/currency-exchange-service/exchange-rate/convert")
  ResponseEntity<ConvertAmountResponseDto> convert(@RequestBody ConvertAmountRequestDto requestDto);
}