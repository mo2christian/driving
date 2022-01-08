package com.driving.planning.monitor;

import com.driving.planning.common.exception.PlanningException;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/api/v1/monitors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Monitor", description = "Monitor endpoint")
public class MonitorResource {

    private final MonitorService service;

    private final Logger logger;

    @Inject
    public MonitorResource(MonitorService service, Logger logger) {
        this.service = service;
        this.logger = logger;
    }

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
    public MonitorResponse list(){
        logger.debug("List monitors");
        var response = new MonitorResponse();
        response.setMonitors(service.list());
        return response;
    }

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
    public void add(@Valid MonitorDto monitorDto){
        logger.debugf("Add monitor %s", monitorDto.getPhoneNumber());
        monitorDto.setId(null);
        service.add(monitorDto);
    }

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
    public void update(@PathParam("id") String id, @Valid MonitorDto dto){
        logger.debugf("Update monitor %s", id);
        if (service.get(id).isEmpty()){
            throw new PlanningException(Response.Status.NOT_FOUND, "Monitor not found");
        }
        dto.setId(id);
        service.update(dto);
    }

    @Operation(description = "Delete monitor")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true)
    @APIResponse(responseCode = "204", description = "Delete success")
    @APIResponse(responseCode = "404", description = "Monitor not found")
    @Path("{id}")
    @DELETE
    public void delete(@PathParam("id") String id){
        logger.debugf("Delete monitor %s", id);
        var monitorDto = service.get(id)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Monitor not found"));
        service.delete(monitorDto.getId());
    }
}
