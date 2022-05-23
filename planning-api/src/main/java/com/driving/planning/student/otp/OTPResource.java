package com.driving.planning.student.otp;

import com.driving.planning.common.constraint.PhoneNumber;
import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.student.StudentService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.Optional;

@ApplicationScoped
@Path("/api/v1/students")
@Tag(name = "Student", description = "Student endpoint")
public class OTPResource {

    private final StudentService studentService;

    private final OTPService otpService;

    private final Logger logger;

    @Inject
    public OTPResource(StudentService studentService, OTPService otpService, Logger logger) {
        this.studentService = studentService;
        this.otpService = otpService;
        this.logger = logger;
    }

    @Operation(description = "Send otp to student", operationId = "sendOTP")
    @APIResponse(responseCode = "204", description = "OTP send")
    @APIResponse(responseCode = "400", description = "Tenant Header not found")
    @APIResponse(responseCode = "404", description = "Email or number not found")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "number", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @POST
    @Path("/{number}/otp/send")
    public void sendOTP(@PathParam("number") @PhoneNumber String phoneNumber, @QueryParam("canal") MethodType methodType){
        logger.infof("Send OTP by %s to %s", methodType, phoneNumber);
        var student = studentService.findByNumber(phoneNumber)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "PhoneNumber not found"));
        switch (methodType){
            case EMAIL:
                if (student.getEmail() == null || student.getEmail().isBlank()){
                    throw new PlanningException(Response.Status.METHOD_NOT_ALLOWED, "Not email available");
                }
                otpService.sendOTPByMail(student);
                break;
            case SMS:
                otpService.sendOTPBySMS(student);
                break;
            default:
                throw new PlanningException(Response.Status.BAD_REQUEST, "Unexpected value: " + methodType);
        }
    }

    @Operation(description = "Verify otp to student", operationId = "verifyOTP")
    @APIResponse(responseCode = "204", description = "OTP send")
    @APIResponse(responseCode = "400", description = "Tenant Header not found")
    @APIResponse(responseCode = "404", description = "Email or number not found")
    @Parameter(in = ParameterIn.HEADER, name = "x-app-tenant", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @Parameter(in = ParameterIn.PATH, name = "number", required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @POST
    @Path("/{number}/otp/verify")
    public void verifyOTP(@PathParam("number") @PhoneNumber String phoneNumber, @QueryParam("otp") String otp){
        logger.infof("Validate OTP %s for user %s", otp, phoneNumber);
        var student = studentService.findByNumber(phoneNumber)
                .orElseThrow(() -> new PlanningException(Response.Status.NOT_FOUND, "PhoneNumber not found"));
        Optional<OTP> otpOptional = otpService.findValidOTP(student.getId())
                .stream()
                .filter(o -> otp.equals(o.getContent()))
                .findFirst();
        if (otpOptional.isEmpty()){
            throw new PlanningException(Response.Status.BAD_REQUEST, "OTP not found or expired");
        }
    }

}
