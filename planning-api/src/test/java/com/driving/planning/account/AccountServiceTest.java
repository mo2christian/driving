package com.driving.planning.account;

import com.driving.planning.account.domain.Account;
import com.driving.planning.account.dto.AccountDto;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
class AccountServiceTest {

    @Inject
    AccountService accountService;

    @InjectMock
    AccountRepository accountRepository;

    @Test
    void list(){
        when(accountRepository.listAll()).thenReturn(Collections.emptyList());
        assertThat(accountService.list()).isEmpty();
    }

    @Test
    void createAccount(){
        var dto = generateAccount();
        accountService.createAccount("pseudo", dto);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).createInSchema(eq("pseudo"), accountCaptor.capture());
        assertThat(accountCaptor.getValue())
                .extracting(Account::getPassword)
                .isNotEqualTo(dto.getPassword());
    }

    @Test
    void checkValidAccount(){

        var dto = generateAccount();
        var account = new Account();
        account.setEmail(dto.getEmail());
        account.setPassword(accountService.hash(dto.getPassword()));
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.of(account));
        assertThat(accountService.isValidAccount(dto)).isTrue();
    }

    @Order(3)
    @Test
    void checkInvalidAccount(){
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        var dto = generateAccount();
        assertThat(accountService.isValidAccount(dto)).isFalse();
    }

    private AccountDto generateAccount(){
        var dto = new AccountDto();
        dto.setEmail("test@test.com");
        dto.setPassword("test");
        return dto;
    }

}
