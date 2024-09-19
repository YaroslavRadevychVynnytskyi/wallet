package com.nerdysoft.service;

import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.dto.api.request.SignInRequestDto;
import com.nerdysoft.dto.api.response.AccountResponseDto;
import com.nerdysoft.dto.api.response.SignInResponseDto;

public interface SecurityService {
  AccountResponseDto signUp(CreateAccountRequestDto createAccountRequestDto);

  SignInResponseDto signIn(SignInRequestDto signInRequestDto);
}
