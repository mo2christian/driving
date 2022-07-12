package com.driving.planning.event;

import com.driving.planning.common.exception.BadRequestException;
import com.driving.planning.common.exception.NotFoundException;
import com.driving.planning.config.database.Tenant;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.dto.EventDto;
import com.driving.planning.monitor.MonitorService;
import com.driving.planning.school.SchoolService;
import com.driving.planning.student.StudentService;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.driving.planning.common.Utils.between;

@Traced
@ApplicationScoped
public class EventService {

    private final EventMapper mapper;

    private final EventRepository repository;

    private final MonitorService monitorService;

    private final StudentService studentService;

    private final Logger logger;

    private final SchoolService schoolService;

    private final Tenant tenant;

    @Inject
    public EventService(EventMapper mapper,
                        EventRepository repository,
                        MonitorService monitorService,
                        StudentService studentService,
                        SchoolService schoolService,
                        Tenant tenant,
                        Logger logger) {
        this.mapper = mapper;
        this.repository = repository;
        this.monitorService = monitorService;
        this.studentService = studentService;
        this.logger = logger;
        this.schoolService = schoolService;
        this.tenant = tenant;
    }

    public void add(@Valid final EventDto dto){
        logger.debugf("Add event %s", dto);
        if (dto.getType() == EventType.MONITOR && monitorService.get(dto.getRelatedUserId()).isEmpty()){
            throw new NotFoundException(String.format("Monitor with id %s do not exist", dto.getRelatedUserId()));
        }
        if (dto.getType() == EventType.STUDENT && studentService.get(dto.getRelatedUserId()).isEmpty()){
            throw new NotFoundException(String.format("Student with id %s do not exist", dto.getRelatedUserId()));
        }
        if (schoolService.isSchoolClosed(tenant.getName(), LocalDateTime.of(dto.getEventDate(), dto.getBegin())) ||
                schoolService.isSchoolClosed(tenant.getName(), LocalDateTime.of(dto.getEventDate(), dto.getEnd()))){
            throw new BadRequestException("School is not open at that time");
        }
        if (!isPlaceAvailable(dto)){
            throw new BadRequestException("No place available for this event");
        }
        var event = mapper.toEntity(dto);
        repository.persist(event);
    }

    public boolean isPlaceAvailable(final EventDto eventDto){
        long nbEvent = repository.listByDate(eventDto.getEventDate())
                .stream()
                .filter(event ->
                                between(event.getBegin(), eventDto.getBegin(), event.getEnd()) ||
                                between(event.getBegin(), eventDto.getEnd(), event.getEnd())
                        ).count();
        long nbPlace = monitorService.list()
                .stream()
                .filter(dto -> dto.getWorkDays().stream()
                        .map(h -> h.getDay().getDayOfWeek())
                        .anyMatch(dow -> dow == eventDto.getEventDate().getDayOfWeek()))
                .count();
        return nbPlace >= nbEvent + 1;
    }

    public List<EventDto> list(){
        return repository.listAll()
                .stream()
                .map(mapper::toDto)
                .sorted((e1, e2) -> {
                    var d1 = LocalDateTime.of(e1.getEventDate(), e1.getBegin());
                    var d2 = LocalDateTime.of(e2.getEventDate(), e2.getBegin());
                    return d2.compareTo(d1);
                })
                .collect(Collectors.toList());
    }

    public void deleteByRef(@NotNull String ref){
        repository.deleteByRef(ref);
    }

}
