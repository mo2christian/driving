package com.driving.planning.monitor;

import com.driving.planning.common.exception.PlanningException;
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
        repository.insert(monitor);
    }

    public void update(@Valid MonitorDto dto){
        logger.debugf("Update monitor %s", dto.getId());
        var optionalMonitor = repository.findById(dto.getId());
        if (optionalMonitor.isEmpty()){
            throw new PlanningException(Response.Status.NOT_FOUND, "Monitor not found");
        }
        var monitor = mapper.toEntity(dto);
        repository.update(monitor);
    }

    public void delete(@NotBlank String id){
        logger.debugf("Delete monitor %s", id);
        var monitor = repository.findById(id)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Monitor not found"));
        repository.delete(monitor.getId());
    }

    public Optional<MonitorDto> get(@NotBlank String id){
        logger.debugf("Get student with %s", id);
        var entity = repository.findById(id)
                .orElse(null);
        if (entity == null){
            return Optional.empty();
        }
        return Optional.of(mapper.toDto(entity));
    }

    public List<MonitorDto> list(){
        logger.debug("List monitor");
        return repository.list()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
