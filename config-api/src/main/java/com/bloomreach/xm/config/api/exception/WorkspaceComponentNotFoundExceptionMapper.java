/*
 *  Copyright 2024 Bloomreach
 */
package com.bloomreach.xm.config.api.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WorkspaceComponentNotFoundExceptionMapper implements ExceptionMapper<WorkspaceComponentNotFoundException> {
    @Override
    public Response toResponse(WorkspaceComponentNotFoundException ex) {
        final ExceptionModel exceptionModel = new ExceptionModel(ex.getLocalizedMessage(), Response.Status.NOT_FOUND.getStatusCode());
        return Response.status(Response.Status.NOT_FOUND).entity(exceptionModel).build();
    }
}