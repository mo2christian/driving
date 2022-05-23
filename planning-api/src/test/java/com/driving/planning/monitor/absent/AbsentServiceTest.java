package com.driving.planning.monitor.absent;

import com.driving.planning.Generator;
import com.driving.planning.common.hourly.Day;
import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.event.EventService;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.dto.EventDto;
import com.driving.planning.monitor.MonitorService;
import com.driving.planning.monitor.dto.MonitorDto;
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
class AbsentServiceTest {

    @InjectMock
    MonitorService monitorService;

    @InjectMock
    EventService eventService;

    @InjectMock
    SchoolService schoolService;

    @Inject
    AbsentService absentService;

    @Test
    void removeEvent(){
        var monitor = Generator.monitor();
        var ref = monitor.getAbsents().get(0).getReference();
        absentService.removeAbsent(monitor, ref);
        ArgumentCaptor<MonitorDto> dtoCaptor = ArgumentCaptor.forClass(MonitorDto.class);
        verify(monitorService, atMostOnce()).update(dtoCaptor.capture());
        assertThat(dtoCaptor.getValue())
                .isNotNull();
        assertThat(dtoCaptor.getValue().getAbsents())
                .isEmpty();
        ArgumentCaptor<String> refCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventService, atLeastOnce()).deleteByRef(refCaptor.capture());
        assertThat(refCaptor.getValue())
                .isEqualTo(ref);
    }

    @Test
    void addEvent(){
        LocalDate now = LocalDate.now();
        var absent = new AbsentRequest(now, now.plusDays(1));
        var monitor = new MonitorDto();
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

        absentService.addAbsent(monitor, absent);

        ArgumentCaptor<EventDto> dtoCaptor = ArgumentCaptor.forClass(EventDto.class);
        verify(eventService, times(2)).add(dtoCaptor.capture());
        assertThat(dtoCaptor.getAllValues())
                .element(0)
                .extracting(EventDto::getEventDate, EventDto::getRelatedUserId, EventDto::getType)
                .contains(absent.getStart(), monitor.getId(), EventType.MONITOR);

        ArgumentCaptor<MonitorDto> monitorCaptor = ArgumentCaptor.forClass(MonitorDto.class);
        verify(monitorService, times(1)).update(monitorCaptor.capture());
        assertThat(monitorCaptor.getValue().getAbsents())
                .hasSize(1)
                .element(0)
                .extracting(Absent::getStart, Absent::getEnd)
                .contains(absent.getStart(), absent.getEnd());
        assertThat(monitorCaptor.getValue().getAbsents())
                .hasSize(1)
                .element(0)
                .extracting(Absent::getReference)
                .isNotNull();
    }

}
