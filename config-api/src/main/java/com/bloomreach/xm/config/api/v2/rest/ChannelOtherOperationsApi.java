package com.bloomreach.xm.config.api.v2.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
