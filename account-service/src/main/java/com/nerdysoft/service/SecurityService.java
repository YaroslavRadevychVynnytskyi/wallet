package com.nerdysoft.service;

import com.nerdysoft.dto.request.CreateAccountRequestDto;
import com.nerdysoft.dto.request.SignInRequestDto;
import com.nerdysoft.dto.response.AccountResponseDto;
import com.nerdysoft.dto.response.SignInResponseDto;

public interface SecurityService {
  AccountResponseDto   signUp(CreateAccountRequestDto createAccountRequestDto);

  SignInResponseDto signIn(SignInRequestDto signInRequestDto);
}
