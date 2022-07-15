package com.driving.planning.school.student;

import com.driving.planning.client.model.StudentDto;
import com.driving.planning.client.model.StudentReservationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    StudentDto formToDto(StudentForm  from);

    StudentForm dtoToForm(StudentReservationDto dto);

}
