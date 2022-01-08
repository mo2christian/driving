package com.driving.planning.calendar;

import com.driving.planning.common.exception.PlanningException;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.ArrayList;

@Path("/api/v1/calendar")
@Tag(name = "Calendar", description = "Calendar endpoint")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CalendarResource {

    private final CalendarService calendarService;

    @Inject
    public CalendarResource(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @Operation(description = "Generate calendar")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.QUERY, name = "start", schema = @Schema(implementation = LocalDate.class))
    @Parameter(in = ParameterIn.QUERY, name = "end", schema = @Schema(implementation = LocalDate.class))
    @APIResponse(
        responseCode = "200",
        description = "Le calendrier de la période",
        content = @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(name = "AdminResponse", implementation = Calendar.class)
                ))
    @GET
    public Calendar generateCalendar(@QueryParam("start") @NotNull LocalDate start,
                                     @QueryParam("end") @NotNull LocalDate end){
        if (start.isAfter(end)){
            throw new PlanningException(Response.Status.BAD_REQUEST, "Invalid date");
        }
        var dayCalendars = new ArrayList<DayCalendar>();
        var date = LocalDate.of(start.getYear(), start.getMonth(), start.getDayOfMonth());
        while (date.isBefore(end) || date.isEqual(end)){
            dayCalendars.add(calendarService.get(start));
            date = date.plusDays(1);
        }
        var calendar = new Calendar();
        calendar.setStart(start);
        calendar.setEnd(end);
        calendar.setDayCalendars(dayCalendars);
        return calendar;
    }
}
