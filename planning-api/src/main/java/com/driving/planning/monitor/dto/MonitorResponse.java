package com.driving.planning.monitor.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.List;

@RegisterForReflection
public class MonitorResponse implements Serializable {

    public static final long serialVersionUID = 1L;

    private List<MonitorAbsenceDto> monitors;

    public List<MonitorAbsenceDto> getMonitors() {
        return monitors;
    }

    public void setMonitors(List<MonitorAbsenceDto> monitors) {
        this.monitors = monitors;
    }
}
