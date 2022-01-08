package com.driving.planning;

import com.driving.planning.account.dto.AccountDto;
import com.driving.planning.common.hourly.Day;
import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.monitor.MonitorDto;
import com.driving.planning.monitor.absent.Absent;
import com.driving.planning.school.dto.AddressDto;
import com.driving.planning.school.dto.SchoolDto;
import com.driving.planning.student.StudentDto;
import com.driving.planning.student.reservation.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;

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
        schoolDto.setWorkDays(Collections.singleton(hourly));
        return schoolDto;
    }

    public static StudentDto student(){
        var student = new StudentDto();
        student.setEmail("toto@toto.com");
        student.setPhoneNumber("147852369");
        student.setFirstName("first");
        student.setLastName("last");
        var reservation = new Reservation();
        reservation.setDate(LocalDate.now().plusDays(3));
        reservation.setBegin(LocalTime.now());
        reservation.setEnd(LocalTime.now().plusHours(1));
        student.setReservations(Collections.singleton(reservation));
        return student;
    }

    public static MonitorDto monitor(){
        var monitor = new MonitorDto();
        monitor.setFirstName("firstname");
        monitor.setLastName("lastname");
        monitor.setPhoneNumber("748596322");
        var hourly = new Hourly();
        hourly.setDay(Day.FRIDAY);
        hourly.setBegin(LocalTime.now());
        hourly.setEnd(LocalTime.now().plusHours(5));
        monitor.getWorkDays().add(hourly);
        var absent = new Absent();
        absent.setStart(LocalDateTime.now());
        absent.setEnd(LocalDateTime.now().plusDays(3));
        absent.setMotif("motif");
        monitor.getAbsents().add(absent);
        return monitor;
    }

}
