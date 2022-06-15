package com.driving.planning.student;

import com.driving.planning.common.constraint.PhoneNumber;
import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.student.domain.Student;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Traced
@ApplicationScoped
public class StudentRepository implements PanacheMongoRepository<Student> {

    private final Logger logger;

    @Inject
    public StudentRepository(Logger logger) {
        this.logger = logger;
    }

    public Optional<Student> findById(String id){
        logger.debugf("Find student by Id %s", id);
        ObjectId obj;
        try{
            obj = new ObjectId(id);
        }
        catch(Exception ex){
            throw new PlanningException(Response.Status.BAD_REQUEST, "Bad ID");
        }
        return findByIdOptional(obj);
    }

    public Optional<Student> findByNumber(@PhoneNumber String phoneNumber){
        logger.debugf("Find by number %s", phoneNumber);
        return find("phone_number", phoneNumber).firstResultOptional();
    }
}
