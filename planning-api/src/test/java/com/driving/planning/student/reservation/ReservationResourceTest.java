package com.driving.planning.student.reservation;

import com.driving.planning.Generator;
import com.driving.planning.student.StudentDto;
import com.driving.planning.student.StudentService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import static org.assertj.core.api.Assertions.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

@QuarkusTest
class ReservationResourceTest {

    @InjectMock
    StudentService studentService;

    @Test
    void add(){
        var id = "id";
        var student = Generator.student();
        when(studentService.get(id)).thenReturn(Optional.of(student));
        var reservation = student.getReservations().stream().findFirst().orElseThrow();

        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(reservation)
                .when()
                .post("/api/v1/students/{id}/reservations", id)
                .then()
                .statusCode(204);

        ArgumentCaptor<StudentDto> studentCaptor = ArgumentCaptor.forClass(StudentDto.class);
        verify(studentService, atMostOnce()).update(studentCaptor.capture());
        assertThat(studentCaptor.getValue())
                .isNotNull();
        assertThat(studentCaptor.getValue().getReservations())
                .contains(reservation);
    }

    @Test
    void addNotFound(){
        var id = "id";
        when(studentService.get(id)).thenReturn(Optional.empty());
        var reservation = new Reservation();
        reservation.setDate(LocalDate.now().plusDays(3));
        reservation.setBegin(LocalTime.now());
        reservation.setEnd(LocalTime.now().plusHours(1));

        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(reservation)
                .when()
                .post("/api/v1/students/{id}/reservations", id)
                .then()
                .statusCode(404);

        verify(studentService, never()).update(any());
    }

    @Test
    void deleteNotFound(){
        var id = "id";
        when(studentService.get(id)).thenReturn(Optional.empty());
        var reservation = new Reservation();
        reservation.setDate(LocalDate.now().plusDays(3));
        reservation.setBegin(LocalTime.now());
        reservation.setEnd(LocalTime.now().plusHours(1));

        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(reservation)
                .when()
                .delete("/api/v1/students/{id}/reservations", id)
                .then()
                .statusCode(404);

        verify(studentService, never()).update(any());
    }

    @Test
    void remove(){
        var id = "85858";
        var student = Generator.student();
        when(studentService.get(id)).thenReturn(Optional.of(student));
        var reservation = student.getReservations().stream().findFirst().orElseThrow();
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(reservation)
                .when()
                .delete("/api/v1/students/{id}/reservations", id)
                .then()
                .statusCode(204);

        ArgumentCaptor<StudentDto> studentCaptor = ArgumentCaptor.forClass(StudentDto.class);
        verify(studentService, atMostOnce()).update(studentCaptor.capture());
        assertThat(studentCaptor.getValue())
                .isNotNull();
        assertThat(studentCaptor.getValue().getReservations())
                .doesNotContain(reservation);
    }

    @ParameterizedTest
    @MethodSource("params")
    void addWithBadParams(String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        var reservation = new Reservation();
        reservation.setDate(LocalDate.now().plusDays(3));
        reservation.setBegin(LocalTime.now());
        reservation.setEnd(LocalTime.now().plusHours(1));
        setField(reservation, field, value);

        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(reservation)
                .when()
                .post("/api/v1/students/{id}/reservations", "874")
                .then()
                .statusCode(400)
                .body("details[0]", Matchers.containsStringIgnoringCase("add.reservation." + field));
    }

    @ParameterizedTest
    @MethodSource("params")
    void deleteWithBadParams(String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        var reservation = new Reservation();
        reservation.setDate(LocalDate.now().plusDays(3));
        reservation.setBegin(LocalTime.now());
        reservation.setEnd(LocalTime.now().plusHours(1));
        setField(reservation, field, value);

        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(reservation)
                .when()
                .delete("/api/v1/students/{id}/reservations", "874")
                .then()
                .statusCode(400)
                .body("details[0]", Matchers.containsStringIgnoringCase("delete.reservation." + field));
    }

    private void setField(Object target, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }

    private static Stream<Arguments> params(){
        return Stream.of(
                Arguments.of("date", null),
                Arguments.of("begin", null),
                Arguments.of("end", null)
        );
    }

}
