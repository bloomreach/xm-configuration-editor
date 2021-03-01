package com.bloomreach.xm.config.api.v2.rest;

import java.util.List;

import javax.jcr.RepositoryException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.v2.model.SitemapItem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("/")
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
    public List<SitemapItem> getChannelSitemap(@PathParam("channel_id") String channelId) throws ChannelNotFoundException, RepositoryException;


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
    Response putChannelSitemap(@PathParam("channel_id") String channelId, @Valid List<SitemapItem> body);
}
