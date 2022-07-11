package com.driving.planning.common.exception;

import javax.ws.rs.core.Response;

public class BadRequestException extends PlanningException {

    public BadRequestException(String message) {
        super(Response.Status.BAD_REQUEST, message);
    }

}
