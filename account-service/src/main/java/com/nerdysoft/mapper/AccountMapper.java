package com.nerdysoft.mapper;

import com.nerdysoft.config.MapperConfig;
import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.dto.api.request.UpdateAccountRequestDto;
import com.nerdysoft.dto.api.response.AccountResponseDto;
import com.nerdysoft.dto.api.response.UpdatedAccountResponseDto;
import com.nerdysoft.dto.api.response.UserDetailsDto;
import com.nerdysoft.entity.Account;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.control.DeepClone;
import org.springframework.security.core.GrantedAuthority;

@Mapper(config = MapperConfig.class, mappingControl = DeepClone.class)
public interface AccountMapper {
    Account toModel(CreateAccountRequestDto requestDto);

    AccountResponseDto toDto(Account account);

    void updateFromDto(@MappingTarget Account account, UpdateAccountRequestDto requestDto);

    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    UpdatedAccountResponseDto toUpdateResponseDto(Account account);

    Account clone(Account in);

    @Mapping(target = "authorities", expression = "java(toSetString(account.getAuthorities()))")
    UserDetailsDto toUserDetailsDto(Account account);

    default Set<String> toSetString(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
    }
}
