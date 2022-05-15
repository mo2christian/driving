package com.driving.planning.event.dto;

import java.util.List;

public class EventResponse{

    private List<EventDto> events;

    public List<EventDto> getEvents() {
        return events;
    }

    public void setEvents(List<EventDto> events) {
        this.events = events;
    }
}
