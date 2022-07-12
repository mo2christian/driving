package com.driving.planning.common.exception;

import javax.ws.rs.core.Response;

public class InternalErrorException extends PlanningException{
    public InternalErrorException(String message) {
        super(Response.Status.INTERNAL_SERVER_ERROR, message);
    }

    public InternalErrorException(String message, Throwable cause) {
        super(Response.Status.INTERNAL_SERVER_ERROR, message, cause);
    }
}
