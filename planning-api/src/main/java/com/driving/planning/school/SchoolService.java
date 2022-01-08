package com.driving.planning.school;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.school.domain.School;
import com.driving.planning.school.dto.SchoolDto;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Traced
@ApplicationScoped
public class SchoolService {

    private final SchoolRepository repository;

    private final SchoolMapper schoolMapper;

    @Inject
    public SchoolService(SchoolRepository repository,
                         SchoolMapper schoolMapper) {
        this.repository = repository;
        this.schoolMapper = schoolMapper;
    }

    public List<SchoolDto> list(){
        return repository.list()
                .stream()
                .map(schoolMapper::toDto)
                .collect(Collectors.toList());
    }

    public boolean isNameUsed(@NotNull String name, String... excludePseudos){
        Optional<School> schoolOptional = repository.findByName(name);
        if (schoolOptional.isEmpty()){
            return false;
        }
        if (excludePseudos.length == 0){
            return true;
        }
        School school = schoolOptional.orElseThrow();
        return !excludePseudos[0].equals(school.getPseudo());
    }

    public void update(SchoolDto schoolDto){
        var school = repository.findByPseudo(schoolDto.getPseudo());
        if (school.isEmpty()){
            throw new PlanningException(Response.Status.NOT_FOUND, "School not found");
        }
        repository.update(schoolMapper.toEntity(schoolDto));
    }

    public Optional<SchoolDto> get(@NotNull String pseudo){
        var school = repository.findByPseudo(pseudo);
        return school.isEmpty() ? Optional.empty() : Optional.of(schoolMapper.toDto(school.get()));
    }

    public void delete(@NotNull String pseudo){
        var school = repository.findByPseudo(pseudo)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "School not found"));
        repository.delete(school.getPseudo());
        repository.deleteDatabase(school.getPseudo());
    }

    public void createSchool(@Valid SchoolDto dto){
        var school = schoolMapper.toEntity(dto);
        repository.createSchool(school);
    }

}
