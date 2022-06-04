package com.driving.planning.student.reservation;

import com.driving.planning.Generator;
import com.driving.planning.student.StudentService;
import com.driving.planning.student.dto.StudentDto;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
class ReservationResourceTest {

    @InjectMock
    StudentService studentService;

    @InjectMock
    ReservationService reservationService;

    @Test
    void add(){
        var id = "id";
        var student = Generator.student();
        when(studentService.get(id)).thenReturn(Optional.of(student));
        when(reservationService.addReservation(any(), any())).thenReturn("ref");
        var reservation = new ReservationRequest();
        reservation.setDate(LocalDate.now());
        reservation.setBegin(LocalTime.of(8,0));
        reservation.setEnd(LocalTime.of(9,0));

        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(reservation)
                .when()
                .post("/api/v1/students/{id}/reservations", id)
                .then()
                .statusCode(200)
                .body("id", Matchers.is("ref"))
                .log();

        ArgumentCaptor<ReservationRequest> reservationCaptor = ArgumentCaptor.forClass(ReservationRequest.class);
        verify(reservationService, times(1)).addReservation(any(), reservationCaptor.capture());
        assertThat(reservationCaptor.getValue())
                .isNotNull()
                .extracting(ReservationRequest::getDate, ReservationRequest::getBegin, ReservationRequest::getEnd)
                .containsExactly(reservation.getDate(), reservation.getBegin(), reservation.getEnd());
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

        verify(reservationService, never()).addReservation(any(), any());
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
                .delete("/api/v1/students/{id}/reservations/{ref}", id, "ref")
                .then()
                .statusCode(404);

        verify(reservationService, never()).addReservation(any(), any());
    }

    @Test
    void remove(){
        var id = "85858";
        var student = Generator.student();
        when(studentService.get(id)).thenReturn(Optional.of(student));
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .when()
                .delete("/api/v1/students/{id}/reservations/{ref}", id, "ref")
                .then()
                .statusCode(204);

        ArgumentCaptor<StudentDto> studentCaptor = ArgumentCaptor.forClass(StudentDto.class);
        verify(reservationService, times(1)).removeReservation(studentCaptor.capture(), eq("ref"));
        assertThat(studentCaptor.getValue())
                .isNotNull();
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
