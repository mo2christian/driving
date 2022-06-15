package com.driving.planning.student.reservation;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.event.EventService;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.dto.EventDto;
import com.driving.planning.student.StudentService;
import com.driving.planning.student.dto.StudentDto;
import com.driving.planning.student.dto.StudentReservationDto;
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
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Traced
@ApplicationScoped
public class ReservationService {

    private final EventService eventService;

    private final StudentService studentService;

    private final MongoClient mongoClient;

    private final Logger logger;

    @Inject
    public ReservationService(EventService eventService,
                              StudentService studentService,
                              MongoClient mongoClient,
                              Logger logger) {
        this.eventService = eventService;
        this.studentService = studentService;
        this.mongoClient = mongoClient;
        this.logger = logger;
    }

    public void removeReservation(@NotNull StudentReservationDto student, @NotNull String ref){
        logger.debugf("remove reservation %s", ref);
        student.getReservations().removeIf(r -> ref.equals(r.getReference()));
        studentService.updateStudentWithReservation(student);
        eventService.deleteByRef(ref);
    }

    public String addReservation(@NotNull StudentReservationDto student, @Valid ReservationRequest request){
        logger.debugf("add reservation to student %s", student.getEmail());
        var txnOptions = TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.LOCAL)
                .writeConcern(WriteConcern.MAJORITY)
                .build();

        try(var session = mongoClient.startSession()){
            return session.withTransaction(new AddEventTransaction(student, request), txnOptions);
        } catch (
        PlanningException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PlanningException(Response.Status.INTERNAL_SERVER_ERROR, "Unable to register reservation", ex);
        }
    }

    private class AddEventTransaction implements TransactionBody<String> {

        private final StudentReservationDto student;

        private final ReservationRequest request;

        public AddEventTransaction(StudentReservationDto student, ReservationRequest request) {
            this.student = student;
            this.request = request;
        }

        @Override
        public String execute() {
            var ref = UUID.randomUUID().toString();
            var event = toEvent(student, request);
            event.setReference(ref);
            eventService.add(event);

            var reservation = toReservation(request);
            reservation.setReference(ref);
            student.addReservation(reservation);
            studentService.updateStudentWithReservation(student);
            return ref;
        }

        private EventDto toEvent(StudentDto student, ReservationRequest request){
            var event = new EventDto();
            event.setEventDate(request.getDate());
            event.setBegin(request.getBegin());
            event.setEnd(request.getEnd());
            event.setType(EventType.STUDENT);
            event.setRelatedUserId(student.getId());
            return event;
        }

        private Reservation toReservation(ReservationRequest request){
            var reservation = new Reservation();
            reservation.setDate(request.getDate());
            reservation.setBegin(request.getBegin());
            reservation.setEnd(request.getEnd());
            return reservation;
        }

    }
}
