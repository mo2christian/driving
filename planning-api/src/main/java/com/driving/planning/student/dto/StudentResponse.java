package com.driving.planning.student.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.List;

@RegisterForReflection
public class StudentResponse implements Serializable {

    public static final long serialVersionUID = 1L;

    private List<StudentReservationDto> students;

    public List<StudentReservationDto> getStudents() {
        return students;
    }

    public void setStudents(List<StudentReservationDto> students) {
        this.students = students;
    }
}
