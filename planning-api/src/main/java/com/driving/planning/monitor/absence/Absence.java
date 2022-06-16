package com.driving.planning.monitor.absence;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.bson.codecs.pojo.annotations.BsonProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@RegisterForReflection
public class Absence implements Serializable {

    private static final long serialVersionUID = 1L;

    private @NotNull @BsonProperty("ref") String reference;

    private @NotNull @BsonProperty("start") LocalDate start;

    private @NotNull @BsonProperty("end") LocalDate end;

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
        Absence absence = (Absence) o;
        return reference.equals(absence.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }
}
