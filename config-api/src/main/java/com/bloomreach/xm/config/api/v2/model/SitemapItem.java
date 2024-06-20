/*
 *  Copyright 2024 Bloomreach
 */
package com.bloomreach.xm.config.api.v2.model;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public class SitemapItem {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "the name (url path element) of the sitemap item. Each sitemap item name must be unique between its siblings.")
    /**
     * the name (url path element) of the sitemap item. Each sitemap item name must be unique between its siblings.
     **/
    private String name = null;

    @Schema(description = "the name of the (default) page definition for rendering a url matching this sitemap item")
    /**
     * the name of the (default) page definition for rendering a url matching this sitemap item
     **/
    private String page = null;

    @Schema(description = "default page (header) title")
    /**
     * default page (header) title
     **/
    private String pageTitle = null;

    @Schema(description = "the (base) content path, relative to the channel (base) content path to use for rendering this sitemap item page, or any of its children")
    /**
     * the (base) content path, relative to the channel (base) content path to use for rendering this sitemap item page, or any of its children
     **/
    private String relativeContentPath = null;

    @Schema(description = "a map of string parameters (names/values) for rendering this sitemap item page")
    /**
     * a map of string parameters (names/values) for rendering this sitemap item page
     **/
    private Map<String, String> parameters = null;

    @Schema(description = "a more specialized page (name) mapping based on the matched document its type")
    /**
     * a more specialized page (name) mapping based on the matched document its type
     **/
    private Map<String, String> doctypePages = null;

    @Schema(description = "a list of child sitemap items.")
    /**
     * a list of child sitemap items.
     **/
    private List<SitemapItem> items = null;

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private static String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    /**
     * the name (url path element) of the sitemap item. Each sitemap item name must be unique between its siblings.
     *
     * @return name
     **/
    @JsonProperty("name")
    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SitemapItem name(String name) {
        this.name = name;
        return this;
    }

    /**
     * the name of the (default) page definition for rendering a url matching this sitemap item
     *
     * @return page
     **/
    @JsonProperty("page")
    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public SitemapItem page(String page) {
        this.page = page;
        return this;
    }

    /**
     * default page (header) title
     *
     * @return pageTitle
     **/
    @JsonProperty("pageTitle")
    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public SitemapItem pageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }

    /**
     * the (base) content path, relative to the channel (base) content path to use for rendering this sitemap item page,
     * or any of its children
     *
     * @return relativeContentPath
     **/
    @JsonProperty("relativeContentPath")
    public String getRelativeContentPath() {
        return relativeContentPath;
    }

    public void setRelativeContentPath(String relativeContentPath) {
        this.relativeContentPath = relativeContentPath;
    }

    public SitemapItem relativeContentPath(String relativeContentPath) {
        this.relativeContentPath = relativeContentPath;
        return this;
    }

    /**
     * a map of string parameters (names/values) for rendering this sitemap item page
     *
     * @return parameters
     **/
    @JsonProperty("parameters")
    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public SitemapItem parameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public SitemapItem putParametersItem(String key, String parametersItem) {
        this.parameters.put(key, parametersItem);
        return this;
    }

    /**
     * a more specialized page (name) mapping based on the matched document its type
     *
     * @return doctypePages
     **/
    @JsonProperty("doctypePages")
    public Map<String, String> getDoctypePages() {
        return doctypePages;
    }

    public void setDoctypePages(Map<String, String> doctypePages) {
        this.doctypePages = doctypePages;
    }

    public SitemapItem doctypePages(Map<String, String> doctypePages) {
        this.doctypePages = doctypePages;
        return this;
    }

    public SitemapItem putDoctypePagesItem(String key, String doctypePagesItem) {
        this.doctypePages.put(key, doctypePagesItem);
        return this;
    }

    /**
     * a list of child sitemap items.
     *
     * @return items
     **/
    @JsonProperty("items")
    public List<SitemapItem> getItems() {
        return items;
    }

    public void setItems(List<SitemapItem> items) {
        this.items = items;
    }

    public SitemapItem items(List<SitemapItem> items) {
        this.items = items;
        return this;
    }

    public SitemapItem addItemsItem(SitemapItem itemsItem) {
        this.items.add(itemsItem);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SitemapItem {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    page: ").append(toIndentedString(page)).append("\n");
        sb.append("    pageTitle: ").append(toIndentedString(pageTitle)).append("\n");
        sb.append("    relativeContentPath: ").append(toIndentedString(relativeContentPath)).append("\n");
        sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
        sb.append("    doctypePages: ").append(toIndentedString(doctypePages)).append("\n");
        sb.append("    items: ").append(toIndentedString(items)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
