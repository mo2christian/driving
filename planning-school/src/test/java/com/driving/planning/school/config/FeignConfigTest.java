package com.driving.planning.school.config;

import com.driving.planning.client.AccountApiClient;
import com.driving.planning.client.model.AccountDto;
import com.driving.planning.school.common.exception.ApiException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest()
@ContextConfiguration(classes = { WireMockConfig.class })
class FeignConfigTest {

    @Autowired
    WireMockServer mockServer;

    @Autowired
    AccountApiClient accountApiClient;

    @BeforeEach
    void before(){
        mockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/api/v1/accounts/check"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }

    @Test
    void handleError(){
        var account = new AccountDto();
        Assertions.assertThatThrownBy(() -> accountApiClient.apiV1AccountsCheckPost("test", account))
                .isInstanceOf(ApiException.class);
    }

}
