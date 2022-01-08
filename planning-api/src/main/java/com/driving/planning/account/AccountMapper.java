package com.driving.planning.account;

import com.driving.planning.account.domain.Account;
import com.driving.planning.account.dto.AccountDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface AccountMapper {

    AccountDto toDto(Account account);

    Account toEntity(AccountDto dto);

}
