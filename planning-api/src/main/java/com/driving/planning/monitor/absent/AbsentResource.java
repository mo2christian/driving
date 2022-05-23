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

    private final AbsentService absentService;

    @Inject
    public AbsentResource(MonitorService monitorService,
                          AbsentService absentService) {
        this.monitorService = monitorService;
        this.absentService = absentService;
    }

    @Override
    public void add(@PathParam("id") String monitorId, @Valid AbsentRequest request){
        var monitor = monitorService.get(monitorId)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Monitor not found"));
        absentService.addAbsent(monitor, request);
    }

    @Override
    public void remove(@PathParam("id") String monitorId, @PathParam("ref") String ref){
        var monitor = monitorService.get(monitorId)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Monitor not found"));
        absentService.removeAbsent(monitor, ref);
    }

}
