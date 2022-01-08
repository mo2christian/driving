package com.driving.planning.common.exception;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.driving.planning.common.Text;

import org.jboss.logging.Logger;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Inject
    Logger logger;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        var text = new Text();
        text.setMessage("Bad parameters");
        for (var violation : exception.getConstraintViolations()){
            var detail = String.format("%s : %s", violation.getPropertyPath(), violation.getMessage());
            text.addDetail(detail);
        }
        logger.warn(text.getDetails());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(text)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}