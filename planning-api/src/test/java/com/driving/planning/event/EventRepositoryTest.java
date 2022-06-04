package com.driving.planning.event;

import com.driving.planning.MongodbTestResource;
import com.driving.planning.config.database.DatabaseResolverInitializer;
import com.driving.planning.config.database.Tenant;
import com.driving.planning.event.domain.Event;
import com.driving.planning.event.domain.EventType;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;

@QuarkusTestResource(MongodbTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
class EventRepositoryTest {

    @Inject
    EventRepository eventRepository;

    final String REF = "ref";

    @BeforeAll
    static void before(){
        var tenant = new Tenant("base");
        DatabaseResolverInitializer.alterAnnotation(Event.class, tenant);
    }

    @Test
    @Order(1)
    void insert(){
        var event = new Event();
        event.setEventDate(LocalDate.now());
        event.setBegin(LocalTime.now());
        event.setEnd(LocalTime.now().plusHours(1));
        event.setType(EventType.MONITOR);
        event.setReference(REF);

        eventRepository.persist(event);

        Assertions.assertThat(eventRepository.listAll())
                .hasSize(1);

        Assertions.assertThat(eventRepository.listByDate(event.getEventDate()))
                .hasSize(1);

        Assertions.assertThat(eventRepository.listByDate(event.getEventDate().plusDays(1)))
                .isEmpty();
    }

    @Test
    @Order(2)
    void delete(){
        eventRepository.deleteByRef(REF);
        Assertions.assertThat(eventRepository.listAll())
                .isEmpty();
    }

}
