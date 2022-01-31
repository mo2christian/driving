package com.driving.planning.school;

import com.driving.planning.Generator;
import com.driving.planning.school.dto.SchoolRequest;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SchoolResourceIT {

    private static String pseudo;

    @Test
    @Order(1)
    void getSchools(){
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/schools")
                .then()
                    .statusCode(200)
                    .body("schools.size()", Matchers.greaterThanOrEqualTo(0));
    }

    @Test
    @Order(2)
    void saveSchool(){
        var schoolDto = Generator.school();
        SchoolRequest request = new SchoolRequest();
        request.setSchool(schoolDto);
        request.setAccount(Generator.account());
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/api/v1/schools")
                    .then()
                        .statusCode(204);
    }

    @Test
    @Order(3)
    void checkSchool(){
        pseudo = given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/schools")
                .then()
                    .statusCode(200)
                    .body("schools.size()", Matchers.greaterThanOrEqualTo(1))
                    .extract()
                        .body()
                        .jsonPath()
                        .get("schools[0].pseudo");
    }

    @Test
    @Order(4)
    void get(){
        var schoolDto = Generator.school();
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(schoolDto)
                .post("/api/v1/schools/{id}", pseudo)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(4)
    void update(){
        var schoolDto = Generator.school();
        schoolDto.setName("testupdate");
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(schoolDto)
                .post("/api/v1/schools/{id}", pseudo)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(4)
    void checkAdmin(){
        given()
                .accept(ContentType.JSON)
                .when()
                .header("x-app-tenant", pseudo)
                .get("/api/v1/accounts")
                .then()
                    .statusCode(200)
                    .body("accounts.size()", Matchers.is(1))
                    .body("accounts[0].password", Matchers.containsStringIgnoringCase("XXX"))
                    .body("accounts[0].email", Matchers.is("test@test.com"));
    }

    @Test
    @Order(4)
    void checkAccount(){
        given()
                .contentType(ContentType.JSON)
                .body(Generator.account())
                .when()
                .header("x-app-tenant", pseudo)
                .post("/api/v1/accounts/check")
                .then()
                .statusCode(200)
                .body("message", Matchers.not(Matchers.emptyOrNullString()));
    }

    @Test
    @Order(4)
    void checkInvalidEmailAccount(){
        var accountDto = Generator.account();
        accountDto.setEmail("error@error.com");
        given()
                .contentType(ContentType.JSON)
                .body(accountDto)
                .when()
                .header("x-app-tenant", pseudo)
                .post("/api/v1/accounts/check")
                .then()
                .statusCode(400)
                .body("message", Matchers.not(Matchers.emptyOrNullString()));
    }

    @Test
    @Order(4)
    void checkInvalidPwdAccount(){
        var accountDto = Generator.account();
        accountDto.setPassword("error");
        given()
                .contentType(ContentType.JSON)
                .body(accountDto)
                .when()
                .header("x-app-tenant", pseudo)
                .post("/api/v1/accounts/check")
                .then()
                .statusCode(400)
                .body("message", Matchers.not(Matchers.emptyOrNullString()));
    }
}
