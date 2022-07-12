package com.driving.planning.monitor.absence;

import com.driving.planning.common.exception.BadRequestException;
import com.driving.planning.common.exception.InternalErrorException;
import com.driving.planning.common.exception.NotFoundException;
import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.event.EventService;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.dto.EventDto;
import com.driving.planning.monitor.MonitorService;
import com.driving.planning.monitor.dto.MonitorAbsenceDto;
import com.mongodb.client.MongoClient;
import com.mongodb.client.TransactionBody;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static com.driving.planning.common.Utils.between;
import static com.driving.planning.common.Utils.transactionOptions;

@ApplicationScoped
public class AbsenceService {

    private final EventService eventService;

    private final MongoClient mongoClient;

    private final MonitorService monitorService;

    private final Logger logger;

    @Inject
    public AbsenceService(EventService eventService, MongoClient mongoClient,
                          MonitorService monitorService,
                          Logger logger) {
        this.eventService = eventService;
        this.mongoClient = mongoClient;
        this.monitorService = monitorService;
        this.logger = logger;
    }

    public void removeAbsent(@NotNull MonitorAbsenceDto monitor, @NotNull String ref){
        logger.debugf("Remove absent %s", ref);
        var txnOptions = transactionOptions();
        var txBody = new RemoveEventTransaction(monitor, ref);
        try (var session = mongoClient.startSession()) {
            session.withTransaction(txBody, txnOptions);
        } catch (PlanningException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalErrorException("Unable to remove absence", ex);
        }
    }

    public void addAbsent(@NotNull MonitorAbsenceDto monitor, @Valid AbsenceRequest request) {
        logger.debugf("Add absent for monitor %s", monitor);
        if (hasAbsence(monitor, request)) {
            throw new BadRequestException("Monitor already have and event at that time");
        }
        var txnOptions = transactionOptions();
        var txBody = new AddEventTransaction(request, monitor);
        try (var session = mongoClient.startSession()) {
            session.withTransaction(txBody, txnOptions);
        } catch (PlanningException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalErrorException("Unable to register absent", ex);
        }
    }

    private boolean hasAbsence(@NotNull MonitorAbsenceDto monitor, @Valid AbsenceRequest request) {
        logger.debugf("Check if monitor %s has event", monitor);
        return monitorService.get(monitor.getId())
                .orElseThrow(() -> new NotFoundException("Monitor not found"))
                .getAbsences()
                .stream()
                .anyMatch(absence ->
                        between(absence.getStart(), request.getStart(), absence.getEnd()) ||
                                between(absence.getStart(), request.getEnd(), absence.getEnd()));
    }

    private class RemoveEventTransaction implements TransactionBody<String> {

        private final MonitorAbsenceDto monitorAbsenceDto;

        private final String ref;

        public RemoveEventTransaction(MonitorAbsenceDto monitorAbsenceDto, String ref) {
            this.monitorAbsenceDto = monitorAbsenceDto;
            this.ref = ref;
        }

        @Override
        public String execute() {
            monitorAbsenceDto.getAbsences().removeIf(a -> a.getReference().equalsIgnoreCase(ref));
            monitorService.updateMonitorWithAbsence(monitorAbsenceDto);
            eventService.deleteByRef(ref);
            return "Done";
        }
    }

    private class AddEventTransaction implements TransactionBody<String> {

        private final AbsenceRequest request;

        private final MonitorAbsenceDto monitor;

        public AddEventTransaction(AbsenceRequest request, MonitorAbsenceDto monitor) {
            this.request = request;
            this.monitor = monitor;
        }

        @Override
        public String execute() {
            var d = request.getStart();
            var ref = UUID.randomUUID().toString();
            for (; d.isBefore(request.getEnd()) || d.equals(request.getEnd()); d = d.plusDays(1)) {
                var wd = getWorkDay(monitor, d);
                if (wd.isEmpty()) {
                    continue;
                }
                var event = toEvent(wd.get());
                event.setRelatedUserId(monitor.getId());
                event.setEventDate(d);
                event.setReference(ref);
                eventService.add(event);
            }
            var absent = new Absence();
            absent.setStart(request.getStart());
            absent.setEnd(request.getEnd());
            absent.setReference(ref);
            monitor.getAbsences().add(absent);
            monitorService.updateMonitorWithAbsence(monitor);
            return "Done";
        }

        private Optional<Hourly> getWorkDay(MonitorAbsenceDto monitorAbsenceDto, LocalDate date) {
            return monitorAbsenceDto.getWorkDays()
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
