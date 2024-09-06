package com.application.mapper;

import com.application.config.MapperConfig;
import com.application.dto.AccountResponseDto;
import com.application.dto.CreateAccountRequestDto;
import com.application.dto.UpdateAccountRequestDto;
import com.application.dto.UpdatedAccountResponseDto;
import com.application.entity.Account;
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
