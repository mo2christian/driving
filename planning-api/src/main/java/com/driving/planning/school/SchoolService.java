package com.driving.planning.school;

import com.driving.planning.common.exception.NotFoundException;
import com.driving.planning.config.database.Tenant;
import com.driving.planning.school.domain.School;
import com.driving.planning.school.dto.SchoolDto;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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

    private final Tenant tenant;

    @Inject
    public SchoolService(SchoolRepository repository,
                         SchoolMapper schoolMapper,
                         Tenant tenant) {
        this.repository = repository;
        this.schoolMapper = schoolMapper;
        this.tenant = tenant;
    }

    public List<SchoolDto> list(){
        return repository.listAll()
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
            throw new NotFoundException(SCHOOL_NOT_FOUND);
        }
        repository.update(schoolMapper.toEntity(schoolDto));
    }

    public Optional<SchoolDto> get(@NotNull String pseudo){
        var school = repository.findByPseudo(pseudo);
        return school.isEmpty() ? Optional.empty() : Optional.of(schoolMapper.toDto(school.get()));
    }

    public void delete(@NotNull String pseudo){
        var school = repository.findByPseudo(pseudo)
                .orElseThrow(() -> new NotFoundException(SCHOOL_NOT_FOUND));
        repository.delete(school.getPseudo());
        repository.deleteDatabase(school.getPseudo());
    }

    public void createSchool(@Valid SchoolDto dto){
        var school = schoolMapper.toEntity(dto);
        repository.persist(school);
    }

    public boolean isSchoolClosed(@NotNull final LocalDateTime dateTime){
        return !isSchoolOpened(tenant.getName(), dateTime);
    }

    public boolean isSchoolClosed(@NotNull String pseudo, @NotNull final LocalDateTime dateTime){
        return !isSchoolOpened(pseudo, dateTime);
    }

    public boolean isSchoolOpened(@NotNull String pseudo, @NotNull final LocalDateTime dateTime){
        var school = get(pseudo)
                .orElseThrow(() -> new NotFoundException(SCHOOL_NOT_FOUND));
        var time = dateTime.toLocalTime();
        return school.getWorkDays()
                .stream()
                .anyMatch(wd -> wd.getDay().getDayOfWeek() == dateTime.getDayOfWeek()
                        && (wd.getBegin().isBefore(time) || wd.getBegin().equals(time))
                        && (wd.getEnd().isAfter(time) || wd.getEnd().equals(time)));
    }

}
