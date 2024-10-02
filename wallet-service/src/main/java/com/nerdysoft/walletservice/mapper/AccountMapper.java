package com.nerdysoft.walletservice.mapper;

import com.nerdysoft.walletservice.config.MapperConfig;
import com.nerdysoft.walletservice.dto.response.UserDetailsResponseDto;
import com.nerdysoft.walletservice.model.Account;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AccountMapper {
  Account toUserDetails(UserDetailsResponseDto userDetailsResponseDto);
}
