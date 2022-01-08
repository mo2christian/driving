package com.driving.planning.calendar;

import com.driving.planning.common.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class DayCalendar implements Serializable {

    public static final long serialVersionUID = 1L;

    @JsonFormat(pattern = DatePattern.DATE)
    private LocalDate day;

    private List<Period> periods;

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public List<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }
}
