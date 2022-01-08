package com.driving.planning.student.otp;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Traced
@RequestScoped
public class OTPRepository {

    private final MongoDatabase mongoDatabase;

    private final Logger logger;

    @Inject
    public OTPRepository(MongoDatabase mongoDatabase, Logger logger) {
        this.mongoDatabase = mongoDatabase;
        this.logger = logger;
    }

    public void insert(OTP otp){
        logger.debug("Insert OTP");
        mongoDatabase.getCollection(OTP.COLLECTION_NAME, OTP.class)
                .insertOne(otp);
    }

    public List<OTP> findByStudent(String studentId){
        var iterator = mongoDatabase.getCollection(OTP.COLLECTION_NAME, OTP.class)
                .find(Filters.eq("student_id", studentId))
                .iterator();
        var list = new ArrayList<OTP>();
        while (iterator.hasNext()){
            list.add(iterator.next());
        }
        return list;
    }

}
