package com.nerdysoft.mapper;

import com.nerdysoft.dto.response.UserDetailsResponseDto;
import com.nerdysoft.entity.Account;
import com.nerdysoft.config.MapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AccountMapper {
  Account toUserDetails(UserDetailsResponseDto userDetailsResponseDto);
}
