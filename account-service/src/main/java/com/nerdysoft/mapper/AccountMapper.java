package com.nerdysoft.mapper;

import com.nerdysoft.config.MapperConfig;
import com.nerdysoft.dto.api.request.UpdateAccountRequestDto;
import com.nerdysoft.dto.api.response.AccountResponseDto;
import com.nerdysoft.dto.api.response.UpdatedAccountResponseDto;
import com.nerdysoft.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.control.DeepClone;

@Mapper(config = MapperConfig.class, mappingControl = DeepClone.class)
public interface AccountMapper {
    AccountResponseDto toDto(Account account);

    void updateFromDto(@MappingTarget Account account, UpdateAccountRequestDto requestDto);

    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    UpdatedAccountResponseDto toUpdateResponseDto(Account account);

    Account clone(Account in);
}
