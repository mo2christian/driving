package com.driving.planning.monitor;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.List;

@RegisterForReflection
public class MonitorResponse implements Serializable {

    public static final long serialVersionUID = 1L;

    private List<MonitorDto> monitors;

    public List<MonitorDto> getMonitors() {
        return monitors;
    }

    public void setMonitors(List<MonitorDto> monitors) {
        this.monitors = monitors;
    }
}
