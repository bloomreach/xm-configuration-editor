/*
 *  Copyright 2024 Bloomreach
 */
package com.bloomreach.xm.config.api.v2.rest;

import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.exception.PageLockedException;
import com.bloomreach.xm.config.api.exception.UnauthorizedException;
import com.bloomreach.xm.config.api.exception.WorkspaceComponentNotFoundException;
import com.bloomreach.xm.config.api.v2.model.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

@Path("/v2")
public interface ChannelFlexPageOperationsApi {


    /**
     * Get a channel page
     */
    @GET
    @Path("/channels/{channel_id}/flexpage/{page_path:.*}")
    @Produces({"application/json"})
    @Operation(summary = "Get a channel page", tags = {"Channel Current Page Operations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Page.class)))})
    Page getChannelPage(@Context HttpServletRequest request, @PathParam("channel_id") String channelId, @PathParam("page_path") String pagePath) throws ChannelNotFoundException, WorkspaceComponentNotFoundException, UnauthorizedException;


    /**
     * Update a channel page
     */
    @PUT
    @Path("/channels/{channel_id}/flexpage/{page_path:.*}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Create or update a channel page", tags = {"Channel Current Page Operations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OK", content = @Content(schema = @Schema(implementation = Page.class)))})
    Response putChannelPage(@Context HttpServletRequest request, @PathParam("channel_id") String channelId, @PathParam("page_path") String pagePath, Page body) throws UnauthorizedException, ChannelNotFoundException, WorkspaceComponentNotFoundException, PageLockedException;
}
