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

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/api/v1/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Student", description = "Student endpoint")
public interface StudentEndpoint {

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
    StudentResponse list();

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
    void addStudent(@Valid StudentDto studentDto);

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
    void updateStudent(@PathParam(value = "id") String id, @Valid StudentDto studentDto);

    @Operation(description = "Delete student", operationId = "deleteStudent")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @APIResponse(responseCode = "400", description = "Tenant Header not found")
    @APIResponse(responseCode = "204", description = "Student deleted")
    @Path("{id}")
    @DELETE
    void delete(@PathParam("id") String id);

    @Operation(description = "Get student", operationId = "getStudent")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "id", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Path("{id}")
    @APIResponse(responseCode = "400", description = "Tenant Header not found")
    @APIResponse(responseCode = "200", description = "Return student")
    @APIResponse(responseCode = "404", description = "Student not found")
    @GET
    StudentDto get(@PathParam("id") String id);

}
