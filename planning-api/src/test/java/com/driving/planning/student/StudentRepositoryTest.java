package com.driving.planning.student;

import com.driving.planning.MongodbTestResource;
import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.config.database.DatabaseResolverInitializer;
import com.driving.planning.config.database.Tenant;
import com.driving.planning.student.domain.Student;
import com.driving.planning.student.reservation.Reservation;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

@QuarkusTestResource(MongodbTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
class StudentRepositoryTest {

    @Inject
    StudentRepository repository;

    static Student student;

    static ObjectId id;

    @BeforeAll
    static void init(){
        var tenant = new Tenant("base");
        DatabaseResolverInitializer.alterAnnotation(Student.class, tenant);

        student = new Student();
        student.setEmail("toto@toto.com");
        student.setFirstName("fist");
        student.setLastName("last");
        student.setPhoneNumber("147852369");

        var reservation = new Reservation();
        reservation.setDate(LocalDate.now());
        reservation.setBegin(LocalTime.now());
        reservation.setEnd(LocalTime.now().plusHours(1));
        student.setReservations(Collections.singletonList(reservation));
    }

    @Test
    @Order(1)
    void insert(){
        repository.persist(student);
        Assertions.assertThat(repository.listAll()).hasSize(1)
                .element(0)
                .extracting(Student::getEmail, Student::getFirstName, Student::getLastName, Student::getPhoneNumber, Student::getReservations)
                .containsExactly(student.getEmail(), student.getFirstName(), student.getLastName(), student.getPhoneNumber(), student.getReservations());
        id = repository.listAll().get(0).getId();
    }

    @Test
    @Order(2)
    void findById(){
        var s = repository.findById(id.toString());
        Assertions.assertThat(s)
                .isNotEmpty()
                .get()
                .extracting(Student::getEmail, Student::getFirstName, Student::getLastName, Student::getPhoneNumber, Student::getReservations)
                .containsExactly(student.getEmail(), student.getFirstName(), student.getLastName(), student.getPhoneNumber(), student.getReservations());
    }

    @Test
    @Order(2)
    void findByNumber(){
        var s = repository.findByNumber(student.getPhoneNumber());
        Assertions.assertThat(s)
                .isNotEmpty()
                .get()
                .extracting(Student::getEmail, Student::getFirstName, Student::getLastName, Student::getPhoneNumber, Student::getReservations)
                .containsExactly(student.getEmail(), student.getFirstName(), student.getLastName(), student.getPhoneNumber(), student.getReservations());
    }

    @Test
    @Order(2)
    void findByIdInvalidID(){
        Assertions.assertThatExceptionOfType(PlanningException.class)
                .isThrownBy(() -> repository.findById("toto"));
    }

    @Test
    @Order(3)
    void update(){
        student.setPhoneNumber("369852147");
        student.setEmail("titi@titi.com");
        student.setId(id);

        var reservation = new Reservation();
        reservation.setDate(LocalDate.now().plusDays(3));
        reservation.setBegin(LocalTime.now());
        reservation.setEnd(LocalTime.now().plusHours(1));
        student.setReservations(Collections.singletonList(reservation));
        repository.update(student);
        Assertions.assertThat(repository.listAll()).hasSize(1)
                .element(0)
                .extracting(Student::getEmail, Student::getFirstName, Student::getLastName, Student::getPhoneNumber, Student::getReservations)
                .containsExactly(student.getEmail(), student.getFirstName(), student.getLastName(), student.getPhoneNumber(), student.getReservations());
    }

    @Test
    @Order(4)
    void delete(){
        repository.deleteById(id);
        Assertions.assertThat(repository.listAll()).isEmpty();
    }

}
