package com.driving.planning.event;

import com.driving.planning.common.exception.BadRequestException;
import com.driving.planning.event.dto.EventDto;
import com.driving.planning.event.dto.EventResponse;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class EventResource implements EventEndpoint{

    private final EventService eventService;

    private final Logger logger;

    @Inject
    public EventResource(EventService eventService, Logger logger) {
        this.eventService = eventService;
        this.logger = logger;
    }

    @Override
    public Response add(@Valid EventDto dto) {
        logger.debugf("Add event %s", dto);
        if (dto.getBegin().isAfter(dto.getEnd()) || dto.getBegin().equals(dto.getEnd())){
            throw new BadRequestException("Begin time must be before end time");
        }
        eventService.add(dto);
        return Response.status(Response.Status.CREATED)
                .build();
    }

    @Override
    public EventResponse list() {
        logger.debugf("List events");
        var response = new EventResponse();
        response.setEvents(eventService.list());
        return response;
    }
}
