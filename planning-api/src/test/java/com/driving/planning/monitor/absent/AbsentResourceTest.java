package com.driving.planning.monitor.absent;

import com.driving.planning.Generator;
import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.monitor.MonitorService;
import com.driving.planning.monitor.dto.MonitorDto;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.*;

@QuarkusTest
class AbsentResourceTest {

    @InjectMock
    MonitorService monitorService;

    @InjectMock
    AbsentService absentService;

    @Test
    void add(){
        when(monitorService.get("id")).thenReturn(Optional.of(new MonitorDto()));
        var absent = new AbsentRequest();
        absent.setStart(LocalDate.now());
        absent.setEnd(LocalDate.now().plusDays(2));
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .body(absent)
                .when()
                .post("/api/v1/monitors/{id}/absents", "id")
                .then()
                .statusCode(204);
        verify(absentService, times(1)).addAbsent(any(), any());
    }

    @Test
    void addNotFound(){
        LocalDate now = LocalDate.now();
        var absent = new AbsentRequest(now, now.plusDays(5));
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
        var monitor = Generator.monitor();
        when(monitorService.get("id")).thenReturn(Optional.of(monitor));

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .when()
                .delete("/api/v1/monitors/{id}/absents/{ref}", "id", monitor.getAbsents().get(0).getReference())
                .then()
                .statusCode(204);

        verify(absentService, times(1)).removeAbsent(any(), eq("ref"));

    }

    @Test
    void wrongParams(){
        when(monitorService.get("id")).thenReturn(Optional.empty());
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .when()
                .delete("/api/v1/monitors/{id}/absents/{ref}", "id", "ref")
                .then()
                .statusCode(404);
    }

}
