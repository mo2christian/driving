package com.driving.planning.student;

import com.driving.planning.common.constraint.PhoneNumber;
import com.driving.planning.common.exception.PlanningException;
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
public class StudentRepository {

    private final MongoDatabase mongoDatabase;

    private final Logger logger;

    @Inject
    public StudentRepository(MongoDatabase mongoDatabase, Logger logger) {
        this.mongoDatabase = mongoDatabase;
        this.logger = logger;
    }

    public void create(@NotNull Student student){
        mongoDatabase.getCollection(Student.COLLECTION_NAME, Student.class)
                .insertOne(student);
    }

    public void update(@NotNull Student student){
        mongoDatabase.getCollection(Student.COLLECTION_NAME, Student.class)
                .updateOne(Filters.eq("_id", student.getId()),
                        Updates.combine(
                                Updates.set("first_name", student.getFirstName()),
                                Updates.set("last_name", student.getLastName()),
                                Updates.set("email", student.getEmail()),
                                Updates.set("phone_number", student.getPhoneNumber()),
                                Updates.set("reservations", student.getReservations())
                        ));
    }

    public void delete(ObjectId id){
        mongoDatabase.getCollection(Student.COLLECTION_NAME, Student.class)
                .deleteOne(Filters.eq("_id", id));
    }

    public Optional<Student> findById(String id){
        ObjectId objectId;
        try{
            objectId = new ObjectId(id);
        }
        catch(IllegalArgumentException ex){
            throw new PlanningException(Response.Status.BAD_REQUEST, String.format("Invalid database id %s", id));
        }
        var student = mongoDatabase.getCollection(Student.COLLECTION_NAME, Student.class)
                .find(Filters.eq("_id", objectId))
                .first();
        return Optional.ofNullable(student);
    }

    public Optional<Student> findByNumber(@PhoneNumber String phoneNumber){
        logger.debugf("Find by number %s", phoneNumber);
        var student = mongoDatabase.getCollection(Student.COLLECTION_NAME, Student.class)
                .find(Filters.eq("phone_number", phoneNumber))
                .first();
        return student == null ? Optional.empty() : Optional.of(student);
    }

    public List<Student> list(){
        logger.debug("List student");
        var cursor = mongoDatabase.getCollection(Student.COLLECTION_NAME, Student.class)
                .find()
                .cursor();
        var list = new ArrayList<Student>();
        while (cursor.hasNext()){
            list.add(cursor.next());
        }
        return list;
    }
}
