package com.driving.planning.monitor;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.monitor.dto.MonitorAbsenceDto;
import com.driving.planning.monitor.dto.MonitorDto;
import com.driving.planning.monitor.dto.MonitorResponse;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class MonitorResource implements MonitorEndpoint {

    private final MonitorService service;

    private final Logger logger;

    @Inject
    public MonitorResource(MonitorService service, Logger logger) {
        this.service = service;
        this.logger = logger;
    }

    @Override
    public MonitorResponse list(){
        logger.debug("List monitors");
        var response = new MonitorResponse();
        response.setMonitors(service.list());
        return response;
    }

    @Override
    public void add(@Valid MonitorDto monitorDto){
        logger.debugf("Add monitor %s", monitorDto.getPhoneNumber());
        monitorDto.setId(null);
        service.add(monitorDto);
    }

    @Override
    public MonitorAbsenceDto get(@PathParam("id") String id){
        logger.debugf("Get monitor %s", id);
        return service.get(id)
                .orElseThrow(this::notFound);
    }

    @Override
    public void update(@PathParam("id") String id, @Valid MonitorDto dto){
        logger.debugf("Update monitor %s", id);
        var monitor = service.get(id)
                .orElseThrow(this::notFound);
        if (!dto.getWorkDays().equals(monitor.getWorkDays())){
            throw new PlanningException(Response.Status.BAD_REQUEST, "Cannot modify work days");
        }
        dto.setId(id);
        service.update(dto);
    }

    @Override
    public void delete(@PathParam("id") String id){
        logger.debugf("Delete monitor %s", id);
        var monitorDto = service.get(id)
                .orElseThrow(this::notFound);
        service.delete(monitorDto.getId());
    }

    private PlanningException notFound(){
        return new PlanningException(Response.Status.NOT_FOUND, "Monitor not found");
    }
}
