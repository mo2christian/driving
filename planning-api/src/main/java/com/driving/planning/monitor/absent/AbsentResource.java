package com.driving.planning.monitor.absent;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.monitor.MonitorService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/v1/monitors")
@Tag(name = "Monitor", description = "Monitor endpoint")
public class AbsentResource {

    private final MonitorService monitorService;

    @Inject
    public AbsentResource(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @Operation(description = "Add absent")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @POST
    @Path("/{id}/absents")
    public void add(@PathParam("id") String monitorId, @Valid Absent absent){
        var monitor = monitorService.get(monitorId)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Monitor not found"));
        monitor.getAbsents().add(absent);
        monitorService.update(monitor);
    }

    @Operation(description = "Delete absent")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @DELETE
    @Path("/{id}/absents")
    public void remove(@PathParam("id") String monitorId, @Valid Absent absent){
        var monitor = monitorService.get(monitorId)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Monitor not found"));
        monitor.getAbsents().remove(absent);
        monitorService.update(monitor);
    }

}
