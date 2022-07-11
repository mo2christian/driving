package com.driving.planning.common.exception;

import javax.ws.rs.core.Response;

public class NotFoundException extends PlanningException{

    public NotFoundException(String message) {
        super(Response.Status.NOT_FOUND, message);
    }
}
