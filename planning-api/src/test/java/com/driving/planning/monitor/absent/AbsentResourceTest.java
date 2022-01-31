package com.driving.planning.monitor.absent;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.monitor.MonitorService;
import com.driving.planning.monitor.dto.MonitorDto;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
class AbsentResourceTest {

    @InjectMock
    MonitorService monitorService;

    @Test
    void add(){
        var now = LocalDateTime.now();
        var absent = new Absent(now, now.plusDays(5));
        when(monitorService.get("id")).thenReturn(Optional.of(new MonitorDto()));
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(absent)
                .when()
                .post("/api/v1/monitors/{id}/absents", "id")
                .then()
                .statusCode(204);

        ArgumentCaptor<MonitorDto> dtoCaptor = ArgumentCaptor.forClass(MonitorDto.class);
        verify(monitorService, atMostOnce()).update(dtoCaptor.capture());
        assertThat(dtoCaptor.getValue())
                .isNotNull();
        assertThat(dtoCaptor.getValue().getAbsents())
                .contains(absent);
    }

    @Test
    void addNotFound(){
        var now = LocalDateTime.now();
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
        var now = LocalDateTime.now();
        var absent = new Absent(now, now.plusDays(5));
        var monitor = new MonitorDto();
        monitor.getAbsents().add(absent);
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
        assertThat(dtoCaptor.getValue().getAbsents())
                .doesNotContain(absent);
    }

    @Test
    void removeNotFound(){
        var now = LocalDateTime.now();
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
