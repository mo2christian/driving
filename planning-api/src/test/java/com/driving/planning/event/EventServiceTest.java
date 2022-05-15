package com.driving.planning.event;

import com.driving.planning.Generator;
import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.common.hourly.Day;
import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.event.domain.Event;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.dto.EventDto;
import com.driving.planning.monitor.MonitorService;
import com.driving.planning.monitor.dto.MonitorDto;
import com.driving.planning.school.SchoolService;
import com.driving.planning.student.StudentService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

@QuarkusTest
class EventServiceTest {

    @InjectMock
    EventRepository eventRepository;

    @InjectMock
    StudentService studentService;

    @InjectMock
    MonitorService monitorService;

    @InjectMock
    SchoolService schoolService;

    @Inject
    EventService eventService;

    @Inject
    EventMapper eventMapper;

    @BeforeEach
    void before(){
        when(schoolService.isSchoolOpened(anyString(), any())).thenReturn(true);
    }

    @Test
    void insertStudentEvent(){
        var eventDto = Generator.event();
        eventDto.setType(EventType.STUDENT);
        when(monitorService.list()).thenReturn(Collections.singletonList(monitorWithWorkingDate()));
        when(studentService.get(eventDto.getRelatedUserId())).thenReturn(Optional.of(Generator.student()));
        eventService.add(eventDto);
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository, times(1)).insert(eventCaptor.capture());
        var event = eventCaptor.getValue();
        Assertions.assertThat(eventDto)
                .extracting(EventDto::getBegin, EventDto::getEnd, EventDto::getEventDate, EventDto::getType, EventDto::getRelatedUserId)
                .containsExactly(event.getBegin(), event.getEnd(), event.getEventDate(), event.getType(), event.getRelatedUserId());
    }

    @Test
    void insertStudentNotFound(){
        var eventDto = Generator.event();
        eventDto.setType(EventType.STUDENT);
        when(monitorService.list()).thenReturn(Collections.singletonList(monitorWithWorkingDate()));
        when(studentService.get(eventDto.getRelatedUserId())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> eventService.add(eventDto))
                .isInstanceOf(PlanningException.class)
                .extracting("status")
                .isEqualTo(Response.Status.NOT_FOUND);
    }

    @Test
    void insertMonitorEvent(){
        var eventDto = Generator.event();
        eventDto.setType(EventType.MONITOR);
        when(monitorService.list()).thenReturn(Collections.singletonList(monitorWithWorkingDate()));
        when(monitorService.get(eventDto.getRelatedUserId())).thenReturn(Optional.of(Generator.monitor()));
        eventService.add(eventDto);
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository, times(1)).insert(eventCaptor.capture());
        var event = eventCaptor.getValue();
        Assertions.assertThat(eventDto)
                .extracting(EventDto::getBegin, EventDto::getEnd, EventDto::getEventDate, EventDto::getType, EventDto::getRelatedUserId)
                .containsExactly(event.getBegin(), event.getEnd(), event.getEventDate(), event.getType(), event.getRelatedUserId());
    }

    @Test
    void insertMonitorNotFound(){
        var eventDto = Generator.event();
        eventDto.setType(EventType.MONITOR);
        when(monitorService.list()).thenReturn(Collections.singletonList(monitorWithWorkingDate()));
        when(monitorService.get(eventDto.getRelatedUserId())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> eventService.add(eventDto))
                .isInstanceOf(PlanningException.class)
                .extracting("status")
                .isEqualTo(Response.Status.NOT_FOUND);
    }

    @Test
    void insertPlaceNotFound(){
        var eventDto = Generator.event();
        eventDto.setType(EventType.MONITOR);
        when(monitorService.list()).thenReturn(Collections.emptyList());
        when(eventRepository.listByDate(eventDto.getEventDate())).thenReturn(Collections.emptyList());
        when(monitorService.get(eventDto.getRelatedUserId())).thenReturn(Optional.of(Generator.monitor()));
        Assertions.assertThatThrownBy(() -> eventService.add(eventDto))
                .isInstanceOf(PlanningException.class)
                .extracting("status")
                .isEqualTo(Response.Status.BAD_REQUEST);
    }

    @Test
    void insertPlaceFound(){
        var eventDto = Generator.event();
        eventDto.setType(EventType.MONITOR);
        when(monitorService.list()).thenReturn(Collections.singletonList(monitorWithWorkingDate()));
        when(monitorService.get(eventDto.getRelatedUserId())).thenReturn(Optional.of(Generator.monitor()));
        eventService.add(eventDto);
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository, times(1)).insert(eventCaptor.capture());
        var event = eventCaptor.getValue();
        Assertions.assertThat(eventDto)
                .extracting(EventDto::getBegin, EventDto::getEnd, EventDto::getEventDate, EventDto::getType, EventDto::getRelatedUserId)
                .containsExactly(event.getBegin(), event.getEnd(), event.getEventDate(), event.getType(), event.getRelatedUserId());
    }

    @Test
    void hasNoPlace_CheckInterval(){
        var eventRef = eventMapper.toEntity(Generator.event());
        var eventDto = Generator.event();
        when(eventRepository.listByDate(eventDto.getEventDate())).thenReturn(Collections.singletonList(eventRef));
        when(monitorService.list()).thenReturn(Collections.singletonList(monitorWithWorkingDate()));
        Assertions.assertThat(eventService.isPLaceAvailable(eventDto)).isFalse();

        eventDto = Generator.event();
        eventDto.setBegin(eventDto.getBegin().minusMinutes(30));
        eventDto.setEnd(eventDto.getEnd().minusMinutes(30));
        when(eventRepository.listByDate(eventDto.getEventDate())).thenReturn(Collections.singletonList(eventRef));
        when(monitorService.list()).thenReturn(Collections.singletonList(monitorWithWorkingDate()));
        Assertions.assertThat(eventService.isPLaceAvailable(eventDto)).isFalse();

        eventDto = Generator.event();
        eventDto.setEnd(eventDto.getEnd().minusMinutes(30));
        when(eventRepository.listByDate(eventDto.getEventDate())).thenReturn(Collections.singletonList(eventRef));
        when(monitorService.list()).thenReturn(Collections.singletonList(monitorWithWorkingDate()));
        Assertions.assertThat(eventService.isPLaceAvailable(eventDto)).isFalse();

        eventDto = Generator.event();
        eventDto.setBegin(eventDto.getBegin().plusMinutes(10));
        eventDto.setEnd(eventDto.getEnd().minusMinutes(10));
        when(eventRepository.listByDate(eventDto.getEventDate())).thenReturn(Collections.singletonList(eventMapper.toEntity(eventDto)));
        when(monitorService.list()).thenReturn(Collections.singletonList(monitorWithWorkingDate()));
        Assertions.assertThat(eventService.isPLaceAvailable(eventDto)).isFalse();
    }

    @Test
    void hasNoPlace_checkDay(){
        var monitor = monitorWithWorkingDate();
        monitor.getWorkDays().forEach(h -> {
            h.setDay(Day.formDayOfWeek(LocalDate.now().plus(1, ChronoUnit.DAYS).getDayOfWeek()));
        });
        when(monitorService.list()).thenReturn(Collections.singletonList(monitor));
        var eventDto = Generator.event();
        Assertions.assertThat(eventService.isPLaceAvailable(eventDto)).isFalse();
    }

    @Test
    void list(){
        var eventDto = Generator.event();
        eventDto.setId("627bfbc9df76e41bf5568c61");
        var evt1 = eventMapper.toEntity(eventDto);
        eventDto.setEventDate(eventDto.getEventDate().plusDays(1));
        var evt2 = eventMapper.toEntity(eventDto);
        when(eventRepository.list()).thenReturn(Arrays.asList(evt1, evt2));
        Assertions.assertThat(eventService.list())
                .hasSize(2)
                .element(0)
                .extracting(EventDto::getEventDate)
                .isEqualTo(evt2.getEventDate());
    }

    @Test
    void schoolClose(){
        var event = Generator.event();
        event.setType(EventType.STUDENT);
        when(schoolService.isSchoolOpened(anyString(), any())).thenReturn(false);
        when(studentService.get(anyString())).thenReturn(Optional.of(Generator.student()));
        Assertions.assertThatThrownBy(() -> eventService.add(event))
                .isInstanceOf(PlanningException.class)
                .extracting("status")
                .isEqualTo(Response.Status.BAD_REQUEST);
    }

    private MonitorDto monitorWithWorkingDate(){
        var dto = Generator.monitor();
        var hourly = new Hourly();
        hourly.setDay(Day.formDayOfWeek(LocalDate.now().getDayOfWeek()));
        hourly.setBegin(LocalTime.of(8,0));
        hourly.setEnd(LocalTime.of(18,0));
        dto.getWorkDays().add(hourly);
        return dto;
    }

}
