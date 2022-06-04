package com.driving.planning.student;

import com.driving.planning.student.dto.StudentDto;
import com.driving.planning.student.dto.StudentResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/api/v1/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Student", description = "Student endpoint")
public class StudentResource {

    private final StudentService studentService;

    private final Logger logger;

    @Inject
    public StudentResource(StudentService studentService, Logger logger) {
        this.studentService = studentService;
        this.logger = logger;
    }

    @Operation(description = "List students", operationId = "getStudents")
    @APIResponse(responseCode = "200", description = "List of students",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "StudentResponse", implementation = StudentResponse.class)
            ))
    @APIResponse(responseCode = "400", description = "Tenant Header not found")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @GET
    public StudentResponse list(){
        logger.debug("List users");
        var response = new StudentResponse();
        response.setStudents(studentService.list());
        return response;
    }

    @Operation(description = "Add student", operationId = "addStudent")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @RequestBody(name = "Student", description = "Student to add",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "StudentDto", implementation = StudentDto.class)
            )
    )
    @APIResponse(responseCode = "400", description = "Tenant Header not found")
    @APIResponse(responseCode = "204", description = "Student created")
    @POST
    public void addStudent(@Valid StudentDto studentDto){
        logger.debugf("add student %s", studentDto);
        studentDto.setId(null);
        studentService.add(studentDto);
    }

    @Operation(description = "Update student", operationId = "updateStudent")
    @RequestBody(name = "Student", description = "Student informations",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "StudentDto", implementation = StudentDto.class)
            )
    )
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @Parameter(in = ParameterIn.PATH, name = "id", required = true)
    @APIResponse(responseCode = "400", description = "Tenant Header not found")
    @APIResponse(responseCode = "204", description = "Student created")
    @Path("{id}")
    @POST
    public void updateStudent(@PathParam(value = "id") String id, @Valid StudentDto studentDto){
        logger.debugf("update user %s", id);
        studentDto.setId(id);
        studentService.update(studentDto);
    }

    @Operation(description = "Delete student", operationId = "deleteStudent")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "400", description = "Tenant Header not found")
    @APIResponse(responseCode = "204", description = "Student deleted")
    @Path("{id}")
    @DELETE
    public void delete(@PathParam("id") String id){
        logger.debugf("Delete student %s", id);
        studentService.delete(id);
    }

}
