package com.driving.planning.monitor;

import com.driving.planning.common.Repository;
import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.monitor.domain.Monitor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Traced
@RequestScoped
public class MonitorRepository implements Repository<Monitor> {

    private final MongoDatabase mongoDatabase;

    private final Logger logger;

    @Inject
    public MonitorRepository(MongoDatabase mongoDatabase, Logger logger) {
        this.mongoDatabase = mongoDatabase;
        this.logger = logger;
    }

    @Override
    public void insert(Monitor monitor) {
        logger.debugf("Insert monitor");
        mongoDatabase.getCollection(Monitor.COLLECTION_NAME, Monitor.class)
                .insertOne(monitor);
    }

    @Override
    public void update(Monitor monitor) {
        logger.debugf("Update monitor %s", monitor.getId());
        mongoDatabase.getCollection(Monitor.COLLECTION_NAME, Monitor.class)
                .updateOne(Filters.eq("_id", monitor.getId()),
                Updates.combine(
                        Updates.set("first_name", monitor.getFirstName()),
                        Updates.set("last_name", monitor.getLastName()),
                        Updates.set("phone_number", monitor.getPhoneNumber()),
                        Updates.set("work_days", monitor.getWorkDays()),
                        Updates.set("absents", monitor.getAbsents())
                ));
    }

    @Override
    public void delete(ObjectId id) {
        logger.debugf("Delete monitor %s", id);
        mongoDatabase.getCollection(Monitor.COLLECTION_NAME, Monitor.class)
                .deleteOne(Filters.eq("_id", id));
    }

    @Override
    public List<Monitor> list() {
        logger.debugf("List monitors");
        var cursor = mongoDatabase.getCollection(Monitor.COLLECTION_NAME, Monitor.class)
                .find()
                .cursor();
        var list = new ArrayList<Monitor>();
        while (cursor.hasNext()){
            list.add(cursor.next());
        }
        return list;
    }

    @Override
    public Optional<Monitor> findById(@NotNull String id) {
        logger.debugf("Find monitor %s", id);
        ObjectId objectId;
        try{
            objectId = new ObjectId(id);
        }
        catch(IllegalArgumentException ex){
            throw new PlanningException(Response.Status.BAD_REQUEST, String.format("Invalid database id %s", id));
        }
        var monitor = mongoDatabase.getCollection(Monitor.COLLECTION_NAME, Monitor.class)
                .find(Filters.eq("_id", objectId))
                .first();
        return Optional.ofNullable(monitor);
    }
}
