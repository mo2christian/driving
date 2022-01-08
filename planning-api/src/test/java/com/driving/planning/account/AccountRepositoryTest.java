package com.driving.planning.account;

import com.driving.planning.MongodbTestResource;
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
        Assertions.assertThat(accountRepository.list()).isEmpty();
    }

}