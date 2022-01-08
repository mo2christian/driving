package com.driving.planning.monitor;

import com.driving.planning.Generator;
import com.driving.planning.common.DatePattern;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.*;

@QuarkusTest
class MonitorResourceTest {

    @InjectMock
    MonitorService service;

    @Test
    void list(){
        var monitorDto = Generator.monitor();
        var absent = monitorDto.getAbsents().stream()
                .findFirst()
                .orElseThrow();
        var hourly = getFirst(monitorDto.getWorkDays());
        monitorDto.setId("id");
        var timeFormatter = DateTimeFormatter.ofPattern(DatePattern.TIME);
        var dayFormatter = DateTimeFormatter.ofPattern(DatePattern.DATE_TIME);
        when(service.list()).thenReturn(Collections.singletonList(monitorDto));
        given()
                .accept(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .when()
                .get("/api/v1/monitors")
                .then()
                .statusCode(200)
                .body("monitors.size()", Matchers.is(1))
                .body("monitors[0].firstName", Matchers.is(monitorDto.getFirstName()))
                .body("monitors[0].lastName", Matchers.is(monitorDto.getLastName()))
                .body("monitors[0].phoneNumber", Matchers.is(monitorDto.getPhoneNumber()))
                .body("monitors[0].id", Matchers.is(monitorDto.getId()))
                .body("monitors[0].workDays.size()", Matchers.is(1))
                .body("monitors[0].workDays[0].day", Matchers.is(hourly.getDay().getValue()))
                .body("monitors[0].workDays[0].begin", Matchers.is(timeFormatter.format(hourly.getBegin())))
                .body("monitors[0].workDays[0].end", Matchers.is(timeFormatter.format(hourly.getEnd())))
                .body("monitors[0].absents.size()", Matchers.is(1))
                .body("monitors[0].absents[0].start", Matchers.is(dayFormatter.format(absent.getStart())))
                .body("monitors[0].absents[0].end", Matchers.is(dayFormatter.format(absent.getEnd())))
                .body("monitors[0].absents[0].motif", Matchers.is(absent.getMotif()));
    }

    @Test
    void add(){
        var dto = Generator.monitor();
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(dto)
                .when()
                .post("/api/v1/monitors")
                .then()
                .statusCode(204);

        ArgumentCaptor<MonitorDto> captor = ArgumentCaptor.forClass(MonitorDto.class);
        verify(service, atMostOnce()).add(captor.capture());
        Assertions.assertThat(captor.getValue())
                .extracting(MonitorDto::getFirstName, MonitorDto::getLastName, MonitorDto::getPhoneNumber, MonitorDto::getWorkDays)
                .containsExactly(dto.getFirstName(), dto.getLastName(), dto.getPhoneNumber(), dto.getWorkDays());
    }

    @ParameterizedTest
    @MethodSource("params")
    void addWithInvalidParams(String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        var monitor = Generator.monitor();
        setField(monitor, field, value);
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(monitor)
                .when()
                .post("/api/v1/monitors")
                .then()
                .statusCode(400)
                .body("details[0]", Matchers.containsStringIgnoringCase("add.monitorDto." + field));
    }

    @Test
    void update(){
        var id = "test";
        var dto = Generator.monitor();
        dto.setId(id);
        when(service.get(id)).thenReturn(Optional.of(dto));
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(dto)
                .when()
                .post("/api/v1/monitors/{id}", id)
                .then()
                .statusCode(204);

        ArgumentCaptor<MonitorDto> captor = ArgumentCaptor.forClass(MonitorDto.class);
        verify(service, atMostOnce()).update(captor.capture());
        Assertions.assertThat(captor.getValue())
                .extracting(MonitorDto::getId, MonitorDto::getFirstName, MonitorDto::getLastName, MonitorDto::getPhoneNumber, MonitorDto::getWorkDays)
                .containsExactly(id, dto.getFirstName(), dto.getLastName(), dto.getPhoneNumber(), dto.getWorkDays());

        when(service.get(id)).thenReturn(Optional.empty());
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(dto)
                .when()
                .post("/api/v1/monitors/{id}", id)
                .then()
                .statusCode(404);
    }

    @Test
    void delete(){
        var id = "id";
        var dto = new MonitorDto();
        dto.setId(id);
        when(service.get(id)).thenReturn(Optional.of(dto));
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .when()
                .delete("/api/v1/monitors/{id}", id)
                .then()
                .statusCode(204);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(service, atMostOnce()).delete(captor.capture());
        Assertions.assertThat(captor.getValue()).isEqualTo(id);

        when(service.get(id)).thenReturn(Optional.empty());
        given()
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .when()
                .delete("/api/v1/monitors/{id}", id)
                .then()
                .statusCode(404);
    }

    private void setField(Object target, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }

    private <T>T getFirst(Set<T> set){
        return set.stream().findFirst().orElse(null);
    }

    private static Stream<Arguments> params(){
        return Stream.of(
                Arguments.of("phoneNumber", null),
                Arguments.of("phoneNumber", ""),
                Arguments.of("firstName", ""),
                Arguments.of("firstName", null),
                Arguments.of("lastName", ""),
                Arguments.of("lastName", null)
        );
    }
}