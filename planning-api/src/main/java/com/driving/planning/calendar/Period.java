package com.driving.planning.calendar;

import com.driving.planning.common.DatePattern;
import com.driving.planning.monitor.MonitorDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

@RegisterForReflection
public class Period implements Serializable {

    public static final long serialVersionUID = 1L;

    @Schema(implementation = String.class, format = "partial-time")
    @JsonFormat(pattern = DatePattern.TIME)
    private LocalTime begin;

    @Schema(implementation = String.class, format = "partial-time")
    @JsonFormat(pattern = DatePattern.TIME)
    private LocalTime end;

    private int availablePlaces;

    private List<MonitorDto> monitors;

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

    public int getAvailablePlaces() {
        return availablePlaces;
    }

    public void setAvailablePlaces(int availablePlaces) {
        this.availablePlaces = availablePlaces;
    }

    public List<MonitorDto> getMonitors() {
        return monitors;
    }

    public void setMonitors(List<MonitorDto> monitors) {
        this.monitors = monitors;
    }

    public boolean include(LocalTime start, LocalTime end){
        return (start.isBefore(begin) || start.equals(begin)) && (end.isAfter(this.end) || end.equals(this.end));
    }

}
