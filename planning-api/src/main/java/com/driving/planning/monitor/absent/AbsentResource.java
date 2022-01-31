package com.driving.planning.monitor.absent;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.monitor.MonitorService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class AbsentResource implements AbsentEndpoint {

    private final MonitorService monitorService;

    @Inject
    public AbsentResource(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @Override
    public void add(@PathParam("id") String monitorId, @Valid Absent absent){
        var monitor = monitorService.get(monitorId)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Monitor not found"));
        monitor.getAbsents().add(absent);
        monitorService.update(monitor);
    }

    @Override
    public void remove(@PathParam("id") String monitorId, @Valid Absent absent){
        var monitor = monitorService.get(monitorId)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Monitor not found"));
        monitor.getAbsents().remove(absent);
        monitorService.update(monitor);
    }

}
