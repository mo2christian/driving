package com.driving.planning.event;

import com.driving.planning.event.dto.EventDto;
import com.driving.planning.event.dto.EventResponse;
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
import javax.ws.rs.core.Response;

@Path("/api/v1/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Event", description = "Event endpoint")
public interface EventEndpoint {

    @Operation(description = "Add event")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @RequestBody(name = "Event", content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(name = "Event", implementation = EventDto.class)
    ))
    @APIResponse(responseCode = "201", description = "Insert success")
    @APIResponse(responseCode = "400", description = "Bad parameters")
    @APIResponse(responseCode = "404", description = "Monitor/Student not found")
    @POST
    Response add(@Valid EventDto dto);

    @Operation(description = "Add event")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "200", description = "Insert success")
    @APIResponse(
            responseCode = "200",
            description = "List of monitors",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "MonitorResponse", implementation = EventResponse.class))
    )
    @GET
    EventResponse list();

}
