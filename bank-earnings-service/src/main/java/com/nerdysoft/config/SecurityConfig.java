package com.nerdysoft.config;

import com.nerdysoft.security.filter.AuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final UserDetailsService userDetailsService;

  private final AuthFilter authFilter;

  @Value("${application.internal-token}")
  private String internalToken;

  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    String[] permittedRoutes = {
        "/error"
    };

    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(requests -> requests
            .requestMatchers(permittedRoutes).permitAll()
            .requestMatchers("/reserves/**")
                .access((authentication, authorizationContext) -> hasAdminRoleOrInternalToken(authorizationContext.getRequest(), authentication.get()))
            .anyRequest().authenticated()
        )
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
        .userDetailsService(userDetailsService)
        .build();
  }

  private AuthorizationDecision hasAdminRoleOrInternalToken(HttpServletRequest request,
                                                            Authentication authentication) {
    Optional<String> internalToken = Optional.ofNullable(request.getHeader("internal-token"));
    boolean decision = internalToken.map(token -> this.internalToken.equals(token))
            .orElseGet(() -> authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")));
    return new AuthorizationDecision(decision);
  }
}
