package com.driving.planning.student;

import com.driving.planning.student.domain.Student;
import com.driving.planning.student.dto.StudentDto;
import com.driving.planning.student.dto.StudentReservationDto;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", imports = {ObjectId.class})
public interface StudentMapper {

    @Mapping(target = "id", expression = "java( dto.getId() != null ? new ObjectId(dto.getId()) : null )")
    Student toStudent(StudentDto dto);

    @Mapping(target = "id", expression = "java( dto.getId() != null ? new ObjectId(dto.getId()) : null )")
    Student toStudentWithReservation(StudentReservationDto dto);

    @Mapping(target = "id", expression = "java( student.getId().toString() )")
    StudentDto toDto(Student student);

    @Mapping(target = "id", expression = "java( student.getId().toString() )")
    StudentReservationDto toStudentReservationDto(Student student);

}
