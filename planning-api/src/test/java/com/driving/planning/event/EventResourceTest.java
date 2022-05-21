package com.driving.planning.event;

import com.driving.planning.Generator;
import com.driving.planning.common.DatePattern;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.dto.EventDto;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.*;

@QuarkusTest
class EventResourceTest {

    @InjectMock
    EventService eventService;

    @Inject
    EventEndpoint eventEndpoint;

    @Test
    void add(){
        var event = Generator.event();
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(event)
                .when()
                .post("/api/v1/events")
                .then()
                .statusCode(201);
        verify(eventService, times(1)).add(any(EventDto.class));
    }

    @Test
    void addBadParams(){
        var event = Generator.event();
        event.setEnd(event.getBegin().minusMinutes(30));
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(event)
                .when()
                .post("/api/v1/events")
                .then()
                .statusCode(400);

        event.setEnd(event.getBegin());
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(event)
                .when()
                .post("/api/v1/events")
                .then()
                .statusCode(400);
    }

    @Test
    void addNullValues(){
        final var event = Generator.event();
        event.setType(null);
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(event)
                .when()
                .post("/api/v1/events")
                .then()
                .statusCode(400);

        event.setType(EventType.STUDENT);
        event.setEventDate(null);
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(event)
                .when()
                .post("/api/v1/events")
                .then()
                .statusCode(400);

        event.setEventDate(LocalDate.now());
        event.setBegin(null);
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(event)
                .when()
                .post("/api/v1/events")
                .then()
                .statusCode(400);

        event.setBegin(LocalTime.now());
        event.setEnd(null);
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(event)
                .when()
                .post("/api/v1/events")
                .then()
                .statusCode(400);
    }

    @Test
    void list(){
        var event = Generator.event();
        var dateFormatter = DateTimeFormatter.ofPattern(DatePattern.DATE);
        var timeFormatter = DateTimeFormatter.ofPattern(DatePattern.TIME);
        when(eventService.list()).thenReturn(Collections.singletonList(event));
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/events")
                .then()
                .statusCode(200)
                .body("events[0].eventDate", Matchers.is(dateFormatter.format(event.getEventDate())))
                .body("events[0].begin", Matchers.is(timeFormatter.format(event.getBegin())))
                .body("events[0].end", Matchers.is(timeFormatter.format(event.getEnd())))
                .body("events[0].type", Matchers.is(event.getType().name()))
                .body("events[0].reference", Matchers.is(event.getReference()))
                .body("events.size()", Matchers.is(1));
    }

}
