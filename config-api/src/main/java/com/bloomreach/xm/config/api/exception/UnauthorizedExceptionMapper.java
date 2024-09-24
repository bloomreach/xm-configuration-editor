/*
 *  Copyright 2024 Bloomreach
 */
package com.bloomreach.xm.config.api.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {

    @Override
    public Response toResponse(UnauthorizedException ex) {
        final ExceptionModel exceptionModel = new ExceptionModel(ex.getLocalizedMessage(), Response.Status.FORBIDDEN.getStatusCode());
        return Response.status(Response.Status.FORBIDDEN).entity(exceptionModel).build();
    }

}