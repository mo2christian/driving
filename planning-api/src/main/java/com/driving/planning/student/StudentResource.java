package com.driving.planning.student;

import com.driving.planning.common.exception.NotFoundException;
import com.driving.planning.student.dto.StudentDto;
import com.driving.planning.student.dto.StudentReservationDto;
import com.driving.planning.student.dto.StudentResponse;
import org.jboss.logging.Logger;

import javax.inject.Inject;

public class StudentResource implements StudentEndpoint {

    private final StudentService studentService;

    private final Logger logger;

    @Inject
    public StudentResource(StudentService studentService, Logger logger) {
        this.studentService = studentService;
        this.logger = logger;
    }

    public StudentResponse list(){
        logger.debug("List users");
        var response = new StudentResponse();
        response.setStudents(studentService.list());
        return response;
    }

    public void addStudent(StudentDto studentDto){
        logger.debugf("add student %s", studentDto);
        studentDto.setId(null);
        studentService.add(studentDto);
    }

    public void updateStudent(String id, StudentDto studentDto){
        logger.debugf("update user %s", id);
        studentDto.setId(id);
        studentService.updateStudent(studentDto);
    }

    public void delete(String id){
        logger.debugf("Delete student %s", id);
        studentService.delete(id);
    }

    @Override
    public StudentReservationDto get(String id) {
        return studentService.get(id)
                .orElseThrow(() -> new NotFoundException("Student not found"));
    }

}
