package com.driving.planning.calendar;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.config.database.Tenant;
import com.driving.planning.monitor.MonitorService;
import com.driving.planning.monitor.dto.MonitorDto;
import com.driving.planning.school.SchoolService;
import com.driving.planning.student.reservation.ReservationRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

@ApplicationScoped
public class CalendarService {

    private final SchoolService schoolService;

    private final MonitorService monitorService;

    private final ReservationRepository reservationRepository;

    private final Tenant tenant;

    @Inject
    public CalendarService(SchoolService schoolService,
                           MonitorService monitorService,
                           ReservationRepository reservationRepository,
                           Tenant tenant) {
        this.schoolService = schoolService;
        this.monitorService = monitorService;
        this.reservationRepository = reservationRepository;
        this.tenant = tenant;
    }

    public DayCalendar get(@NotNull LocalDate date){
        //get workday on school
        var school = schoolService.get(tenant.getName())
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, String.format("School %s not found", tenant.getName())));
        var hourlyOptional = school.getWorkDays().stream()
                .filter(h -> h.getDay().getDayOfWeek() == date.getDayOfWeek())
                .findFirst();
        if (hourlyOptional.isEmpty()){
            var calendar = new DayCalendar();
            calendar.setDay(date);
            calendar.setPeriods(Collections.emptyList());
            return calendar;
        }
        //generate period
        var beginTime = hourlyOptional.get().getBegin();
        final var endTime = hourlyOptional.get().getEnd();
        final var periods = new ArrayList<Period>();
        while (beginTime.isBefore(endTime)
                && (beginTime.plusHours(1).equals(endTime) || beginTime.plusHours(1).isBefore(endTime))){
            var period = new Period();
            period.setBegin(beginTime);
            period.setEnd(beginTime.plusHours(1));
            periods.add(period);
            beginTime = period.getEnd();
        }
        //calculate nb of available places for each period
        var monitors = monitorService.list();
        var reservations = reservationRepository.findByDate(date);
        for (var period : periods){
            int nbMonitors = 0;
            var monitorsAvailable = new ArrayList<MonitorDto>();
            for (var monitor : monitors){
                if (isMonitorAvailable(monitor, period, date)){
                    nbMonitors++;
                    monitorsAvailable.add(monitor);
                }
            }
            period.setMonitors(monitorsAvailable);
            int nbReservations = 0;
            for (var reservation : reservations){
                if (period.include(reservation.getBegin(), reservation.getEnd())){
                    nbReservations++;
                }
            }
            period.setAvailablePlaces(nbMonitors - nbReservations);
        }
        var dayCalendar = new DayCalendar();
        dayCalendar.setDay(date);
        dayCalendar.setPeriods(periods);
        return dayCalendar;
    }

    private boolean isMonitorAvailable(MonitorDto monitorDto, Period period, LocalDate date){
        var hourlyOptional = monitorDto.getWorkDays().stream()
                .filter(h -> h.getDay().getDayOfWeek() == date.getDayOfWeek())
                .findFirst();
        if (hourlyOptional.isEmpty()){
            return false;
        }
        var beginDateTime = LocalDateTime.of(date, period.getBegin());
        var endDateTime = LocalDateTime.of(date, period.getEnd());
        var absents = monitorDto.getAbsents()
                .stream()
                .filter(absent -> absent.include(beginDateTime) || absent.include(endDateTime))
                .findFirst();
        var hourly = hourlyOptional.get();
        return period.include(hourly.getBegin(), hourly.getEnd()) && absents.isEmpty();
    }

}
