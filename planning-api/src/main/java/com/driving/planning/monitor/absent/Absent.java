package com.driving.planning.monitor.absent;

import com.driving.planning.common.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Absent implements Serializable {

    public static final long serialVersionUID = 1L;

    private String motif;

    @JsonFormat(pattern = DatePattern.DATE_TIME)
    @NotNull
    private LocalDateTime start;

    @JsonFormat(pattern = DatePattern.DATE_TIME)
    @NotNull
    private LocalDateTime end;

    public Absent() {
    }

    public Absent(LocalDateTime start, LocalDateTime end) {
        setStart(start);
        setEnd(end);
    }

    public boolean include(LocalDateTime dateTime){
        return (start.isEqual(dateTime) || start.isBefore(dateTime)) &&
                (end.isEqual(dateTime) || end.isAfter(dateTime));
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
        if (this.start != null){
            this.start = start
                    .withSecond(0)
                    .withNano(0);
        }
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
        if (this.end != null){
            this.end = end
                    .withSecond(0)
                    .withNano(0);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Absent absent = (Absent) o;
        return Objects.equals(start, absent.start) && Objects.equals(end, absent.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
