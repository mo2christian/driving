package com.driving.planning.monitor;

import com.driving.planning.MongodbTestResource;
import com.driving.planning.common.hourly.Day;
import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.config.database.DatabaseResolverInitializer;
import com.driving.planning.config.database.Tenant;
import com.driving.planning.monitor.absence.Absence;
import com.driving.planning.monitor.domain.Monitor;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

@QuarkusTestResource(MongodbTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
class MonitorRepositoryTest {

    static ObjectId id;

    @Inject
    MonitorRepository repository;

    Monitor monitor;

    @BeforeAll
    static void before(){
        var tenant = new Tenant("base");
        DatabaseResolverInitializer.alterAnnotation(Monitor.class, tenant);
    }

    @BeforeEach
    void init(){
        monitor = new Monitor();
        monitor.setFirstName("firstname");
        monitor.setLastName("lastname");
        monitor.setPhoneNumber("0101010101");
        var hourly = new Hourly();
        hourly.setDay(Day.FRIDAY);
        hourly.setBegin(LocalTime.now());
        hourly.setEnd(LocalTime.now().plusHours(5));
        monitor.setWorkDays(Collections.singleton(hourly));
        var absent = new Absence();
        absent.setStart(LocalDate.now());
        absent.setEnd(LocalDate.now().plusDays(3));
        absent.setReference("ref");
        monitor.getAbsences().add(absent);
    }

    @Order(1)
    @Test
    void insert(){
        repository.persist(monitor);

        Assertions.assertThat(repository.listAll())
                .hasSize(1)
                .element(0)
                .extracting(Monitor::getFirstName, Monitor::getLastName, Monitor::getPhoneNumber, Monitor::getWorkDays, Monitor::getAbsences)
                .containsExactly(monitor.getFirstName(), monitor.getLastName(), monitor.getPhoneNumber(), monitor.getWorkDays(), monitor.getAbsences());
        id = repository.listAll().get(0).getId();
    }

    @Order(2)
    @Test
    void update(){
        monitor.setFirstName("update");
        monitor.setLastName("update");
        monitor.setPhoneNumber("4242424242");
        monitor.setId(id);
        var hourly = new Hourly();
        hourly.setDay(Day.MONDAY);
        hourly.setBegin(LocalTime.now());
        hourly.setEnd(LocalTime.now().plusHours(5));
        monitor.setWorkDays(Collections.singleton(hourly));
        var absent = new Absence();
        absent.setStart(LocalDate.now());
        absent.setEnd(LocalDate.now().plusDays(5));
        absent.setReference("ref1");
        monitor.getAbsences().add(absent);
        repository.update(monitor);
        Assertions.assertThat(repository.listAll())
                .hasSize(1)
                .element(0)
                .extracting(Monitor::getId, Monitor::getFirstName, Monitor::getLastName, Monitor::getPhoneNumber, Monitor::getWorkDays, Monitor::getAbsences)
                .containsExactly(id, monitor.getFirstName(), monitor.getLastName(), monitor.getPhoneNumber(), monitor.getWorkDays(), monitor.getAbsences());
    }

    @Order(3)
    @Test
    void delete(){
        repository.deleteById(id);

        Assertions.assertThat(repository.listAll())
                .isEmpty();
    }

}
