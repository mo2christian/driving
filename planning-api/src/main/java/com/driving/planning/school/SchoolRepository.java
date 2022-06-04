package com.driving.planning.school;

import com.driving.planning.school.domain.School;
import com.mongodb.client.MongoClient;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Traced
@ApplicationScoped
public class SchoolRepository implements PanacheMongoRepository<School> {

    private final Logger logger;

    private final MongoClient mongoClient;

    @Inject
    public SchoolRepository(Logger logger, MongoClient mongoClient) {
        this.logger = logger;
        this.mongoClient = mongoClient;
    }

    public Optional<School> findByName(@NotNull String name){
        logger.debug("Find school by name");
        return find("name", name).firstResultOptional();
    }

    public Optional<School> findByPseudo(@NotNull String pseudo){
        logger.debug("Find school by pseudo");
        return find("_id", pseudo).firstResultOptional();
    }

    public void delete(@NotNull String pseudo){
        logger.debugf("Delete school %s", pseudo);
        delete("_id", pseudo);
    }

    public void deleteDatabase(@NotNull String schema){
        logger.warnf("Suppression de la base de donnée %s", schema);
        mongoClient.getDatabase(schema).drop();
    }
}
