package com.driving.planning.monitor.absence;

import com.driving.planning.Generator;
import com.driving.planning.school.dto.SchoolRequest;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AbsenceResourceIT {

    private static String tenant;

    private static String id;

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
    @Order(1)
    void add(){
        @javax.validation.constraints.NotNull LocalDate now = LocalDate.now();
        var absent = new AbsenceRequest(now, now.plusDays(5));
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("x-app-tenant", tenant)
                .body(absent)
                .when()
                .post("/api/v1/monitors/{id}/absents", "60f6ab7f443a1d3e27b6cbaf")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(2)
    void delete(){
        @javax.validation.constraints.NotNull LocalDate now = LocalDate.now();
        var absent = new AbsenceRequest(now, now.plusDays(5));
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("x-app-tenant", tenant)
                .body(absent)
                .when()
                .delete("/api/v1/monitors/{id}/absents/{ref}", "60f6ab7f443a1d3e27b6cbaf", "ref")
                .then()
                .statusCode(404);
    }

}
