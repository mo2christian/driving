package com.driving.planning.student;

import com.driving.planning.common.exception.PlanningException;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Traced
public class StudentService {

    private final Logger logger;

    private final StudentMapper mapper;

    private final StudentRepository repository;

    @Inject
    public StudentService(Logger logger, StudentMapper mapper, StudentRepository repository) {
        this.logger = logger;
        this.mapper = mapper;
        this.repository = repository;
    }

    public void add(@Valid StudentDto dto){
        logger.debugf("Create student with email %s", dto.getEmail());
        var student = mapper.toEntity(dto);
        repository.persist(student);
    }

    public void update(@Valid StudentDto dto){
        logger.debugf("Update student %s", dto.getId());
        var optionalStudent = repository.findById(dto.getId());
        if (optionalStudent.isEmpty()){
            throw new PlanningException(Response.Status.NOT_FOUND, "Student not found");
        }
        var student = mapper.toEntity(dto);
        repository.update(student);
    }

    public void delete(String id){
        logger.debugf("Delete student %s", id);
        var student = repository.findById(id)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "Student not found"));
        repository.deleteById(student.getId());
    }

    public Optional<StudentDto> get(String id){
        logger.debugf("Get student with %s", id);
        var entity = repository.findById(id)
                .orElse(null);
        if (entity == null){
            return Optional.empty();
        }
        return Optional.of(mapper.toDto(entity));
    }

    public List<StudentDto> list(){
        logger.debug("List students");
        return repository.listAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<StudentDto> findByNumber(String phoneNumber){
        logger.debugf("Find by number %s", phoneNumber);
        var optionalStudent = repository.findByNumber(phoneNumber);
        return optionalStudent.isEmpty() ? Optional.empty() : Optional.of(mapper.toDto(optionalStudent.get()));
    }

}
