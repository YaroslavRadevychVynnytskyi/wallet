package com.nerdysoft.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

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
