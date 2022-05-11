package com.driving.planning.school.common.form;

import com.driving.planning.client.model.Hourly;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkDayMapper {


    @Mapping(target = "begin", source = "hourly.begin",  dateFormat = "HH:mm")
    @Mapping(target = "end", source = "hourly.end", dateFormat = "HH:mm")
    @Mapping(target = "selected", constant = "true")
    WorkDayForm hourlyToForm(Hourly hourly);

    @Mapping(target = "begin", dateFormat = "HH:mm")
    @Mapping(target = "end", dateFormat = "HH:mm")
    Hourly formToHourly(WorkDayForm from);


}
