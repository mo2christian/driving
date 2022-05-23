package com.driving.planning.account;

import com.driving.planning.account.dto.AccountDto;
import com.driving.planning.account.dto.AccountResponse;
import com.driving.planning.common.Text;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@ApplicationScoped
@Path("/api/v1/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Account", description = "Account endpoint")
public class AccountResource {

    @Inject
    AccountService accountService;

    @Operation(description = "List accounts", operationId="getAccounts")
    @APIResponse(responseCode = "200", description = "List of admin",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "AdminResponse", implementation = AccountResponse.class)
            ))
    @APIResponse(responseCode = "400", description = "Tenant Header not found")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @GET
    public AccountResponse getAdmins(){
        return new AccountResponse(accountService.list()
                .stream()
                .map(dto -> {
                    dto.setPassword("XXXXXXX");
                    return dto;
                })
                .collect(Collectors.toList())
        );
    }

    @Operation(description = "Check account status", operationId = "isValidAccount")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @RequestBody(name = "account", description = "Account to check",
        content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema( implementation = AccountDto.class))
    )
    @APIResponse(responseCode = "200", description = "Compte valide",
        content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(name = "response", implementation = Text.class))
    )
    @APIResponse(responseCode = "400", description = "Invalid account",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "response", implementation = Text.class))
    )
    @Path("check")
    @POST
    public Response checkAccount(@Valid AccountDto accountDto){
        var text = new Text();
        if (accountService.isValidAccount(accountDto)){
            text.setMessage("Valid account");
            return Response.ok(text).build();
        }
        text.setMessage("Invalid username/password");
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(text)
                .build();
    }

}
