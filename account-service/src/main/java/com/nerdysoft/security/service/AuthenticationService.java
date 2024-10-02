package com.nerdysoft.security.service;

import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.dto.api.request.LoginRequestDto;
import com.nerdysoft.dto.api.response.AuthResponseDto;
import com.nerdysoft.entity.Account;
import com.nerdysoft.security.util.JwtUtil;
import com.nerdysoft.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final AuthenticationManager authManager;
  private final AccountService accountService;
  private final JwtUtil jwtUtil;

  public AuthResponseDto register(CreateAccountRequestDto requestDto) {
    Account account = accountService.create(requestDto);
    return new AuthResponseDto(jwtUtil.generateToken(account));
  }

  public AuthResponseDto login(LoginRequestDto requestDto) {
    Authentication authenticate = authManager.authenticate(new UsernamePasswordAuthenticationToken(
            requestDto.email(), requestDto.password()
    ));
    Account account = accountService.findByEmail(authenticate.getName());
    return new AuthResponseDto(jwtUtil.generateToken(account));
  }
}
