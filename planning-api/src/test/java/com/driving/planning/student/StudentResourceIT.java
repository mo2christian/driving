package com.driving.planning.student;

import com.driving.planning.Generator;
import com.driving.planning.common.DatePattern;
import com.driving.planning.school.dto.SchoolRequest;
import com.driving.planning.student.reservation.ReservationRequest;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentResourceIT {

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
    void create(){
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", tenant)
                .body(Generator.student())
                .when()
                .post("/api/v1/students")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(2)
    void list(){
        var student = Generator.student();
        var dateFormatter = DateTimeFormatter.ofPattern(DatePattern.DATE);
        var timeFormatter = DateTimeFormatter.ofPattern(DatePattern.TIME);
        var reservation = student.getReservations().stream().findFirst().orElseThrow();
        id = given()
                .accept(ContentType.JSON)
                .header("x-app-tenant", tenant)
                .when()
                .get("/api/v1/students")
                .then()
                .statusCode(200)
                .body("students.size()", Matchers.is(1))
                .body("students[0].email", Matchers.is(student.getEmail()))
                .body("students[0].phoneNumber", Matchers.is(student.getPhoneNumber()))
                .body("students[0].firstName", Matchers.is(student.getFirstName()))
                .body("students[0].lastName", Matchers.is(student.getLastName()))
                .body("students[0].reservations.size()", Matchers.is(1))
                .body("students[0].reservations[0].date", Matchers.is(dateFormatter.format(reservation.getDate())))
                .body("students[0].reservations[0].begin", Matchers.is(timeFormatter.format(reservation.getBegin())))
                .body("students[0].reservations[0].end", Matchers.is(timeFormatter.format(reservation.getEnd())))
                .extract()
                .body()
                .jsonPath()
                .get("students[0].id");
    }

    @Test
    @Order(3)
    void get(){
        var student = Generator.student();
        given()
                .accept(ContentType.JSON)
                .header("x-app-tenant", tenant)
                .when()
                .get("/api/v1/students/{id}", id)
                .then()
                .statusCode(200)
                .body("email", Matchers.is(student.getEmail()))
                .body("phoneNumber", Matchers.is(student.getPhoneNumber()))
                .body("firstName", Matchers.is(student.getFirstName()))
                .body("lastName", Matchers.is(student.getLastName()));
    }

    @Test
    @Order(3)
    void addReservation(){
        var request = new ReservationRequest();
        request.setDate(LocalDate.now());
        request.setBegin(LocalTime.of(11, 0));
        request.setEnd(LocalTime.of(12, 0));
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", tenant)
                .body(request)
                .when()
                .post("/api/v1/students/{id}/reservations", id)
                .then()
                .statusCode(Matchers.anyOf(Matchers.is(200), Matchers.is(400)))
                .log();
    }

    @Test
    @Order(3)
    void listWithInvalidTenant(){
        given()
                .accept(ContentType.JSON)
                .header("x-app-tenant", "notfound")
                .when()
                .get("/api/v1/students")
                .then()
                .statusCode(200)
                .body("students.size()", Matchers.is(0));
    }

    @Test
    @Order(4)
    void delete(){
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", tenant)
                .delete("/api/v1/students/{id}", id)
                .then()
                .statusCode(204);
    }
}
