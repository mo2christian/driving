package com.driving.planning.event;

import com.driving.planning.common.Repository;
import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.event.domain.Event;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Traced
@RequestScoped
public class EventRepository implements Repository<Event> {

    private final MongoDatabase mongoDatabase;

    private final Logger logger;

    @Inject
    public EventRepository(MongoDatabase mongoDatabase, Logger logger) {
        this.mongoDatabase = mongoDatabase;
        this.logger = logger;
    }

    @Override
    public void insert(Event event) {
        logger.debugf("Insert event");
        mongoDatabase.getCollection(Event.COLLECTION_NAME, Event.class)
                .insertOne(event);
    }

    @Override
    public void update(Event event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ObjectId id) {
        logger.debugf("Delete event %s", id);
        mongoDatabase.getCollection(Event.COLLECTION_NAME, Event.class)
                .deleteOne(Filters.eq("_id", id));
    }

    @Override
    public List<Event> list() {
        logger.debugf("List events");
        var cursor = mongoDatabase.getCollection(Event.COLLECTION_NAME, Event.class)
                .find()
                .cursor();
        var list = new ArrayList<Event>();
        while (cursor.hasNext()){
            list.add(cursor.next());
        }
        return list;
    }

    @Override
    public Optional<Event> findById(String id) {
        logger.debugf("Find event %s", id);
        ObjectId objectId;
        try{
            objectId = new ObjectId(id);
        }
        catch(IllegalArgumentException ex){
            throw new PlanningException(Response.Status.BAD_REQUEST, String.format("Invalid database id %s", id));
        }
        var monitor = mongoDatabase.getCollection(Event.COLLECTION_NAME, Event.class)
                .find(Filters.eq("_id", objectId))
                .first();
        return Optional.ofNullable(monitor);
    }

    public List<Event> listByDate(LocalDate date){
        var cursor = mongoDatabase.getCollection(Event.COLLECTION_NAME, Event.class)
                .find(Filters.eq("event_date", date))
                .iterator();
        var list = new ArrayList<Event>();
        while (cursor.hasNext()){
            list.add(cursor.next());
        }
        return list;
    }
}
