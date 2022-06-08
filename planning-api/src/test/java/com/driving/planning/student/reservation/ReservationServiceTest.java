package com.driving.planning.student.reservation;

import com.driving.planning.Generator;
import com.driving.planning.event.EventService;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.dto.EventDto;
import com.driving.planning.student.StudentService;
import com.driving.planning.student.dto.StudentDto;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest
class ReservationServiceTest {

    @InjectMock
    EventService eventService;

    @InjectMock
    StudentService studentService;

    @Inject
    ReservationService reservationService;

    @Test
    void removeReservation(){
        var reservation = new Reservation();
        reservation.setReference("ref");
        var student = Generator.student();
        student.addReservation(reservation);
        reservationService.removeReservation(student, "ref");

        ArgumentCaptor<StudentDto> studentCaptor = ArgumentCaptor.forClass(StudentDto.class);
        verify(studentService, times(1)).update(studentCaptor.capture());
        Assertions.assertThat(student.getReservations()
                .stream()
                .filter(r -> "ref".equals(r.getReference()))
                .findFirst())
                        .isEmpty();
        verify(eventService, times(1)).deleteByRef("ref");
    }

    @Test
    void addReservation(){
        var request = new ReservationRequest();
        request.setDate(LocalDate.now());
        request.setBegin(LocalTime.of(10,0));
        request.setEnd(LocalTime.of(11,0));
        var student = Generator.student();
        reservationService.addReservation(student, request);

        ArgumentCaptor<EventDto> eventCaptor = ArgumentCaptor.forClass(EventDto.class);
        verify(eventService, times(1)).add(eventCaptor.capture());
        var event = eventCaptor.getValue();
        Assertions.assertThat(event)
                .isNotNull()
                .extracting(EventDto::getEventDate, EventDto::getBegin, EventDto::getEnd, EventDto::getRelatedUserId, EventDto::getType)
                .containsExactly(event.getEventDate(), event.getBegin(), event.getEnd(), student.getId(), EventType.STUDENT);

        ArgumentCaptor<StudentDto> studentCaptor = ArgumentCaptor.forClass(StudentDto.class);
        verify(studentService, times(1)).update(studentCaptor.capture());
        Assertions.assertThat(student.getReservations()
                        .stream()
                        .anyMatch(r -> event.getReference().equals(r.getReference())))
                .isTrue();
    }
}