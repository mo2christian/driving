package com.driving.planning.student;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.List;

@RegisterForReflection
public class StudentResponse implements Serializable {

    public static final long serialVersionUID = 1L;

    private List<StudentDto> students;

    public List<StudentDto> getStudents() {
        return students;
    }

    public void setStudents(List<StudentDto> students) {
        this.students = students;
    }
}
