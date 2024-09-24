/*
 *  Copyright 2024 Bloomreach
 */
package com.bloomreach.xm.config.api.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class PageLockedExceptionMapper implements ExceptionMapper<PageLockedException> {
    @Override
    public Response toResponse(PageLockedException ex) {
        final ExceptionModel exceptionModel = new ExceptionModel(ex.getLocalizedMessage(), Response.Status.NOT_FOUND.getStatusCode());
        return Response.status(Response.Status.FORBIDDEN).entity(exceptionModel).build();
    }
}