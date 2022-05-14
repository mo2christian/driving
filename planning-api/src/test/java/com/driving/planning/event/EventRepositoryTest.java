package com.driving.planning.event;

import com.driving.planning.MongodbTestResource;
import com.driving.planning.event.domain.Event;
import com.driving.planning.event.domain.EventType;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;

@QuarkusTestResource(MongodbTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
class EventRepositoryTest {

    @Inject
    EventRepository eventRepository;

    @Test
    void insert(){
        var event = new Event();
        event.setEventDate(LocalDate.now());
        event.setBegin(LocalTime.now());
        event.setEnd(LocalTime.now().plusHours(1));
        event.setType(EventType.MONITOR);
        eventRepository.insert(event);

        Assertions.assertThat(eventRepository.list())
                .hasSize(1);

        Assertions.assertThat(eventRepository.listByDate(event.getEventDate()))
                .hasSize(1);

        Assertions.assertThat(eventRepository.listByDate(event.getEventDate().plusDays(1)))
                .isEmpty();
    }

}
