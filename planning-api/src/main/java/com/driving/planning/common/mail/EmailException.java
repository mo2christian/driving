package com.driving.planning.common.mail;

import com.driving.planning.common.exception.PlanningException;

import javax.ws.rs.core.Response;

public class EmailException extends PlanningException {

    public EmailException(String message, Throwable throwable) {
        super(Response.Status.INTERNAL_SERVER_ERROR, message, throwable);
    }

}
