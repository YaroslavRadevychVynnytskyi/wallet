package com.nerdysoft.dto.request;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
public abstract class TransactionRequestDto extends WalletOperationRequestDto {}
