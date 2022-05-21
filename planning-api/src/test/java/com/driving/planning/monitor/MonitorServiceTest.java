package com.driving.planning.monitor;

import com.driving.planning.Generator;
import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.monitor.domain.Monitor;
import com.driving.planning.monitor.dto.MonitorDto;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@QuarkusTest
class MonitorServiceTest {

    @InjectMock
    MonitorRepository repository;

    @Inject
    MonitorService service;

    @Test
    void add(){
        MonitorDto monitorDto = Generator.monitor();
        service.add(monitorDto);
        ArgumentCaptor<Monitor> monitorCaptor = ArgumentCaptor.forClass(Monitor.class);
        verify(repository, atMostOnce()).insert(monitorCaptor.capture());
        Monitor monitor = monitorCaptor.getValue();
        assertThat(monitorDto)
                .extracting(MonitorDto::getFirstName, MonitorDto::getLastName, MonitorDto::getPhoneNumber, MonitorDto::getWorkDays, MonitorDto::getAbsents)
                .containsExactly(monitor.getFirstName(), monitor.getLastName(), monitor.getPhoneNumber(), monitor.getWorkDays(), monitor.getAbsents());
    }

    @Test
    void update(){
        var id = "60f6ab7f443a1d3e27b6cbaf";
        MonitorDto monitorDto = Generator.monitor();
        monitorDto.setId(id);
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThatExceptionOfType(PlanningException.class).isThrownBy(() -> service.update(monitorDto));

        when(repository.findById(id)).thenReturn(Optional.of(new Monitor()));
        service.update(monitorDto);
        ArgumentCaptor<Monitor> monitorCaptor = ArgumentCaptor.forClass(Monitor.class);
        verify(repository, atMostOnce()).update(monitorCaptor.capture());
        Monitor monitor = monitorCaptor.getValue();
        assertThat(monitorDto)
                .extracting(MonitorDto::getId, MonitorDto::getFirstName, MonitorDto::getLastName, MonitorDto::getPhoneNumber, MonitorDto::getWorkDays, MonitorDto::getAbsents)
                .containsExactly(monitor.getId().toString(), monitor.getFirstName(), monitor.getLastName(), monitor.getPhoneNumber(), monitor.getWorkDays(), monitor.getAbsents());
    }

    @Test
    void delete(){
        var id = "60f6ab7f443a1d3e27b6cbaf";
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThatExceptionOfType(PlanningException.class).isThrownBy(() -> service.delete(id));

        var monitor = new Monitor();
        monitor.setId(new ObjectId(id));
        when(repository.findById(id)).thenReturn(Optional.of(monitor));
        ArgumentCaptor<ObjectId> idCaptor = ArgumentCaptor.forClass(ObjectId.class);
        service.delete(id);
        verify(repository, atMostOnce()).delete(idCaptor.capture());
        assertThat(idCaptor.getValue()).hasToString(id);
    }

    @Test
    void get(){
        var id = "60f6ab7f443a1d3e27b6cbaf";
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThat(service.get(id)).isEmpty();

        var monitor = new Monitor();
        var monitorDto = Generator.monitor();
        monitor.setId(new ObjectId(id));
        monitor.setPhoneNumber(monitorDto.getPhoneNumber());
        monitor.setFirstName(monitorDto.getFirstName());
        monitor.setLastName(monitorDto.getLastName());
        when(repository.findById(id)).thenReturn(Optional.of(monitor));
        assertThat(service.get(id)).isNotEmpty()
                .get()
                .extracting(MonitorDto::getId, MonitorDto::getFirstName, MonitorDto::getLastName, MonitorDto::getPhoneNumber, MonitorDto::getWorkDays, MonitorDto::getAbsents)
                .containsExactly(monitor.getId().toString(), monitor.getFirstName(), monitor.getLastName(), monitor.getPhoneNumber(), monitor.getWorkDays(), monitor.getAbsents());
    }

    @Test
    void list(){
        var id = "60f6ab7f443a1d3e27b6cbaf";
        var monitor = new Monitor();
        var monitorDto = Generator.monitor();
        monitor.setId(new ObjectId(id));
        monitor.setPhoneNumber(monitorDto.getPhoneNumber());
        monitor.setFirstName(monitorDto.getFirstName());
        monitor.setLastName(monitorDto.getLastName());
        when(repository.list()).thenReturn(Collections.singletonList(monitor));
        assertThat(service.list()).hasSize(1)
                .element(0)
                .extracting(MonitorDto::getId, MonitorDto::getFirstName, MonitorDto::getLastName, MonitorDto::getPhoneNumber, MonitorDto::getWorkDays)
                .containsExactly(monitor.getId().toString(), monitor.getFirstName(), monitor.getLastName(), monitor.getPhoneNumber(), monitor.getWorkDays());
    }

}
