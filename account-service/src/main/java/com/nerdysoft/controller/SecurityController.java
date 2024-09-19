package com.nerdysoft.controller;

import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.dto.api.request.SignInRequestDto;
import com.nerdysoft.dto.api.response.AccountResponseDto;
import com.nerdysoft.dto.api.response.SignInResponseDto;
import com.nerdysoft.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
@RequiredArgsConstructor
public class SecurityController {
  private final SecurityService securityService;

  @PostMapping("/sign-up")
  public ResponseEntity<AccountResponseDto> signUp(@RequestBody CreateAccountRequestDto createAccountRequestDto) {
    return new ResponseEntity<>(securityService.signUp(createAccountRequestDto), HttpStatus.CREATED);
  }

  @PostMapping("/sign-in")
  public ResponseEntity<SignInResponseDto> signIn(@RequestBody SignInRequestDto signInRequestDto) {
    return new ResponseEntity<>(securityService.signIn(signInRequestDto), HttpStatus.ACCEPTED);
  }
}
