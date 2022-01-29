package com.driving.planning.monitor;

import com.driving.planning.Generator;
import com.driving.planning.common.DatePattern;
import com.driving.planning.school.dto.SchoolRequest;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MonitorResourceIT {

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
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", tenant)
                .body(Generator.monitor())
                .when()
                .post("/api/v1/monitors")
                .then()
                .statusCode(204);
    }

    @Order(2)
    @Test
    void list(){
        var monitor = Generator.monitor();
        var hourly = monitor.getWorkDays()
                .stream()
                .findFirst()
                .orElseThrow();
        var formatter = DateTimeFormatter.ofPattern(DatePattern.TIME);
        id = given()
                .accept(ContentType.JSON)
                .header("x-app-tenant", tenant)
                .when()
                .get("/api/v1/monitors")
                .then()
                .statusCode(200)
                .body("monitors.size()", Matchers.is(1))
                .body("monitors[0].phoneNumber", Matchers.is(monitor.getPhoneNumber()))
                .body("monitors[0].firstName", Matchers.is(monitor.getFirstName()))
                .body("monitors[0].lastName", Matchers.is(monitor.getLastName()))
                .body("monitors[0].workDays.size()", Matchers.is(1))
                .body("monitors[0].workDays[0].day", Matchers.is(hourly.getDay().getValue()))
                .body("monitors[0].workDays[0].begin", Matchers.is(formatter.format(hourly.getBegin())))
                .body("monitors[0].workDays[0].end", Matchers.is(formatter.format(hourly.getEnd())))
                .extract()
                .body()
                .jsonPath()
                .get("monitors[0].id");
    }

    @Order(3)
    @Test
    void get(){
        var monitor = Generator.monitor();
        var hourly = monitor.getWorkDays()
                .stream()
                .findFirst()
                .orElseThrow();
        var formatter = DateTimeFormatter.ofPattern(DatePattern.TIME);
        given()
                .accept(ContentType.JSON)
                .header("x-app-tenant", tenant)
                .when()
                .get("/api/v1/monitors/{id}", monitor.getId())
                .then()
                .statusCode(200)
                .body("phoneNumber", Matchers.is(monitor.getPhoneNumber()))
                .body("firstName", Matchers.is(monitor.getFirstName()))
                .body("lastName", Matchers.is(monitor.getLastName()))
                .body("workDays.size()", Matchers.is(1))
                .body("workDays[0].day", Matchers.is(hourly.getDay().getValue()))
                .body("workDays[0].begin", Matchers.is(formatter.format(hourly.getBegin())))
                .body("workDays[0].end", Matchers.is(formatter.format(hourly.getEnd())));
    }

    @Order(3)
    @Test
    void delete(){
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", tenant)
                .delete("/api/v1/monitors/{id}", id)
                .then()
                .statusCode(204);
    }

}
