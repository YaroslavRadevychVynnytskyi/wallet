package com.nerdysoft.controller;

import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.dto.api.request.LoginRequestDto;
import com.nerdysoft.dto.api.response.AuthResponseDto;
import com.nerdysoft.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> register(@RequestBody CreateAccountRequestDto dto) {
    return new ResponseEntity<>(new AuthResponseDto(authenticationService.register(dto)), HttpStatus.ACCEPTED);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto dto) {
    return new ResponseEntity<>(new AuthResponseDto(authenticationService.login(dto)), HttpStatus.ACCEPTED);
  }
}
