package com.driving.planning.student;

import com.driving.planning.student.domain.Student;
import com.driving.planning.student.dto.StudentDto;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", imports = {ObjectId.class})
public interface StudentMapper {

    @Mapping(target = "id", expression = "java( dto.getId() != null ? new ObjectId(dto.getId()) : null )")
    Student toEntity(StudentDto dto);

    @Mapping(target = "id", expression = "java( entity.getId().toString() )")
    StudentDto toDto(Student entity);

}
