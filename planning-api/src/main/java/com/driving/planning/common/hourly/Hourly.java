package com.driving.planning.common.hourly;

import com.driving.planning.common.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Hourly implements Serializable {

    private static final long serialVersionUID = 1L;

    private Day day;

    @Schema(implementation = String.class, format = "partial-time")
    @JsonFormat(pattern = DatePattern.TIME)
    private LocalTime begin;

    @Schema(implementation = String.class, format = "partial-time")
    @JsonFormat(pattern = DatePattern.TIME)
    private LocalTime end;

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hourly hourly = (Hourly) o;
        var formatter = DateTimeFormatter.ofPattern(DatePattern.TIME_NO_SECOND);
        if (hourly.begin == null || begin == null){
            return false;
        }
        if (hourly.end == null || end == null){
            return false;
        }
        return hourly.day == day  &&
                begin.format(formatter).equals(hourly.begin.format(formatter)) &&
                end.format(formatter).equals(hourly.end.format(formatter));
    }

    @Override
    public int hashCode() {
        return Objects.hash(day);
    }

    @Override
    public String toString() {
        return "Hourly{" +
                "day=" + day +
                ", begin=" + begin +
                ", end=" + end +
                '}';
    }
}
