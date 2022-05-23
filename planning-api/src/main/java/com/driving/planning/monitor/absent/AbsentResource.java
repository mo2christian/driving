package com.driving.planning.monitor.absent;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.config.database.Tenant;
import com.driving.planning.event.EventService;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.dto.EventDto;
import com.driving.planning.monitor.MonitorService;
import com.driving.planning.school.SchoolService;
import com.driving.planning.school.dto.SchoolDto;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.TransactionBody;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AbsentResource implements AbsentEndpoint {

    private final MonitorService monitorService;

    private final EventService eventService;

    private final SchoolService schoolService;

    private final MongoClient mongoClient;

    private final Tenant tenant;

    @Inject
    public AbsentResource(MonitorService monitorService, EventService eventService,
                          MongoClient mongoClient,
                          SchoolService schoolService,  Tenant tenant) {
        this.monitorService = monitorService;
        this.eventService = eventService;
        this.schoolService = schoolService;
        this.mongoClient = mongoClient;
        this.tenant = tenant;
    }

    @Override
    public void add(@PathParam("id") String monitorId, @Valid AbsentRequest request){
        var school = schoolService.get(tenant.getName())
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "School not found"));
        var monitor = monitorService.get(monitorId)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Monitor not found"));
        if (eventService.hasEvent(monitorId, request.getStart(), request.getEnd())){
            throw new PlanningException(Response.Status.BAD_REQUEST, "Monitor already have and event at that time");
        }
        var txnOptions = TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.LOCAL)
                .writeConcern(WriteConcern.MAJORITY)
                .build();
        TransactionBody<String> txBody = () -> {
                var d = request.getStart();
                var ref = UUID.randomUUID().toString();
                for (;d.isBefore(request.getEnd()) || d.equals(request.getEnd()); d = d.plusDays(1)){
                    var wd = getWorkDay(school, d);
                    if (wd.isEmpty()){
                        continue;
                    }
                    var event = toEvent(wd.get());
                    event.setRelatedUserId(monitorId);
                    event.setEventDate(d);
                    event.setReference(ref);
                    eventService.add(event);
                }
                var absent = new Absent();
                absent.setStart(request.getStart());
                absent.setEnd(request.getEnd());
                absent.setReference(ref);
                monitor.getAbsents().add(absent);
                monitorService.update(monitor);
                return "Done";
        };
        try (var session = mongoClient.startSession()) {
            session.withTransaction(txBody, txnOptions);
        }
        catch (PlanningException ex){
            throw ex;
        } catch (Exception ex) {
            throw new PlanningException(Response.Status.INTERNAL_SERVER_ERROR, "Unable to register absent", ex);
        }
        monitorService.update(monitor);
    }

    @Override
    public void remove(@PathParam("id") String monitorId, @PathParam("ref") String ref){
        var monitor = monitorService.get(monitorId)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Monitor not found"));
        monitor.getAbsents().removeIf(a -> a.getReference().equalsIgnoreCase(ref));
        monitorService.update(monitor);
        eventService.deleteByRef(ref);
    }

    private Optional<Hourly> getWorkDay(SchoolDto schoolDto, LocalDate date){
        return schoolDto.getWorkDays()
                .stream()
                .filter(h -> h.getDay().getDayOfWeek() == date.getDayOfWeek())
                .findFirst();
    }

    private EventDto toEvent(Hourly wd){
        var event = new EventDto();
        event.setType(EventType.MONITOR);
        event.setEnd(wd.getEnd());
        event.setBegin(wd.getBegin());
        return event;
    }

}
