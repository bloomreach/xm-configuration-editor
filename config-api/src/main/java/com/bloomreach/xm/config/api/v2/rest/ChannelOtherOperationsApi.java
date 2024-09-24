package com.bloomreach.xm.config.api.v2.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.exception.UnauthorizedException;

@Path("/v2")
public interface ChannelOtherOperationsApi {


    @Path("/acl")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    Response hasPermission(@Context final HttpServletRequest request) throws InternalServerErrorException;

    @Path("/channels/{channel_id}/components")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    Response getAllComponents(@Context final HttpServletRequest request, @PathParam("channel_id") String channelId) throws ChannelNotFoundException, UnauthorizedException;
}
