package com.driving.planning.monitor.absent;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.config.database.Tenant;
import com.driving.planning.event.EventService;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.dto.EventDto;
import com.driving.planning.monitor.MonitorService;
import com.driving.planning.monitor.dto.MonitorDto;
import com.driving.planning.school.SchoolService;
import com.driving.planning.school.dto.SchoolDto;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.TransactionBody;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AbsentService {

    private final EventService eventService;

    private final MongoClient mongoClient;

    private final SchoolService schoolService;

    private final MonitorService monitorService;

    private final Logger logger;

    private final Tenant tenant;

    @Inject
    public AbsentService(EventService eventService, MongoClient mongoClient,
                         SchoolService schoolService, MonitorService monitorService,
                         Logger logger, Tenant tenant) {
        this.eventService = eventService;
        this.mongoClient = mongoClient;
        this.schoolService = schoolService;
        this.monitorService = monitorService;
        this.logger = logger;
        this.tenant = tenant;
    }

    public boolean hasAbsent(@NotNull MonitorDto monitor, @Valid AbsentRequest request) {
        logger.debugf("Check in monitor %s has event", monitor);
        return eventService.hasEvent(monitor.getId(), request.getStart(), request.getEnd());
    }

    public void removeAbsent(@NotNull MonitorDto monitor, @NotNull String ref){
        logger.debugf("Remove absent %s", ref);
        monitor.getAbsents().removeIf(a -> a.getReference().equalsIgnoreCase(ref));
        monitorService.update(monitor);
        eventService.deleteByRef(ref);
    }

    public void addAbsent(@NotNull MonitorDto monitor, @Valid AbsentRequest request) {
        logger.debugf("Add absent for monitor %s", monitor);
        if (hasAbsent(monitor, request)) {
            throw new PlanningException(Response.Status.BAD_REQUEST, "Monitor already have and event at that time");
        }
        var txnOptions = TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.LOCAL)
                .writeConcern(WriteConcern.MAJORITY)
                .build();
        var txBody = new AddEventTransaction(request, monitor);
        try (var session = mongoClient.startSession()) {
            session.withTransaction(txBody, txnOptions);
        } catch (PlanningException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PlanningException(Response.Status.INTERNAL_SERVER_ERROR, "Unable to register absent", ex);
        }
    }

    private class AddEventTransaction implements TransactionBody<String> {

        private final AbsentRequest request;

        private final MonitorDto monitor;

        public AddEventTransaction(AbsentRequest request, MonitorDto monitor) {
            this.request = request;
            this.monitor = monitor;
        }

        @Override
        public String execute() {
            var d = request.getStart();
            var ref = UUID.randomUUID().toString();
            var school = schoolService.get(tenant.getName())
                    .orElseThrow();
            for (; d.isBefore(request.getEnd()) || d.equals(request.getEnd()); d = d.plusDays(1)) {
                var wd = getWorkDay(school, d);
                if (wd.isEmpty()) {
                    continue;
                }
                var event = toEvent(wd.get());
                event.setRelatedUserId(monitor.getId());
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
        }

        private Optional<Hourly> getWorkDay(SchoolDto schoolDto, LocalDate date) {
            return schoolDto.getWorkDays()
                    .stream()
                    .filter(h -> h.getDay().getDayOfWeek() == date.getDayOfWeek())
                    .findFirst();
        }

        private EventDto toEvent(Hourly wd) {
            var event = new EventDto();
            event.setType(EventType.MONITOR);
            event.setEnd(wd.getEnd());
            event.setBegin(wd.getBegin());
            return event;
        }
    }
}
