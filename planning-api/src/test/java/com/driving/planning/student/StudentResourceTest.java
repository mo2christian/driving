package com.driving.planning.student;

import com.driving.planning.Generator;
import com.driving.planning.common.DatePattern;
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
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
class StudentResourceTest {

    @InjectMock
    StudentService studentService;

    @Test
    void list(){
        var student = Generator.student();
        var dateFormatter = DateTimeFormatter.ofPattern(DatePattern.DATE);
        var timeFormatter = DateTimeFormatter.ofPattern(DatePattern.TIME);
        var reservation = student.getReservations().stream().findFirst().orElseThrow();
        when(studentService.list()).thenReturn(Collections.singletonList(student));
        given()
                .accept(ContentType.JSON)
                .header("x-app-tenant", "tenant")
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
                .body("students[0].reservations[0].end", Matchers.is(timeFormatter.format(reservation.getEnd())));
    }

    @Test
    void add(){
        var dto = Generator.student();
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(dto)
                .when()
                .post("/api/v1/students")
                .then()
                .statusCode(204);
        ArgumentCaptor<StudentDto> studentCaptor = ArgumentCaptor.forClass(StudentDto.class);
        verify(studentService, times(1)).add(studentCaptor.capture());
        assertThat(studentCaptor.getValue()).isNotNull()
                .extracting(StudentDto::getEmail, StudentDto::getFirstName, StudentDto::getLastName, StudentDto::getPhoneNumber, StudentDto::getReservations)
                .containsExactly(dto.getEmail(), dto.getFirstName(), dto.getLastName(), dto.getPhoneNumber(), dto.getReservations());
        assertThat(studentCaptor.getValue().getId()).isNull();
    }

    @ParameterizedTest
    @MethodSource("addParams")
    void addWithInvalidParams(String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        var student = Generator.student();
        setField(student, field, value);
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(student)
                .when()
                .post("/api/v1/students")
                .then()
                .statusCode(400)
                .body("details[0]", Matchers.containsStringIgnoringCase("addStudent.studentDto." + field));
    }

    @ParameterizedTest
    @MethodSource("addParams")
    void updateWithInvalidParams(String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        var student = Generator.student();
        setField(student, field, value);
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(student)
                .when()
                .post("/api/v1/students/{id}", "id")
                .then()
                .statusCode(400)
                .body("details[0]", Matchers.containsStringIgnoringCase("updateStudent.studentDto." + field));
    }

    @Test
    void update(){
        var dto = Generator.student();
        final String id = "id";
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(dto)
                .when()
                .post("/api/v1/students/{id}", id)
                .then()
                .statusCode(204);
        ArgumentCaptor<StudentDto> studentCaptor = ArgumentCaptor.forClass(StudentDto.class);
        verify(studentService, times(1)).update(studentCaptor.capture());
        assertThat(studentCaptor.getValue()).isNotNull()
                .extracting(StudentDto::getEmail, StudentDto::getFirstName, StudentDto::getLastName, StudentDto::getPhoneNumber, StudentDto::getReservations)
                .containsExactly(dto.getEmail(), dto.getFirstName(), dto.getLastName(), dto.getPhoneNumber(), dto.getReservations());
        assertThat(studentCaptor.getValue().getId())
                .isNotNull()
                .isEqualTo(id);
    }

    @Test
    void delete(){
        final String id = "id";
        given()
                .contentType(ContentType.JSON)
                .delete("/api/v1/students/{id}", id)
                .then()
                .statusCode(204);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(studentService, times(1)).delete(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isNotNull().isEqualTo(id);
    }

    private void setField(Object target, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }

    private static Stream<Arguments> addParams(){
        return Stream.of(
                Arguments.of("email", null),
                Arguments.of("email", ""),
                Arguments.of("phoneNumber", null),
                Arguments.of("phoneNumber", ""),
                Arguments.of("email", "toto@"),
                Arguments.of("email", "toto"),
                Arguments.of("firstName", ""),
                Arguments.of("firstName", null),
                Arguments.of("lastName", ""),
                Arguments.of("lastName", null)
        );
    }

}
