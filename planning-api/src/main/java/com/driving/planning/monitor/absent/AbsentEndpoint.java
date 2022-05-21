package com.driving.planning.monitor.absent;

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
public interface AbsentEndpoint {

    @Operation(description = "Add absent")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @POST
    @Path("/{id}/absents")
    void add(@PathParam("id") String monitorId, @Valid AbsentRequest absent);

    @Operation(description = "Delete absent")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @DELETE
    @Path("/{id}/absents")
    void remove(@PathParam("id") String monitorId, @Valid AbsentRequest absent);

}
