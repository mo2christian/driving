package com.driving.planning.account;

import com.driving.planning.MongodbTestResource;
import com.driving.planning.account.dto.AccountDto;
import com.driving.planning.config.database.Tenant;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.inject.Inject;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@QuarkusTestResource(MongodbTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
class AccountServiceTest {

    @Inject
    AccountService accountService;

    @InjectMock
    Tenant tenant;

    @Order(1)
    @Test
    void list(){
        selectPseudoSchema();
        assertThat(accountService.list()).isEmpty();
    }

    @Order(2)
    @Test
    void createAccount(){
        var dto = generateAccount();
        selectPseudoSchema();
        accountService.createAccount("pseudo", dto);
        assertThat(accountService.list())
                .hasSize(1)
                .element(0)
                .extracting(AccountDto::getPassword)
                .isNotEqualTo(dto.getPassword());
    }

    @Order(3)
    @Test
    void checkValidAccount(){
        var dto = generateAccount();
        selectPseudoSchema();
        assertThat(accountService.isValidAccount(dto)).isTrue();
    }

    @Order(3)
    @Test
    void checkInvalidAccount(){
        var dto = generateAccount();
        dto.setPassword("wrongpwd");
        selectPseudoSchema();
        assertThat(accountService.isValidAccount(dto)).isFalse();
    }

    private AccountDto generateAccount(){
        var dto = new AccountDto();
        dto.setEmail("test@test.com");
        dto.setPassword("test");
        return dto;
    }

    private void selectPseudoSchema(){
        when(tenant.getName()).thenReturn("pseudo");
    }

}
