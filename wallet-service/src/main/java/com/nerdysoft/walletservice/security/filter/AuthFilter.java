package com.nerdysoft.walletservice.security.filter;

import com.nerdysoft.walletservice.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {
  private final List<String> publicRoutes = List.of();

  private final JwtUtil jwtUtil;

  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    if (!isPublicRoute(request.getRequestURI())) {
      Optional<String> internalToken = Optional.ofNullable(request.getHeader("internal-token"));
      if (internalToken.isPresent()) {
        if (jwtUtil.isInternalTokenValid(internalToken.get())) {
          filterChain.doFilter(request, response);
        } else {
          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        return;
      }
      Optional<String> token = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));
      if (token.isPresent() && !token.get().isEmpty()) {
        if (jwtUtil.isTokenValid(token.get())) {
          filterChain.doFilter(request, response);
        } else {
          response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
      } else {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }

  private boolean isPublicRoute(String route) {
    return publicRoutes.stream().anyMatch(pattern -> pathMatcher.match(pattern, route));
  }
}
