/*
 *  Copyright 2024 Bloomreach
 */
package com.bloomreach.xm.config.api.v2.rest;

import java.util.List;

import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.v2.model.SitemapItem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/v2")
public interface ChannelSitemapOperationsApi {


    /**
     * Get the channel sitemap
     */
    @GET
    @Path("/channels/{channel_id}/sitemap")
    @Produces({"application/json"})
    @Operation(summary = "Get the channel sitemap", tags = {"Channel Sitemap Operations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SitemapItem.class))))})
    public List<SitemapItem> getChannelSitemap(@PathParam("channel_id") String channelId) throws ChannelNotFoundException;


    /**
     * Create or update a sitemap item
     */
    @PUT
    @Path("/channels/{channel_id}/sitemap")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @Operation(summary = "Update the sitemap ", tags = {"Channel Sitemap Operations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SitemapItem.class))))})
    Response putChannelSitemap(@PathParam("channel_id") String channelId, @Valid List<SitemapItem> body) throws ChannelNotFoundException;
}
