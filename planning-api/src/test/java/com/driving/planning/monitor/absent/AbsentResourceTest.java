package com.driving.planning.monitor.absent;

import com.driving.planning.Generator;
import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.common.hourly.Day;
import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.event.EventService;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.dto.EventDto;
import com.driving.planning.monitor.MonitorService;
import com.driving.planning.monitor.dto.MonitorDto;
import com.driving.planning.school.SchoolService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
class AbsentResourceTest {

    @InjectMock
    MonitorService monitorService;

    @InjectMock
    SchoolService schoolService;

    @InjectMock
    EventService eventService;

    @Test
    void add(){
        @javax.validation.constraints.NotNull LocalDate now = LocalDate.now();
        var absent = new Absent(now, now.plusDays(1));
        when(monitorService.get("id")).thenReturn(Optional.of(new MonitorDto()));
        var h1 = new Hourly();
        h1.setDay(Day.fromDayOfWeek(absent.getStart().getDayOfWeek()));
        h1.setBegin(LocalTime.of(8, 0));
        h1.setEnd(LocalTime.of(18, 0));
        var h2 = new Hourly();
        h2.setDay(Day.fromDayOfWeek(absent.getEnd().getDayOfWeek()));
        h2.setBegin(LocalTime.of(8, 0));
        h2.setEnd(LocalTime.of(18, 0));
        var school = Generator.school();
        school.getWorkDays().add(h1);
        school.getWorkDays().add(h2);
        when(schoolService.get("tenant")).thenReturn(Optional.of(school));
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(absent)
                .when()
                .post("/api/v1/monitors/{id}/absents", "id")
                .then()
                .statusCode(204);

        ArgumentCaptor<EventDto> dtoCaptor = ArgumentCaptor.forClass(EventDto.class);
        verify(eventService, times(2)).add(dtoCaptor.capture());
        assertThat(dtoCaptor.getAllValues())
                .element(0)
                .extracting(EventDto::getEventDate, EventDto::getRelatedUserId, EventDto::getType)
                .contains(absent.getStart(), "id", EventType.MONITOR);
    }

    @Test
    void addNotFound(){
        @javax.validation.constraints.NotNull LocalDate now = LocalDate.now();
        var absent = new Absent(now, now.plusDays(5));
        when(monitorService.get("id")).thenThrow(new PlanningException(Response.Status.NOT_FOUND, "Not found"));
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(absent)
                .when()
                .post("/api/v1/monitors/{id}/absents", "id")
                .then()
                .statusCode(404);
    }

    @Test
    void delete(){
        @javax.validation.constraints.NotNull LocalDate now = LocalDate.now();
        var absent = new Absent(now, now.plusDays(5));
        var monitor = new MonitorDto();
        when(monitorService.get("id")).thenReturn(Optional.of(monitor));

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(absent)
                .when()
                .delete("/api/v1/monitors/{id}/absents", "id")
                .then()
                .statusCode(204);
        ArgumentCaptor<MonitorDto> dtoCaptor = ArgumentCaptor.forClass(MonitorDto.class);
        verify(monitorService, atMostOnce()).update(dtoCaptor.capture());
        assertThat(dtoCaptor.getValue())
                .isNotNull();
    }

    @Test
    void removeNotFound(){
        @javax.validation.constraints.NotNull LocalDate now = LocalDate.now();
        var absent = new Absent(now, now.plusDays(5));
        when(monitorService.get("id")).thenThrow(new PlanningException(Response.Status.NOT_FOUND, "Not found"));
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(absent)
                .when()
                .delete("/api/v1/monitors/{id}/absents", "id")
                .then()
                .statusCode(404);
    }

    @Test
    void wrongParams(){
        var absent = new Absent();
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(absent)
                .when()
                .delete("/api/v1/monitors/{id}/absents", "id")
                .then()
                .statusCode(400);
    }

}
