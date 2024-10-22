package com.nerdysoft.security.service;

import com.nerdysoft.axon.command.account.CreateAccountCommand;
import com.nerdysoft.axon.query.account.FindAccountByEmailQuery;
import com.nerdysoft.axon.query.account.FindAccountByIdQuery;
import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.dto.api.request.LoginRequestDto;
import com.nerdysoft.model.Account;
import com.nerdysoft.security.util.JwtUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final AuthenticationManager authManager;
  private final JwtUtil jwtUtil;
  private final CommandGateway commandGateway;
  private final QueryGateway queryGateway;

  public String register(CreateAccountRequestDto dto) {
    CreateAccountCommand command = new CreateAccountCommand(dto.email(), dto.fullName(), dto.password());
    UUID accountId = commandGateway.sendAndWait(command);
    Account account = queryGateway.query(new FindAccountByIdQuery(accountId), Account.class).join();
    return jwtUtil.generateToken(account.getEmail());
  }

  public String login(LoginRequestDto dto) {
    authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
    Account account = queryGateway.query(new FindAccountByEmailQuery(dto.email()), Account.class).join();
    return jwtUtil.generateToken(account.getEmail());
  }
}
