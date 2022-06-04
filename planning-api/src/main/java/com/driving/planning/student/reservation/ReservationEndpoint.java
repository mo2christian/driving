package com.driving.planning.student.reservation;

import com.driving.planning.common.ResponseId;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/v1/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Student", description = "Student endpoint")
public interface ReservationEndpoint {

    @Operation(description = "Reserve a period", operationId = "addReservation")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @POST
    @Path("/{id}/reservations")
    ResponseId add(@PathParam("id") String studentId, @Valid ReservationRequest reservation);

    @Operation(description = "Delete a reservation", operationId = "deleteReservation")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "ref", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @DELETE
    @Path("/{id}/reservations/{ref}")
    void delete(@PathParam("id") String studentId, @PathParam("ref") String ref);

}
