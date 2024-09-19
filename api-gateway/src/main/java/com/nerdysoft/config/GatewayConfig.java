package com.nerdysoft.config;

import com.nerdysoft.security.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    private final AuthFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("account-service", r -> r.path("/auth/**", "/accounts/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://account-service"))
                .build();
    }
}