package com.driving.planning.monitor.absent;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@RegisterForReflection
public class Absent implements Serializable {

    private static final long serialVersionUID = 1L;

    private @NotNull String reference;

    private @NotNull LocalDate start;

    private @NotNull LocalDate end;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Absent absent = (Absent) o;
        return reference.equals(absent.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }
}
