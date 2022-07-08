package com.driving.planning.monitor;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.monitor.dto.MonitorAbsenceDto;
import com.driving.planning.monitor.dto.MonitorDto;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Traced
@ApplicationScoped
public class MonitorService {

    private static final String NOT_FOUND_MSG = "Monitor not found";

    private final MonitorRepository repository;

    private final MonitorMapper mapper;

    private final Logger logger;

    @Inject
    public MonitorService(MonitorRepository repository, MonitorMapper mapper, Logger logger) {
        this.repository = repository;
        this.mapper = mapper;
        this.logger = logger;
    }

    public void add(@Valid MonitorDto dto){
        logger.debugf("Create monitor with phone number %s", dto.getPhoneNumber());
        var monitor = mapper.toEntity(dto);
        repository.persist(monitor);
    }

    public void updateMonitor(@Valid MonitorDto dto){
        logger.debugf("Update monitor %s", dto.getId());
        var optionalMonitor = repository.findById(dto.getId());
        if (optionalMonitor.isEmpty()){
            throw new PlanningException(Response.Status.NOT_FOUND, NOT_FOUND_MSG);
        }
        var monitor = mapper.toEntity(dto);
        monitor.setAbsences(optionalMonitor.get().getAbsences());
        repository.update(monitor);
    }

    public void updateMonitorWithAbsence(@Valid MonitorAbsenceDto dto){
        logger.debugf("Update monitor %s with absence", dto.getId());
        var optionalMonitor = repository.findById(dto.getId());
        if (optionalMonitor.isEmpty()){
            throw new PlanningException(Response.Status.NOT_FOUND, NOT_FOUND_MSG);
        }
        var monitor = mapper.toMonitorWithAbsence(dto);
        repository.update(monitor);
    }

    public void delete(@NotBlank String id){
        logger.debugf("Delete monitor %s", id);
        var monitor = repository.findById(id)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, NOT_FOUND_MSG));
        repository.deleteById(monitor.getId());
    }

    public Optional<MonitorAbsenceDto> get(@NotBlank String id){
        logger.debugf("Get student with %s", id);
        var entity = repository.findById(id)
                .orElse(null);
        if (entity == null){
            return Optional.empty();
        }
        return Optional.of(mapper.toMonitorAbsenceDto(entity));
    }

    public List<MonitorAbsenceDto> list(){
        logger.debug("List monitor");
        return repository.listAll()
                .stream()
                .map(mapper::toMonitorAbsenceDto)
                .collect(Collectors.toList());
    }
}
