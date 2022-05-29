package com.driving.planning.account;

import com.driving.planning.MongodbTestResource;
import com.driving.planning.account.domain.Account;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTestResource(MongodbTestResource.class)
@QuarkusTest
class AccountRepositoryTest {

    @Inject
    AccountRepository accountRepository;

    @Test
    void list(){
        Assertions.assertThat(accountRepository.listAll()).isEmpty();
    }

    @Test
    void findByEmail(){
        Assertions.assertThat(accountRepository.findByEmail("titi@toto.com")).isEmpty();
    }

    @Test
    void createInSchema(){
        var account = new Account();
        account.setEmail("toto@toto.com");
        account.setPassword("test");
        accountRepository.createInSchema("base", account);

        Assertions.assertThat(accountRepository.findByEmail("toto@toto.com")).isNotEmpty();
    }

}