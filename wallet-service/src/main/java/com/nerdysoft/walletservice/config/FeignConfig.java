package com.nerdysoft.walletservice.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
  @Value("${application.internal-token}")
  private String internalToken;

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate ->
        requestTemplate.header("internal-token", internalToken);
  }
}
