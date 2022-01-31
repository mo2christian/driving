package com.driving.planning.calendar;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.common.hourly.Day;
import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.monitor.MonitorService;
import com.driving.planning.monitor.absent.Absent;
import com.driving.planning.monitor.dto.MonitorDto;
import com.driving.planning.school.SchoolService;
import com.driving.planning.school.dto.SchoolDto;
import com.driving.planning.student.reservation.Reservation;
import com.driving.planning.student.reservation.ReservationRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
class CalendarServiceTest {

    @InjectMock
    SchoolService schoolService;

    @InjectMock
    MonitorService monitorService;

    @InjectMock
    ReservationRepository reservationRepository;

    @Inject
    CalendarService calendarService;

    @BeforeEach
    void init(){
        var schoolHourly = new Hourly();
        schoolHourly.setDay(Day.MONDAY);
        schoolHourly.setBegin(LocalTime.of(8, 0));
        schoolHourly.setEnd(LocalTime.of(11,30));
        var school = new SchoolDto();
        school.setWorkDays(Collections.singleton(schoolHourly));
        when(schoolService.get(anyString())).thenReturn(Optional.of(school));

        var monitor = monitor();
        when(monitorService.list()).thenReturn(Collections.singletonList(monitor));
    }

    @Test
    void calendarWithNoReservation(){
        var date = LocalDate.of(2021, 9, 13);
        when(reservationRepository.findByDate(date)).thenReturn(Collections.emptyList());

        var dayCalendar = calendarService.get(date);
        assertThat(dayCalendar.getDay()).isEqualTo(date);
        assertThat(dayCalendar.getPeriods()).hasSize(3);
        assertThat(dayCalendar.getPeriods()).element(0)
                .extracting(Period::getBegin, Period::getEnd, Period::getAvailablePlaces)
                .containsExactly(LocalTime.of(8,0), LocalTime.of(9, 0), 0);
        assertThat(dayCalendar.getPeriods()).element(0)
                        .matches(period -> period.getMonitors().isEmpty());

        assertThat(dayCalendar.getPeriods()).element(1)
                .extracting(Period::getBegin, Period::getEnd, Period::getAvailablePlaces)
                .containsExactly(LocalTime.of(9,0), LocalTime.of(10, 0), 1);
        assertThat(dayCalendar.getPeriods()).element(1)
                .matches(period -> period.getMonitors().size() == 1);

        assertThat(dayCalendar.getPeriods()).element(2)
                .extracting(Period::getBegin, Period::getEnd, Period::getAvailablePlaces)
                .containsExactly(LocalTime.of(10,0), LocalTime.of(11, 0), 1);
        assertThat(dayCalendar.getPeriods()).element(2)
                .matches(period -> period.getMonitors().size() == 1);
    }

    @Test
    void calendarWithReservation(){
        var date = LocalDate.of(2021, 9, 13);
        var reservation = new Reservation();
        reservation.setDate(date);
        reservation.setBegin(LocalTime.of(10,0));
        reservation.setEnd(LocalTime.of(11,0));
        when(reservationRepository.findByDate(date)).thenReturn(Collections.singletonList(reservation));

        var dayCalendar = calendarService.get(date);
        assertThat(dayCalendar.getDay()).isEqualTo(date);
        assertThat(dayCalendar.getPeriods()).hasSize(3);
        assertThat(dayCalendar.getPeriods()).element(0)
                .extracting(Period::getBegin, Period::getEnd, Period::getAvailablePlaces)
                .containsExactly(LocalTime.of(8,0), LocalTime.of(9, 0), 0);
        assertThat(dayCalendar.getPeriods()).element(0)
                .matches(period -> period.getMonitors().isEmpty());

        assertThat(dayCalendar.getPeriods()).element(1)
                .extracting(Period::getBegin, Period::getEnd, Period::getAvailablePlaces)
                .containsExactly(LocalTime.of(9,0), LocalTime.of(10, 0), 1);
        assertThat(dayCalendar.getPeriods()).element(1)
                .matches(period -> period.getMonitors().size() == 1);

        assertThat(dayCalendar.getPeriods()).element(2)
                .extracting(Period::getBegin, Period::getEnd, Period::getAvailablePlaces)
                .containsExactly(LocalTime.of(10,0), LocalTime.of(11, 0), 0);
        assertThat(dayCalendar.getPeriods()).element(2)
                .matches(period -> period.getMonitors().size() == 1);
    }

    @Test
    void calendarSchoolNotFound(){
        when(schoolService.get(anyString())).thenReturn(Optional.empty());
        var date = LocalDate.now();
        assertThatThrownBy(() -> calendarService.get(date))
                .isInstanceOf(PlanningException.class);
    }

    @Test
    void calendarEmpty(){
        var date = LocalDate.of(2021, 9, 14);
        var dayCalendar = calendarService.get(date);
        assertThat(dayCalendar.getDay()).isEqualTo(date);
        assertThat(dayCalendar.getPeriods()).isEmpty();
    }

    @Test
    void calendarWithAbsent(){
        var monitor = monitor();
        var absent = new Absent();
        absent.setStart(LocalDateTime.of(2021, 9, 27, 9,0));
        absent.setEnd(LocalDateTime.of(2021, 9, 27, 11,0));
        monitor.setAbsents(Collections.singleton(absent));
        when(monitorService.list()).thenReturn(Collections.singletonList(monitor));
        var date = LocalDate.of(2021, 9, 27);
        var dayCalendar = calendarService.get(date);
        assertThat(dayCalendar.getDay()).isEqualTo(date);
        assertThat(dayCalendar.getPeriods()).hasSize(3);
        assertThat(dayCalendar.getPeriods()).element(0)
                .extracting(Period::getBegin, Period::getEnd, Period::getAvailablePlaces)
                .containsExactly(LocalTime.of(8,0), LocalTime.of(9, 0), 0);
        assertThat(dayCalendar.getPeriods()).element(0)
                .matches(period -> period.getMonitors().isEmpty());

        assertThat(dayCalendar.getPeriods()).element(1)
                .extracting(Period::getBegin, Period::getEnd, Period::getAvailablePlaces)
                .containsExactly(LocalTime.of(9,0), LocalTime.of(10, 0), 0);
        assertThat(dayCalendar.getPeriods()).element(0)
                .matches(period -> period.getMonitors().isEmpty());

        assertThat(dayCalendar.getPeriods()).element(2)
                .extracting(Period::getBegin, Period::getEnd, Period::getAvailablePlaces)
                .containsExactly(LocalTime.of(10,0), LocalTime.of(11, 0), 0);
        assertThat(dayCalendar.getPeriods()).element(0)
                .matches(period -> period.getMonitors().isEmpty());
    }

    private MonitorDto monitor(){
        var monitorHourly = new Hourly();
        monitorHourly.setDay(Day.MONDAY);
        monitorHourly.setBegin(LocalTime.of(9,0));
        monitorHourly.setEnd(LocalTime.of(11,30));
        var monitor = new MonitorDto();
        monitor.setWorkDays(Collections.singleton(monitorHourly));
        return monitor;
    }

}
