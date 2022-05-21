package com.driving.planning.event;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.config.database.Tenant;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.dto.EventDto;
import com.driving.planning.monitor.MonitorService;
import com.driving.planning.school.SchoolService;
import com.driving.planning.student.StudentService;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.TransactionBody;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Traced
@ApplicationScoped
public class EventService {

    private final EventMapper mapper;

    private final EventRepository repository;

    private final MonitorService monitorService;

    private final StudentService studentService;

    private final Logger logger;

    private final MongoClient mongoClient;

    private final SchoolService schoolService;

    private final Tenant tenant;

    @Inject
    public EventService(EventMapper mapper,
                        EventRepository repository,
                        MonitorService monitorService,
                        MongoClient mongoClient,
                        StudentService studentService,
                        SchoolService schoolService,
                        Tenant tenant,
                        Logger logger) {
        this.mapper = mapper;
        this.repository = repository;
        this.monitorService = monitorService;
        this.studentService = studentService;
        this.mongoClient = mongoClient;
        this.logger = logger;
        this.schoolService = schoolService;
        this.tenant = tenant;
    }

    public void add(@Valid final EventDto dto){
        logger.debugf("Add event %s", dto);
        if (dto.getType() == EventType.MONITOR && monitorService.get(dto.getRelatedUserId()).isEmpty()){
            throw new PlanningException(Response.Status.NOT_FOUND, String.format("Monitor with id %s do not exist", dto.getRelatedUserId()));
        }
        if (dto.getType() == EventType.STUDENT && studentService.get(dto.getRelatedUserId()).isEmpty()){
            throw new PlanningException(Response.Status.NOT_FOUND, String.format("Student with id %s do not exist", dto.getRelatedUserId()));
        }
        if (schoolService.isSchoolClosed(tenant.getName(), LocalDateTime.of(dto.getEventDate(), dto.getBegin())) ||
                schoolService.isSchoolClosed(tenant.getName(), LocalDateTime.of(dto.getEventDate(), dto.getEnd()))){
            throw new PlanningException(Response.Status.BAD_REQUEST, "School is not open at that time");
        }
        var txOption = TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.LOCAL)
                .writeConcern(WriteConcern.MAJORITY)
                .build();
        TransactionBody<String> txBody = () -> {
            if (!isPLaceAvailable(dto)){
                throw new PlanningException(Response.Status.BAD_REQUEST, "Place not found for the event");
            }
            var event = mapper.toEntity(dto);
            repository.insert(event);
            return "Done";
        };
        try (var session = mongoClient.startSession()) {
            session.withTransaction(txBody, txOption);
        } catch (PlanningException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PlanningException(Response.Status.INTERNAL_SERVER_ERROR, "Unable to register an event", ex);
        }
    }

    public boolean isPLaceAvailable(final EventDto eventDto){
        long nbEvent = repository.listByDate(eventDto.getEventDate())
                .stream()
                .filter(event ->
                                between(event.getBegin(), eventDto.getBegin(), event.getEnd())
                                        || between(event.getBegin(), eventDto.getEnd(), event.getEnd())
                        ).count();
        long nbPlace = monitorService.list()
                .stream().filter(dto -> dto.getWorkDays().stream()
                        .map(h -> h.getDay().getDayOfWeek())
                        .anyMatch(dow -> dow == eventDto.getEventDate().getDayOfWeek()))
                .count();
        return nbPlace >= nbEvent + 1;
    }

    public boolean hasEvent(@NotBlank String userId, @NotNull LocalDate begin, @NotNull LocalDate end){
        return repository.listByUserId(userId)
                .stream()
                .anyMatch(event -> {
                    var value = event.getEventDate();
                    return (begin.isBefore(value) || begin.equals(value)) && (value.isBefore(end) || value.equals(end));
                });
    }

    public List<EventDto> list(){
        return repository.list()
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

    private boolean between(LocalTime begin, LocalTime value, LocalTime end){
        return (begin.isBefore(value) || begin.equals(value)) && value.isBefore(end);
    }
}
