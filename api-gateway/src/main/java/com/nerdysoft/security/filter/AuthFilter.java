package com.nerdysoft.security.filter;

import com.nerdysoft.security.service.RouterCheckerService;
import com.nerdysoft.security.service.TokenValidatorService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter {
  private final TokenValidatorService tokenValidatorService;

  private final RouterCheckerService routerCheckerService;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    if (!routerCheckerService.isOpenedRequest.test(exchange.getRequest().getURI().getPath())) {
      if (exchange.getRequest().getHeaders().containsKey("internal-token")) {
        String internalToken = exchange.getRequest().getHeaders().getFirst("internal-token");
        if (tokenValidatorService.isInternalTokenValid(internalToken)) {
          return chain.filter(exchange);
        } else {
          return declineRequest(exchange, HttpStatus.FORBIDDEN);
        }
      }

      if (exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!token.isEmpty()) {
          if (tokenValidatorService.isTokenValid(token)) {
            return chain.filter(exchange);
          } else {
            return declineRequest(exchange, HttpStatus.FORBIDDEN);
          }
        } else {
          return declineRequest(exchange, HttpStatus.UNAUTHORIZED);
        }
      } else {
        return declineRequest(exchange, HttpStatus.UNAUTHORIZED);
      }
    } else {
      return chain.filter(exchange);
    }
  }

  private Mono<Void> declineRequest(ServerWebExchange exchange, HttpStatus status) {
    exchange.getResponse().setStatusCode(status);
    return exchange.getResponse().setComplete();
  }
}
