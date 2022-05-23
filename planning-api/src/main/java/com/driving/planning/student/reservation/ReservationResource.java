package com.driving.planning.student.reservation;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.student.StudentService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/api/v1/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Student", description = "Student endpoint")
public class ReservationResource {

    private final StudentService studentService;

    private final Logger logger;

    @Inject
    public ReservationResource(StudentService studentService, Logger logger) {
        this.studentService = studentService;
        this.logger = logger;
    }

    @Operation(description = "Reserve a period", operationId = "addReservation")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @POST
    @Path("/{id}/reservations")
    public void add(@PathParam("id") String studentId, @Valid Reservation reservation){
        logger.debugf("Add reservation to student %s", studentId);
        var student = studentService.get(studentId)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Student not found"));
        student.addReservation(reservation);
        studentService.update(student);
    }

    @Operation(description = "Delete a reservation", operationId = "deleteReservation")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @DELETE
    @Path("/{id}/reservations")
    public void delete(@PathParam("id") String studentId, @Valid Reservation reservation){
        logger.debugf("Remove reservation to student %s", studentId);
        var student = studentService.get(studentId)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Student not found"));
        student.removeReservation(reservation);
        studentService.update(student);
    }

}
