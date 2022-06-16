package com.driving.planning.monitor.absence;

import com.driving.planning.common.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

public class AbsenceRequest implements Serializable {

    public static final long serialVersionUID = 1L;

    private String motif;

    private String reference;

    @JsonFormat(pattern = DatePattern.DATE)
    private @NotNull LocalDate start;

    @JsonFormat(pattern = DatePattern.DATE)
    private @NotNull LocalDate end;

    public AbsenceRequest() {
    }

    public AbsenceRequest(@NotNull LocalDate start, @NotNull LocalDate end) {
        setStart(start);
        setEnd(end);
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

}
