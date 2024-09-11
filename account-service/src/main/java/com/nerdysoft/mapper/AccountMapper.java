package com.nerdysoft.mapper;

import com.nerdysoft.config.MapperConfig;
import com.nerdysoft.dto.request.CreateAccountRequestDto;
import com.nerdysoft.dto.request.UpdateAccountRequestDto;
import com.nerdysoft.dto.response.AccountResponseDto;
import com.nerdysoft.dto.response.UpdatedAccountResponseDto;
import com.nerdysoft.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface AccountMapper {
    Account toModel(CreateAccountRequestDto requestDto);

    AccountResponseDto toDto(Account account);

    void updateFromDto(@MappingTarget Account account, UpdateAccountRequestDto requestDto);

    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    UpdatedAccountResponseDto toUpdateResponseDto(Account account);
}
