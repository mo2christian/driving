package com.driving.planning;

import com.driving.planning.account.dto.AccountDto;
import com.driving.planning.common.hourly.Day;
import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.event.domain.EventType;
import com.driving.planning.event.dto.EventDto;
import com.driving.planning.monitor.absence.Absence;
import com.driving.planning.monitor.dto.MonitorAbsenceDto;
import com.driving.planning.school.dto.AddressDto;
import com.driving.planning.school.dto.SchoolDto;
import com.driving.planning.student.dto.StudentDto;
import com.driving.planning.student.dto.StudentReservationDto;
import com.driving.planning.student.reservation.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;

public class  Generator {

    private Generator(){}

    public static AccountDto account(){
        var accountDto = new AccountDto();
        accountDto.setEmail("test@test.com");
        accountDto.setPassword("test");
        return accountDto;
    }

    public static SchoolDto school(){
        var schoolDto = new SchoolDto();
        schoolDto.setName("test");
        schoolDto.setPhoneNumber("0101010101");
        var addressDto = new AddressDto();
        addressDto.setPath("1 tue de la victoire");
        addressDto.setPostalCode("54100");
        addressDto.setTown("World");
        schoolDto.setAddress(addressDto);
        var hourly = new Hourly();
        hourly.setDay(Day.MONDAY);
        hourly.setBegin(LocalTime.now());
        hourly.setEnd(LocalTime.now().plusHours(5));
        var  h = new HashSet<Hourly>();
        h.add(hourly);
        schoolDto.setWorkDays(h);
        return schoolDto;
    }

    public static StudentDto student(){
        var student = new StudentDto();
        student.setEmail("toto@toto.com");
        student.setPhoneNumber("147852369");
        student.setFirstName("first");
        student.setLastName("last");
        return student;
    }

    public static StudentReservationDto studentReservation(){
        var student = new StudentReservationDto();
        var dto = student();
        student.setEmail(dto.getEmail());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        var reservation = new Reservation();
        reservation.setDate(LocalDate.now().plusDays(3));
        reservation.setBegin(LocalTime.now());
        reservation.setEnd(LocalTime.now().plusHours(1));
        student.addReservation(reservation);
        return student;
    }

    public static MonitorAbsenceDto monitor(){
        var monitor = new MonitorAbsenceDto();
        monitor.setFirstName("firstname");
        monitor.setLastName("lastname");
        monitor.setPhoneNumber("748596322");
        var hourly = new Hourly();
        hourly.setDay(Day.TUESDAY);
        hourly.setBegin(LocalTime.now());
        hourly.setEnd(LocalTime.now().plusHours(5));
        monitor.getWorkDays().add(hourly);
        var absent = new Absence();
        absent.setStart(LocalDate.now());
        absent.setEnd(LocalDate.now().plusDays(3));
        absent.setReference("ref");
        monitor.getAbsences().add(absent);
        return monitor;
    }

    public static EventDto event(){
        var event = new EventDto();
        event.setEventDate(LocalDate.now());
        event.setBegin(LocalTime.now());
        event.setEnd(LocalTime.now().plusHours(1));
        event.setRelatedUserId("613383809b162658348d87cb");
        event.setType(EventType.STUDENT);
        event.setReference("REF");
        return event;
    }

}
