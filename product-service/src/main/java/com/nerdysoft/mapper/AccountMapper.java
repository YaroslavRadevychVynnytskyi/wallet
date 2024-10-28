package com.nerdysoft.mapper;

import com.nerdysoft.config.MapperConfig;
import com.nerdysoft.dto.api.response.UserDetailsResponseDto;
import com.nerdysoft.model.security.Account;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AccountMapper {
  Account toUserDetails(UserDetailsResponseDto userDetailsResponseDto);
}
