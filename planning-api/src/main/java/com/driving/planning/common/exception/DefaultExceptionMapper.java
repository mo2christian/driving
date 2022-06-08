package com.driving.planning.common.exception;

import com.driving.planning.common.Text;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Exception> {

    @Inject
    Logger logger;

    @Override
    public Response toResponse(Exception exception) {
        logger.error("Unexpected error : %s", exception.getMessage(), exception);
        Text text = new Text();
        text.setMessage("Internal error");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(text)
                .build();
    }
}
