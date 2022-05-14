package com.driving.planning.event.dto;

import com.driving.planning.common.DatePattern;
import com.driving.planning.event.domain.EventType;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventDto {

    private String id;

    @NotNull
    @JsonFormat(pattern = DatePattern.DATE)
    private LocalDate eventDate;

    @NotNull
    @JsonFormat(pattern = DatePattern.TIME)
    private LocalTime begin;

    @NotNull
    @JsonFormat(pattern = DatePattern.TIME)
    private LocalTime end;

    @NotNull
    private EventType type;

    private String relatedUserId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getBegin() {
        return begin;
    }

    public void setBegin(LocalTime begin) {
        this.begin = begin;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getRelatedUserId() {
        return relatedUserId;
    }

    public void setRelatedUserId(String relatedUserId) {
        this.relatedUserId = relatedUserId;
    }

    @Override
    public String toString() {
        return "EventDto{" +
                "id='" + id + '\'' +
                ", eventDate=" + eventDate +
                ", begin=" + begin +
                ", end=" + end +
                ", type=" + type +
                ", relatedUserId='" + relatedUserId + '\'' +
                '}';
    }

}
