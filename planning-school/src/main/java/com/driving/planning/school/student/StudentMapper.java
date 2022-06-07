package com.driving.planning.school.student;

import com.driving.planning.client.model.StudentDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentDto formToDto(StudentForm  from);

    StudentForm dtoToForm(StudentDto dto);

}
