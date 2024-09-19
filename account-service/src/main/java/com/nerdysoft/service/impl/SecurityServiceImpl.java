package com.nerdysoft.service.impl;

import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.dto.api.request.SignInRequestDto;
import com.nerdysoft.dto.api.response.AccountResponseDto;
import com.nerdysoft.dto.api.response.SignInResponseDto;
import com.nerdysoft.entity.Account;
import com.nerdysoft.mapper.AccountMapper;
import com.nerdysoft.service.AccountService;
import com.nerdysoft.service.JwtService;
import com.nerdysoft.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {
  private final AccountService accountService;

  private final PasswordEncoder passwordEncoder;

  private final AuthenticationManager authenticationManager;

  private final JwtService jwtService;

  private final AccountMapper accountMapper;

  @Override
  public AccountResponseDto signUp(CreateAccountRequestDto createAccountRequestDto) {
    CreateAccountRequestDto encodedCreateAccountRequestDto = new CreateAccountRequestDto(
        createAccountRequestDto.fullName(),
        createAccountRequestDto.email(),
        passwordEncoder.encode(createAccountRequestDto.password())
    );
    Account account = accountService.create(encodedCreateAccountRequestDto);
    String jwtToken = jwtService.generateJwtToken(account);
    AccountResponseDto accountResponseDto = accountMapper.toDto(account);
    accountResponseDto.setAccessToken(jwtToken);
    return accountResponseDto;
  }

  @Override
  public SignInResponseDto signIn(SignInRequestDto signInRequestDto) {
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(signInRequestDto.email(), signInRequestDto.password());
    authenticationManager.authenticate(authenticationToken);
    Account account = accountService.findByEmail(signInRequestDto.email());
    return new SignInResponseDto(jwtService.generateJwtToken(account));
  }
}
