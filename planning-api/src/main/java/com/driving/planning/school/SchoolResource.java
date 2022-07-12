package com.driving.planning.school;

import com.driving.planning.account.AccountService;
import com.driving.planning.common.exception.BadRequestException;
import com.driving.planning.common.exception.NotFoundException;
import com.driving.planning.school.dto.SchoolDto;
import com.driving.planning.school.dto.SchoolRequest;
import com.driving.planning.school.dto.SchoolResponse;
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
import java.time.LocalDateTime;

@ApplicationScoped
@Path("/api/v1/schools")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Driving school", description = "Endpoint to manipulate school")
public class SchoolResource {

    private final Logger logger;

    private final SchoolService schoolService;

    private final AccountService accountService;

    @Inject
    public SchoolResource(Logger logger, SchoolService schoolService, AccountService accountService) {
        this.logger = logger;
        this.schoolService = schoolService;
        this.accountService = accountService;
    }

    @Operation(description = "List driving school", operationId = "getSchools")
    @APIResponse(responseCode = "200", description = "List of driving school",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "SchoolResponse", implementation = SchoolResponse.class)
            ))
    @GET
    public SchoolResponse getSchools(){
        logger.info("Get schools");
        return new SchoolResponse(schoolService.list());
    }

    @Operation(description = "Create driving school", operationId = "addSchool")
    @RequestBody(name = "school", description = "School to create",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "SchoolRequest", implementation = SchoolRequest.class)
            ))
    @APIResponse(responseCode = "204", description = "School created")
    @POST
    public void createSchool(@Valid SchoolRequest request){
        if (schoolService.isNameUsed(request.getSchool().getName())){
            throw new BadRequestException("Name already used");
        }
        var schoolDto = request.getSchool();
        String pseudo = generatePseudo(schoolDto.getName());
        schoolDto.setPseudo(pseudo);
        schoolService.createSchool(schoolDto);
        accountService.createAccount(pseudo, request.getAccount());
    }

    @Operation(description = "Update driving school", operationId = "updateSchool")
    @RequestBody(name = "school", description = "School to update",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "SchoolDto", implementation = SchoolDto.class)
            ))
    @APIResponse(responseCode = "204", description = "School updated")
    @Path("{id}")
    @POST
    public void updateSchool(@PathParam("id") String pseudo, @Valid SchoolDto schoolDto){
        if (schoolService.get(pseudo).isEmpty()){
            throw new NotFoundException("School not found");
        }
        if (schoolService.isNameUsed(schoolDto.getName(), pseudo)){
            throw new BadRequestException("Name already used");
        }
        schoolDto.setPseudo(pseudo);
        schoolService.update(schoolDto);
    }

    @Operation(description = "Delete driving school", operationId = "deleteSchool")
    @Parameter(name = "pseudo", in = ParameterIn.PATH, required = true,
            schema = @Schema(required = true, implementation = String.class))
    @APIResponse(responseCode = "201", description = "School deleted")
    @Path("/{pseudo}")
    @DELETE
    public void deleteSchool(@PathParam("pseudo") String pseudo){
        SchoolDto dto = schoolService.get(pseudo)
                .orElseThrow(() -> new NotFoundException("Pseudo not found"));
        schoolService.delete(dto.getPseudo());
    }

    @Operation(description = "Get specific driving school", operationId = "getSchoolByID")
    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
            schema = @Schema(required = true, implementation = String.class))
    @APIResponse(responseCode = "200", description = "School returned")
    @APIResponse(responseCode = "404", description = "School not found")
    @Path("/{id}")
    @GET
    public SchoolDto getSchool(@PathParam("id") String id){
        return schoolService.get(id)
                .orElseThrow(() -> new NotFoundException("School not found"));
    }

    private String generatePseudo(String name){
        var builder = new StringBuilder();
        for (var i = 0; i < name.length() && i < 12; i++){
            if (Character.isLetter(name.charAt(i))){
                builder.append(name.charAt(i));
            }
        }
        return builder.append(LocalDateTime.now().getNano())
                .toString()
                .toLowerCase();
    }

}
