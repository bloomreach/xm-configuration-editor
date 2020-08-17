package com.bloomreach.xm.config.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WorkspaceComponentNotFoundExceptionMapper implements ExceptionMapper<WorkspaceComponentNotFoundException> {
    @Override
    public Response toResponse(WorkspaceComponentNotFoundException ex) {
        ExceptionModel exceptionModel = new ExceptionModel(ex.getLocalizedMessage(), Response.Status.NOT_FOUND.getStatusCode());
        return Response.status(Response.Status.NOT_FOUND).entity(exceptionModel).build();
    }
}