package com.driving.planning.monitor.absence;

import com.driving.planning.common.exception.NotFoundException;
import com.driving.planning.monitor.MonitorService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.PathParam;

@ApplicationScoped
public class AbsenceResource implements AbsenceEndpoint {

    private final MonitorService monitorService;

    private final AbsenceService absenceService;

    @Inject
    public AbsenceResource(MonitorService monitorService,
                           AbsenceService absenceService) {
        this.monitorService = monitorService;
        this.absenceService = absenceService;
    }

    @Override
    public void add(@PathParam("id") String monitorId, @Valid AbsenceRequest request){
        var monitor = monitorService.get(monitorId)
                .orElseThrow(() -> new NotFoundException("Monitor not found"));
        absenceService.addAbsent(monitor, request);
    }

    @Override
    public void remove(@PathParam("id") String monitorId, @PathParam("ref") String ref){
        var monitor = monitorService.get(monitorId)
                .orElseThrow(() -> new NotFoundException("Monitor not found"));
        absenceService.removeAbsent(monitor, ref);
    }

}
