package com.driving.planning.school;

import com.driving.planning.school.domain.School;
import com.driving.planning.school.dto.SchoolDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface SchoolMapper {

    SchoolDto toDto(School entity);

    School toEntity(SchoolDto dto);

}
