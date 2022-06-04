package com.driving.planning.monitor;

import com.driving.planning.monitor.domain.Monitor;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Traced
@ApplicationScoped
public class MonitorRepository implements PanacheMongoRepository<Monitor> {

    private final Logger logger;

    @Inject
    public MonitorRepository(Logger logger) {
        this.logger = logger;
    }

    public Optional<Monitor> findById(@NotNull String id) {
        logger.debugf("Find monitor %s", id);
        return findByIdOptional(new ObjectId(id));
    }
}
