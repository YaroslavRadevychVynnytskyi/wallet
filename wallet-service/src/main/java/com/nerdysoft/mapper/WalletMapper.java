package com.nerdysoft.mapper;

import com.nerdysoft.config.MapperConfig;
import com.nerdysoft.dto.request.CreateWalletDto;
import com.nerdysoft.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface WalletMapper {
  @Mapping(target = "accountId", source = "accountId")
  @Mapping(target = "balance", expression = "java(java.math.BigDecimal.valueOf(0.0))")
  @Mapping(target = "currency", source = "currency")
  Wallet toWallet(CreateWalletDto createWalletDto);
}
