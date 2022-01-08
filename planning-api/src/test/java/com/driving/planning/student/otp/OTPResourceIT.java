package com.driving.planning.student.otp;

import com.driving.planning.Generator;
import com.driving.planning.school.dto.SchoolRequest;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
class OTPResourceIT {

    private static String tenant;

    @BeforeEach
    void init(){
        if (tenant != null){
            return;
        }

        int size = given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/schools")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .get("schools.size()");

        if (size == 0) {
            var school = Generator.school();
            school.setName(school.getName() + LocalDateTime.now().getNano());
            SchoolRequest request = new SchoolRequest();
            request.setSchool(school);
            request.setAccount(Generator.account());
            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .body(request)
                    .post("/api/v1/schools")
                    .then()
                    .statusCode(204);
        }
        tenant = given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/schools")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .get("schools[0].pseudo");
    }

    @Test
    void sendOTP(){
        given().log()
                .ifValidationFails()
                .header("x-app-tenant", tenant)
                .queryParam("canal", MethodType.EMAIL.getCanal())
                .when()
                .post("/api/v1/students/{phone}/otp/verify", "799874569")
                .then()
                .statusCode(404);
    }

    @Test
    void verifyOTP(){
        given().log()
                .ifValidationFails()
                .header("x-app-tenant", tenant)
                .queryParam("otp", "00")
                .when()
                .post("/api/v1/students/{phone}/otp/verify", "799874569")
                .then()
                .statusCode(404);
    }

}
