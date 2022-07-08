package com.driving.planning.monitor.absence;

import com.driving.planning.Generator;
import com.driving.planning.common.hourly.Day;
import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.event.EventService;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.dto.EventDto;
import com.driving.planning.monitor.MonitorService;
import com.driving.planning.monitor.dto.MonitorAbsenceDto;
import com.driving.planning.school.SchoolService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
class AbsenceServiceTest {

    @InjectMock
    MonitorService monitorService;

    @InjectMock
    EventService eventService;

    @InjectMock
    SchoolService schoolService;

    @Inject
    AbsenceService absenceService;

    @Test
    void removeEvent(){
        var monitor = Generator.monitor();
        var ref = monitor.getAbsences().get(0).getReference();
        absenceService.removeAbsent(monitor, ref);
        ArgumentCaptor<MonitorAbsenceDto> dtoCaptor = ArgumentCaptor.forClass(MonitorAbsenceDto.class);
        verify(monitorService, atMostOnce()).updateMonitorWithAbsence(dtoCaptor.capture());
        assertThat(dtoCaptor.getValue())
                .isNotNull();
        assertThat(dtoCaptor.getValue().getAbsences())
                .isEmpty();
        ArgumentCaptor<String> refCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventService, atLeastOnce()).deleteByRef(refCaptor.capture());
        assertThat(refCaptor.getValue())
                .isEqualTo(ref);
    }

    @Test
    void addEvent(){
        LocalDate now = LocalDate.now();
        var absent = new AbsenceRequest(now, now.plusDays(1));
        var monitor = new MonitorAbsenceDto();
        monitor.setId("id");
        when(monitorService.get(monitor.getId())).thenReturn(Optional.of(monitor));
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
        when(schoolService.get(anyString())).thenReturn(Optional.of(school));

        absenceService.addAbsent(monitor, absent);

        ArgumentCaptor<EventDto> dtoCaptor = ArgumentCaptor.forClass(EventDto.class);
        verify(eventService, times(2)).add(dtoCaptor.capture());
        assertThat(dtoCaptor.getAllValues())
                .element(0)
                .extracting(EventDto::getEventDate, EventDto::getRelatedUserId, EventDto::getType)
                .contains(absent.getStart(), monitor.getId(), EventType.MONITOR);

        ArgumentCaptor<MonitorAbsenceDto> monitorCaptor = ArgumentCaptor.forClass(MonitorAbsenceDto.class);
        verify(monitorService, times(1)).updateMonitorWithAbsence(monitorCaptor.capture());
        assertThat(monitorCaptor.getValue().getAbsences())
                .hasSize(1)
                .element(0)
                .extracting(Absence::getStart, Absence::getEnd)
                .contains(absent.getStart(), absent.getEnd());
        assertThat(monitorCaptor.getValue().getAbsences())
                .hasSize(1)
                .element(0)
                .extracting(Absence::getReference)
                .isNotNull();
    }

}
