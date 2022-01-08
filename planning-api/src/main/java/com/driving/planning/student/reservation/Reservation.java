package com.driving.planning.student.reservation;

import com.driving.planning.common.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@RegisterForReflection
public class Reservation implements Serializable {

    public static final long serialVersionUID = 1L;

    @NotNull
    @JsonFormat(pattern = DatePattern.DATE)
    private LocalDate date;

    @NotNull
    @Schema(implementation = String.class, format = "partial-time")
    @JsonFormat(pattern = DatePattern.TIME)
    private LocalTime begin;

    @NotNull
    @Schema(implementation = String.class, format = "partial-time")
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
        if (this.begin != null) {
            this.begin = begin
                    .withSecond(0)
                    .withNano(0);
        }
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
        if (end != null) {
            this.end = end
                    .withSecond(0)
                    .withNano(0);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return date.equals(that.date) && begin.equals(that.begin) && end.equals(that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, begin, end);
    }
}
