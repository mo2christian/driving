package com.driving.planning.account;

import com.driving.planning.account.dto.AccountDto;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static io.restassured.RestAssured.given;

@QuarkusTest
class AccountResourceTest {

    @InjectMock
    AccountService accountService;

    @Test
    void getAdmins(){
        var dto = new AccountDto();
        dto.setPassword("pwd");
        dto.setEmail("test@test.com");
        Mockito.when(accountService.list()).thenReturn(Collections.singletonList(dto));
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/accounts")
                .then()
                .statusCode(200)
                .body("accounts[0].email", Matchers.is(dto.getEmail()))
                .body("accounts[0].password", Matchers.containsStringIgnoringCase("XXX"))
                .body("accounts.size()", Matchers.is(1));
        Mockito.verify(accountService, Mockito.times(1)).list();
    }

    @Test
    void checkAccount(){
        var dto = new AccountDto();
        dto.setPassword("pwd");
        dto.setEmail("test@test.com");
        Mockito.when(accountService.isValidAccount(dto)).thenReturn(true);
        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/v1/accounts/check")
                .then()
                .statusCode(200)
                .body("message", Matchers.not(Matchers.emptyOrNullString()));
        Mockito.verify(accountService, Mockito.times(1)).isValidAccount(dto);
    }

    @Test
    void checkInvalidAccount(){
        var dto = new AccountDto();
        dto.setPassword("pwd");
        dto.setEmail("test@test.com");
        Mockito.when(accountService.isValidAccount(dto)).thenReturn(false);
        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/v1/accounts/check")
                .then()
                .statusCode(400)
                .body("message", Matchers.not(Matchers.emptyOrNullString()));
        Mockito.verify(accountService, Mockito.times(1)).isValidAccount(dto);
    }

}
