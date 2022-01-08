package com.driving.planning.calendar;

import com.driving.planning.common.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class Calendar implements Serializable {

    public static final long serialVersionUID = 1L;

    @JsonFormat(pattern = DatePattern.DATE)
    private LocalDate start;

    @JsonFormat(pattern = DatePattern.DATE)
    private LocalDate end;

    private List<DayCalendar> dayCalendars;

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public List<DayCalendar> getDayCalendars() {
        return dayCalendars;
    }

    public void setDayCalendars(List<DayCalendar> dayCalendars) {
        this.dayCalendars = dayCalendars;
    }
}
