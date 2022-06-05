package com.driving.planning.student;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.student.domain.Student;
import com.driving.planning.student.dto.StudentDto;
import com.driving.planning.student.reservation.Reservation;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@QuarkusTest
class StudentServiceTest {

    @Inject
    StudentService studentService;

    @InjectMock
    StudentRepository studentRepository;

    StudentDto studentDto;

    @BeforeEach
    void init(){
        studentDto = new StudentDto();
        studentDto.setEmail("toto@toto.com");
        studentDto.setFirstName("first");
        studentDto.setLastName("last");
        studentDto.setPhoneNumber("258963254");
        studentDto.setId("60f6ab7f443a1d3e27b6cbaf");
        var reservation = new Reservation();
        reservation.setDate(LocalDate.now().plusDays(3));
        reservation.setBegin(LocalTime.now());
        reservation.setEnd(LocalTime.now().plusHours(1));
        studentDto.setReservations(Collections.singletonList(reservation));
    }

    @Test
    void add(){
        studentService.add(studentDto);
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository, times(1)).persist(studentCaptor.capture());
        assertThat(studentCaptor.getValue()).isNotNull()
                .extracting(Student::getEmail, Student::getFirstName, Student::getLastName, Student::getPhoneNumber, Student::getReservations)
                .containsExactly(studentDto.getEmail(), studentDto.getFirstName(), studentDto.getLastName(), studentDto.getPhoneNumber(), studentDto.getReservations());
    }

    @Test
    void update(){
        when(studentRepository.findById(studentDto.getId())).thenReturn(Optional.of(new Student()));
        studentService.update(studentDto);
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository, times(1)).update(studentCaptor.capture());
        assertThat(studentCaptor.getValue()).isNotNull()
                .extracting(Student::getEmail, Student::getFirstName, Student::getLastName, Student::getPhoneNumber, Student::getReservations)
                .containsExactly(studentDto.getEmail(), studentDto.getFirstName(), studentDto.getLastName(), studentDto.getPhoneNumber(), studentDto.getReservations());
        assertThat(studentCaptor.getValue().getId())
                .isNotNull()
                .asString()
                .isEqualTo(studentDto.getId());
    }

    @Test
    void updateNotFound(){
        when(studentRepository.findById(studentDto.getId())).thenReturn(Optional.empty());
        assertThatExceptionOfType(PlanningException.class)
                .isThrownBy(() -> studentService.update(studentDto));
    }

    @Test
    void deleteNotFound(){
        var id = studentDto.getId();
        when(studentRepository.findById(studentDto.getId())).thenReturn(Optional.empty());
        assertThatExceptionOfType(PlanningException.class)
                .isThrownBy(() -> studentService.delete(id))
                .withMessage("Student not found");
    }

    @Test
    void delete(){
        var student = new Student();
        student.setId(new ObjectId(studentDto.getId()));
        when(studentRepository.findById(studentDto.getId())).thenReturn(Optional.of(student));
        studentService.delete(studentDto.getId());
        verify(studentRepository, times(1)).deleteById(student.getId());
    }

    @Test
    void list(){
        var student = new Student();
        student.setEmail(studentDto.getEmail());
        student.setLastName(studentDto.getLastName());
        student.setFirstName(studentDto.getFirstName());
        student.setPhoneNumber(studentDto.getPhoneNumber());
        student.setId(new ObjectId(studentDto.getId()));
        student.setReservations(studentDto.getReservations());
        when(studentRepository.listAll()).thenReturn(Collections.singletonList(student));
        assertThat(studentService.list()).hasSize(1)
                .element(0)
                .extracting(StudentDto::getEmail, StudentDto::getFirstName, StudentDto::getLastName, StudentDto::getPhoneNumber, StudentDto::getId, StudentDto::getReservations)
                .containsExactly(student.getEmail(), student.getFirstName(), student.getLastName(), student.getPhoneNumber(), student.getId().toString(), student.getReservations());
    }

    @Test
    void get(){
        var student = new Student();
        student.setEmail(studentDto.getEmail());
        student.setLastName(studentDto.getLastName());
        student.setFirstName(studentDto.getFirstName());
        student.setPhoneNumber(studentDto.getPhoneNumber());
        student.setId(new ObjectId(studentDto.getId()));
        when(studentRepository.findById(studentDto.getId())).thenReturn(Optional.of(student));
        assertThat(studentService.get(studentDto.getId())).isNotEmpty()
                .get()
                .extracting(StudentDto::getEmail, StudentDto::getFirstName, StudentDto::getLastName, StudentDto::getPhoneNumber, StudentDto::getId)
                .containsExactly(student.getEmail(), student.getFirstName(), student.getLastName(), student.getPhoneNumber(), student.getId().toString());
    }

    @Test
    void findByNumber(){
        var student = new Student();
        student.setEmail(studentDto.getEmail());
        student.setLastName(studentDto.getLastName());
        student.setFirstName(studentDto.getFirstName());
        student.setPhoneNumber(studentDto.getPhoneNumber());
        student.setId(new ObjectId(studentDto.getId()));
        when(studentRepository.findByNumber(studentDto.getPhoneNumber())).thenReturn(Optional.of(student));
        assertThat(studentService.findByNumber(studentDto.getPhoneNumber())).isNotEmpty()
                .get()
                .extracting(StudentDto::getEmail, StudentDto::getFirstName, StudentDto::getLastName, StudentDto::getPhoneNumber, StudentDto::getId)
                .containsExactly(student.getEmail(), student.getFirstName(), student.getLastName(), student.getPhoneNumber(), student.getId().toString());
    }

    @Test
    void getNotFound(){
        when(studentRepository.findById(studentDto.getId())).thenReturn(Optional.empty());
        assertThat(studentService.get(studentDto.getId())).isEmpty();
    }

}
