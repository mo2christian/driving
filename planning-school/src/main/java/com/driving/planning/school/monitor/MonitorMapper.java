package com.driving.planning.school.monitor;

import com.driving.planning.client.model.MonitorAbsenceDto;
import com.driving.planning.client.model.MonitorDto;
import com.driving.planning.school.common.form.WorkDayMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {WorkDayMapper.class})
public interface MonitorMapper {

    @Mapping(target = "workDays", ignore = true)
    MonitorDto formToDto(MonitorForm form);

    @Mapping(target = "workDays", ignore = true)
    MonitorForm dtoToForm(MonitorAbsenceDto dto);

}
