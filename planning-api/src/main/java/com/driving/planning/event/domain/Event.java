package com.driving.planning.event.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalTime;

@RegisterForReflection
public class Event {

    public static final String COLLECTION_NAME = "event";

    @BsonProperty("_id")
    @BsonId
    private ObjectId id;

    @BsonProperty("event_date")
    private LocalDate eventDate;

    @BsonProperty("begin_date")
    private LocalTime begin;

    @BsonProperty("end_date")
    private LocalTime end;

    @BsonProperty("type")
    private EventType type;

    @BsonProperty("user_id")
    private String relatedUserId;

    @BsonProperty("ref")
    private String reference;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public ObjectId getId() {
        return id;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public LocalTime getBegin() {
        return begin;
    }

    public LocalTime getEnd() {
        return end;
    }

    public EventType getType() {
        return type;
    }

    public String getRelatedUserId() {
        return relatedUserId;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public void setBegin(LocalTime begin) {
        this.begin = begin;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public void setRelatedUserId(String relatedUserId) {
        this.relatedUserId = relatedUserId;
    }
}
