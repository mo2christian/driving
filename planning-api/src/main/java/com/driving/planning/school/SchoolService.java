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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Traced
@ApplicationScoped
public class SchoolService {

    private static final String SCHOOL_NOT_FOUND = "School not found";

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
            throw new PlanningException(Response.Status.NOT_FOUND, SCHOOL_NOT_FOUND);
        }
        repository.update(schoolMapper.toEntity(schoolDto));
    }

    public Optional<SchoolDto> get(@NotNull String pseudo){
        var school = repository.findByPseudo(pseudo);
        return school.isEmpty() ? Optional.empty() : Optional.of(schoolMapper.toDto(school.get()));
    }

    public void delete(@NotNull String pseudo){
        var school = repository.findByPseudo(pseudo)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, SCHOOL_NOT_FOUND));
        repository.delete(school.getPseudo());
        repository.deleteDatabase(school.getPseudo());
    }

    public void createSchool(@Valid SchoolDto dto){
        var school = schoolMapper.toEntity(dto);
        repository.createSchool(school);
    }

    public boolean isSchoolClosed(@NotNull String pseudo, @NotNull final LocalDateTime dateTime){
        return !isSchoolOpened(pseudo, dateTime);
    }

    public boolean isSchoolOpened(@NotNull String pseudo, @NotNull final LocalDateTime dateTime){
        var school = get(pseudo)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, SCHOOL_NOT_FOUND));
        var time = dateTime.toLocalTime();
        return school.getWorkDays()
                .stream()
                .anyMatch(wd -> wd.getDay().getDayOfWeek() == dateTime.getDayOfWeek()
                        && (wd.getBegin().isBefore(time) || wd.getBegin().equals(time))
                        && (wd.getEnd().isAfter(time) || wd.getEnd().equals(time)));
    }

}
