package com.driving.planning.school.agency;

import com.driving.planning.client.model.SchoolDto;
import com.driving.planning.school.common.form.WorkDayMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {WorkDayMapper.class})
public interface SchoolMapper {

    @Mapping(target = "id", source = "pseudo")
    @Mapping(target = "path", source = "address.path")
    @Mapping(target = "zipCode", source = "address.postalCode")
    @Mapping(target = "town", source = "address.town")
    SchoolForm dtoToForm(SchoolDto dto);

    @Mapping(target = "pseudo", source = "id")
    @Mapping(target = "address.path", source = "path")
    @Mapping(target = "address.town", source = "town")
    @Mapping(target = "address.postalCode", source = "zipCode")
    SchoolDto formToDto(SchoolForm form);

}
