package com.driving.planning.common.exception;

import javax.ws.rs.core.Response;

public class PlanningException extends RuntimeException {

    private final Response.Status status;

    public PlanningException(Response.Status status, String message) {
        super(message);
        this.status = status;
    }

    public PlanningException(Response.Status status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }
}
