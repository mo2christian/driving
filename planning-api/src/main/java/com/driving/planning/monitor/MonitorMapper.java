package com.driving.planning.monitor;

import com.driving.planning.monitor.domain.Monitor;
import com.driving.planning.monitor.dto.MonitorDto;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", imports = {ObjectId.class})
public interface MonitorMapper {

    @Mapping(target = "id", expression = "java( dto.getId() != null ? new ObjectId(dto.getId()) : null )")
    Monitor toEntity(MonitorDto dto);

    @Mapping(target = "id", expression = "java( entity.getId().toString() )")
    MonitorDto toDto(Monitor entity);

}
