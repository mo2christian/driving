package com.driving.planning.student.reservation;

import com.driving.planning.common.ResponseId;
import com.driving.planning.common.exception.NotFoundException;
import com.driving.planning.student.StudentService;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped

public class ReservationResource implements ReservationEndpoint{

    private final StudentService studentService;

    private final ReservationService reservationService;

    private final Logger logger;

    @Inject
    public ReservationResource(StudentService studentService,
                               ReservationService reservationService,
                               Logger logger) {
        this.studentService = studentService;
        this.reservationService = reservationService;
        this.logger = logger;
    }

    public ResponseId add(String studentId, ReservationRequest reservation){
        logger.debugf("Add reservation to student %s", studentId);
        var student = studentService.get(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));
        var ref = reservationService.addReservation(student, reservation);
        return new ResponseId(ref);
    }

    public void delete(String studentId, String ref){
        logger.debugf("Remove reservation to student %s", studentId);
        var student = studentService.get(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));
        reservationService.removeReservation(student, ref);
    }

}
