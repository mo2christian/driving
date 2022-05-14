package com.driving.planning.event;

import com.driving.planning.Generator;
import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.dto.EventDto;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

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
        eventEndpoint.add(event);
        verify(eventService, times(1)).add(any(EventDto.class));
    }

    @Test
    void addBadParams(){
        var event = Generator.event();
        event.setEnd(event.getBegin().minusMinutes(30));
        Assertions.assertThatThrownBy(() -> eventEndpoint.add(event))
                .isInstanceOf(PlanningException.class)
                .extracting("status")
                .isEqualTo(Response.Status.BAD_REQUEST);

        event.setEnd(event.getBegin());
        Assertions.assertThatThrownBy(() -> eventEndpoint.add(event))
                .isInstanceOf(PlanningException.class)
                .extracting("status")
                .isEqualTo(Response.Status.BAD_REQUEST);
    }

    @Test
    void addNullValues(){
        final var event = Generator.event();
        event.setType(null);
        Assertions.assertThatThrownBy(() -> eventEndpoint.add(event))
                .isInstanceOf(Exception.class);

        event.setType(EventType.STUDENT);
        event.setEventDate(null);
        Assertions.assertThatThrownBy(() -> eventEndpoint.add(event))
                .isInstanceOf(Exception.class);

        event.setEventDate(LocalDate.now());
        event.setBegin(null);
        Assertions.assertThatThrownBy(() -> eventEndpoint.add(event))
                .isInstanceOf(Exception.class);

        event.setBegin(LocalTime.now());
        event.setEnd(null);
        Assertions.assertThatThrownBy(() -> eventEndpoint.add(event))
                .isInstanceOf(Exception.class);
    }

    @Test
    void list(){
        when(eventService.list()).thenReturn(Collections.singletonList(Generator.event()));
        Assertions.assertThat(eventEndpoint.list())
                .matches(resp -> resp.getEvents().size() == 1);
    }

}
