package com.driving.planning.calendar;

import com.driving.planning.Generator;
import com.driving.planning.common.DatePattern;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class CalendarResourceTest {

    @InjectMock
    CalendarService calendarService;

    @Test
    void generateCalendar(){
        var start = LocalDate.of(2021, 10, 1);
        var end = LocalDate.of(2021, 10, 3);
        var dayFormatter = DateTimeFormatter.ofPattern(DatePattern.DATE);
        final var monitor = Generator.monitor();
        when(calendarService.get(any())).then(ans -> {
            var dayCalendar = new DayCalendar();
            dayCalendar.setDay(ans.getArgument(0));
            var period = new Period();
            period.setAvailablePlaces(1);
            period.setBegin(LocalTime.of(10, 0));
            period.setEnd(LocalTime.of(11, 0));
            period.setMonitors(Collections.singletonList(monitor));
            dayCalendar.setPeriods(Collections.singletonList(period));
            return dayCalendar;
        });
        given()
                .accept(ContentType.JSON)
                .header("x-app-tenant", "tenant")
                .when()
                .queryParam("start", start.format(dayFormatter))
                .queryParam("end", end.format(dayFormatter))
                .get("/api/v1/calendar")
                .then()
                .statusCode(200)
                .body("start", Matchers.is(dayFormatter.format(start)))
                .body("end", Matchers.is(dayFormatter.format(end)))
                .body("dayCalendars.size()", Matchers.is(3))
                .body("dayCalendars[0].day", Matchers.is(dayFormatter.format(start)))
                .body("dayCalendars[0].periods[0].begin", Matchers.is("10:00"))
                .body("dayCalendars[0].periods[0].end", Matchers.is("11:00"))
                .body("dayCalendars[0].periods[0].availablePlaces", Matchers.is(1))
                .body("dayCalendars[0].periods[0].monitors.size()", Matchers.is(1))
                .body("dayCalendars[0].periods[0].monitors[0].firstName", Matchers.is(monitor.getFirstName()))
                .body("dayCalendars[0].periods[0].monitors[0].lastName", Matchers.is(monitor.getLastName()));
    }

}
