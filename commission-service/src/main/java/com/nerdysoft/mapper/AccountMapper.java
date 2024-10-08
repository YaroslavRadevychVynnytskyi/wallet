package com.nerdysoft.mapper;

import com.nerdysoft.config.MapperConfig;
import com.nerdysoft.dto.security.Account;
import com.nerdysoft.dto.security.UserDetailsResponseDto;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AccountMapper {
    Account toUserDetails(UserDetailsResponseDto userDetailsResponseDto);
}
