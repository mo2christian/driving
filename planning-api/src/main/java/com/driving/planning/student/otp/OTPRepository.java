package com.driving.planning.student.otp;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Traced
@ApplicationScoped
public class OTPRepository implements PanacheMongoRepository<OTP> {

    private final Logger logger;

    @Inject
    public OTPRepository(Logger logger) {
        this.logger = logger;
    }

    public List<OTP> findByStudent(String studentId){
        logger.debugf("find OTP for user %s", studentId);
        return list("student_id", studentId);
    }

}
