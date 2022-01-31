package com.driving.planning.monitor;

import com.driving.planning.monitor.dto.MonitorDto;
import com.driving.planning.monitor.dto.MonitorResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/v1/monitors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Monitor", description = "Monitor endpoint")
public interface MonitorEndpoint {

    @Operation(description = "List monitors")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(
            responseCode = "200",
            description = "List of monitors",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "MonitorResponse", implementation = MonitorResponse.class))
    )
    @GET
    MonitorResponse list();

    @Operation(description = "Add monitor")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @RequestBody(name = "Monitor", content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(name = "Monitor", implementation = MonitorDto.class)
    ))
    @APIResponse(responseCode = "204", description = "Insert success")
    @APIResponse(responseCode = "400", description = "Bad parameters")
    @POST
    void add(@Valid MonitorDto monitorDto);

    @Operation(description = "Modify monitor")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true)
    @APIResponse(responseCode = "200", description = "Change done")
    @APIResponse(responseCode = "404", description = "Monitor not found")
    @Path("{id}")
    @GET
    MonitorDto get(@PathParam("id") String id);

    @Operation(description = "Modify monitor")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true)
    @RequestBody(name = "Moniteur", content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(name = "Monitor", implementation = MonitorDto.class)
    ))
    @APIResponse(responseCode = "204", description = "Change done")
    @APIResponse(responseCode = "400", description = "Bad parameters")
    @APIResponse(responseCode = "404", description = "Monitor not found")
    @Path("{id}")
    @POST
    void update(@PathParam("id") String id, @Valid MonitorDto dto);

    @Operation(description = "Delete monitor")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true)
    @APIResponse(responseCode = "204", description = "Delete success")
    @APIResponse(responseCode = "404", description = "Monitor not found")
    @Path("{id}")
    @DELETE
    void delete(@PathParam("id") String id);

}
