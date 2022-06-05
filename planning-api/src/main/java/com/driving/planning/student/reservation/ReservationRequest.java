package com.driving.planning.student.reservation;

import com.driving.planning.common.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@RegisterForReflection
public class ReservationRequest {

    @NotNull
    @JsonFormat(pattern = DatePattern.DATE)
    private LocalDate date;

    @NotNull
    @JsonFormat(pattern = DatePattern.TIME)
    private LocalTime begin;

    @NotNull
    @JsonFormat(pattern = DatePattern.TIME)
    private LocalTime end;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getBegin() {
        return begin;
    }

    public void setBegin(LocalTime begin) {
        this.begin = begin;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }
}
