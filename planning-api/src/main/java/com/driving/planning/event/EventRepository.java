package com.driving.planning.event;

import com.driving.planning.event.domain.Event;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

@Traced
@ApplicationScoped
public class EventRepository implements PanacheMongoRepository<Event> {

    private final Logger logger;

    @Inject
    public EventRepository(Logger logger) {
        this.logger = logger;
    }

    public List<Event> listByDate(LocalDate date){
        logger.debugf("find by date %s", date);
        return list("event_date", date);
    }

    public List<Event> listByUserId(String userId){
        logger.debugf("find by user ID %s", userId);
        return list("user_id", userId);
    }

    public void deleteByRef(String ref){
        logger.debugf("Delete event by ref: %s", ref);
        delete("ref", ref);
    }
}
