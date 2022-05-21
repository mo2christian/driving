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
    public void add(@PathParam("id") String monitorId, @Valid Absent absent){
        var monitor = monitorService.get(monitorId)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Monitor not found"));
        if (eventService.hasEvent(monitorId, absent.getStart(), absent.getEnd())){
            throw new PlanningException(Response.Status.BAD_REQUEST, "Monitor already have and event at that time");
        }
        var school = schoolService.get(tenant.getName())
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "School not found"));
        TransactionOptions txnOptions = TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.LOCAL)
                .writeConcern(WriteConcern.MAJORITY)
                .build();
        TransactionBody<String> txBody = () -> {
                var d = absent.getStart();
                for (;d.isBefore(absent.getEnd()) || d.equals(absent.getEnd()); d = d.plusDays(1)){
                    var wd = getWorkDay(school, d);
                    if (wd.isEmpty()){
                        continue;
                    }
                    var event = toEvent(wd.get());
                    event.setRelatedUserId(monitorId);
                    event.setEventDate(d);
                    eventService.add(event);
                }
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
    public void remove(@PathParam("id") String monitorId, @Valid Absent absent){
        var monitor = monitorService.get(monitorId)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Monitor not found"));
        monitorService.update(monitor);
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
