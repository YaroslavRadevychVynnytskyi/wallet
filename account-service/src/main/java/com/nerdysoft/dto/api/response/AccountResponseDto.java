package com.nerdysoft.dto.api.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountResponseDto {
  private UUID accountId;
  private String fullName;
  private String email;
  private LocalDateTime createdAt;
}
