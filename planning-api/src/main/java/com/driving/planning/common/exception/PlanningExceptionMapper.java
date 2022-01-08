package com.driving.planning.common.exception;

import com.driving.planning.common.Text;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class PlanningExceptionMapper implements ExceptionMapper<PlanningException> {

    @Inject
    Logger logger;

    @Override
    public Response toResponse(PlanningException exception) {
        logger.warn("Bad parameters", exception);
        var text = new Text();
        text.setMessage(exception.getMessage());
        return Response.status(exception.getStatus())
                .entity(text)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}
