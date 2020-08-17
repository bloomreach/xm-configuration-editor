package com.bloomreach.xm.config.api.exception;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InternalServerErrorExceptionMapper implements ExceptionMapper<InternalServerErrorException> {
    @Override
    public Response toResponse(InternalServerErrorException ex) {
        ExceptionModel exceptionModel = new ExceptionModel(ex.getLocalizedMessage(), Response.Status.NOT_FOUND.getStatusCode());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(exceptionModel).build();
    }
}