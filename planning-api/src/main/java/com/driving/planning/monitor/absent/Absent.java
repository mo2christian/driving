package com.driving.planning.monitor.absent;

import com.driving.planning.common.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Absent implements Serializable {

    public static final long serialVersionUID = 1L;

    private String motif;

    private String reference;

    @JsonFormat(pattern = DatePattern.DATE)
    private @NotNull LocalDate start;

    @JsonFormat(pattern = DatePattern.DATE)
    private @NotNull LocalDate end;

    public Absent() {
    }

    public Absent(@NotNull LocalDate start, @NotNull LocalDate end) {
        setStart(start);
        setEnd(end);
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public boolean include(LocalDate dateTime){
        return (start.isEqual(dateTime) || start.isBefore(dateTime)) &&
                (end.isEqual(dateTime) || end.isAfter(dateTime));
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public @NotNull LocalDate getStart() {
        return start;
    }

    public void setStart(@NotNull LocalDate start) {
        this.start = start;
    }

    public @NotNull LocalDate getEnd() {
        return end;
    }

    public void setEnd(@NotNull LocalDate end) {
        this.end = end;
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
