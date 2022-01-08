package com.driving.planning.school;

import com.driving.planning.school.domain.School;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.mongodb.client.model.Filters.eq;

@Traced
@RequestScoped
public class SchoolRepository {

    private final Logger logger;

    private final MongoDatabase mongoDatabase;

    private final MongoClient mongoClient;

    @Inject
    public SchoolRepository(Logger logger, MongoClient mongoClient) {
        this.logger = logger;
        this.mongoDatabase = mongoClient.getDatabase("base");
        this.mongoClient = mongoClient;
    }

    public List<School> list(){
        logger.debug("List schools from database");
        var cursor = mongoDatabase.getCollection(School.COLLECTION_NAME, School.class)
                .find()
                .cursor();
        var list = new ArrayList<School>();
        while (cursor.hasNext()){
            list.add(cursor.next());
        }
        return list;
    }

    public void update(School school) {
        logger.debugf("Update school %s", school.getPseudo());
        mongoDatabase.getCollection(School.COLLECTION_NAME, School.class)
                .updateOne(Filters.eq("_id", school.getPseudo()),
                        Updates.combine(
                                Updates.set("name", school.getName()),
                                Updates.set("phone_number", school.getPhoneNumber()),
                                Updates.set("address", school.getAddress()),
                                Updates.set("work_days", school.getWorkDays())
                        ));
    }

    public Optional<School> findByName(@NotNull String name){
        logger.debug("Find school by name");
        var school = mongoDatabase.getCollection(School.COLLECTION_NAME, School.class)
                .find(eq("name", name))
                .first();
        return Optional.ofNullable(school);
    }

    public Optional<School> findByPseudo(@NotNull String pseudo){
        logger.debug("Find school by pseudo");
        var school = mongoDatabase.getCollection(School.COLLECTION_NAME, School.class)
                .find(eq("_id", pseudo))
                .first();
        return Optional.ofNullable(school);
    }

    public void delete(@NotNull String pseudo){
        logger.debugf("Delete school %s", pseudo);
        mongoDatabase.getCollection(School.COLLECTION_NAME, School.class)
                .deleteOne(eq("_id", pseudo));
    }

    public void createSchool(@Valid School school){
        logger.debugf("create school %s", school.getPseudo());
        mongoDatabase.getCollection(School.COLLECTION_NAME, School.class)
                .insertOne(school);
    }

    public void deleteDatabase(@NotNull String schema){
        mongoClient.getDatabase(schema).drop();
    }
}
