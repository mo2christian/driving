package com.driving.planning.monitor.absence;

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

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/v1/monitors")
@Tag(name = "Monitor", description = "Monitor endpoint")
public interface AbsenceEndpoint {

    @Operation(description = "Add absent", operationId = "addAbsent")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @POST
    @Path("/{id}/absences")
    ResponseId add(@PathParam("id") String monitorId, @Valid AbsenceRequest absent);

    @Operation(description = "Delete absent", operationId = "deleteAbsent")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "ref", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @DELETE
    @Path("/{id}/absences/{ref}")
    void remove(@PathParam("id") String monitorId, @PathParam("ref") String ref);

}
