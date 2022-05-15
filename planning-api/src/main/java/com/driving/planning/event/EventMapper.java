package com.driving.planning.event;

import com.driving.planning.event.domain.Event;
import com.driving.planning.event.dto.EventDto;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", imports = {ObjectId.class})
public interface EventMapper {

    @Mapping(target = "id", expression = "java( dto.getId() != null ? new ObjectId(dto.getId()) : null )")
    Event toEntity(EventDto dto);

    @Mapping(target = "id", expression = "java( entity.getId().toString() )")
    EventDto toDto(Event entity);

}
