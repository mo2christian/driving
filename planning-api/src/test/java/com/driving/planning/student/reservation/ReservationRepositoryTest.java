package com.driving.planning.student.reservation;

import com.driving.planning.MongodbTestResource;
import com.driving.planning.student.Student;
import com.driving.planning.student.StudentRepository;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

@QuarkusTest
@QuarkusTestResource(value = MongodbTestResource.class, restrictToAnnotatedClass = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReservationRepositoryTest {

    @Inject
    ReservationRepository reservationRepository;

    @Inject
    StudentRepository studentRepository;

    @BeforeEach
    void init(){
        var student = new Student();
        student.setEmail("toto@toto.com");
        student.setFirstName("fist");
        student.setLastName("last");
        student.setPhoneNumber("147852369");

        var reservation = new Reservation();
        reservation.setDate(LocalDate.now());
        reservation.setBegin(LocalTime.of(10, 30));
        reservation.setEnd(LocalTime.of(11, 30));
        student.setReservations(Collections.singleton(reservation));

        studentRepository.create(student);
    }

    @Test
    void find(){
        var reservations = reservationRepository.findByDate(LocalDate.now());
        Assertions.assertThat(reservations)
                .hasSize(1)
                .element(0)
                .extracting(Reservation::getBegin, Reservation::getEnd)
                .containsExactly(LocalTime.of(10, 30), LocalTime.of(11, 30));

        reservations = reservationRepository.findByDate(LocalDate.now().plusDays(1));
        Assertions.assertThat(reservations).isEmpty();
    }

}
