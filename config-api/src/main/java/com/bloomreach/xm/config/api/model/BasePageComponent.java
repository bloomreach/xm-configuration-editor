package com.bloomreach.xm.config.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class BasePageComponent {

    @Schema(required = true, description = "identifying name of this page/component within its channel (for pages) or within its page (for components)")
    /**
     * identifying name of this page/component within its channel (for pages) or within its page (for components)
     **/
    private String name = null;

    public void setName(String name) {
        this.name = name;
    }

    @Schema(description = "default display label for this page/component")
    /**
     * default display label for this page/component
     **/
    private String label = null;

    @Schema(description = "description for this page/component")
    /**
     * description for this page/component
     **/
    private String description = null;

    @Schema(description = "a map of string parameters (names/values) for this page/component")
    /**
     * a map of string parameters (names/values) for this page/component
     **/
    private Map<String, String> parameters = null;

    /**
     * identifying name of this page/component within its channel (for pages) or within its page (for components)
     *
     * @return name
     **/
    @JsonProperty("name")
    @NotNull
    public String getName() {
        return name;
    }


    /**
     * default display label for this page/component
     *
     * @return label
     **/
    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BasePageComponent label(String label) {
        this.label = label;
        return this;
    }

    /**
     * description for this page/component
     *
     * @return description
     **/
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BasePageComponent description(String description) {
        this.description = description;
        return this;
    }

    /**
     * a map of string parameters (names/values) for this page/component
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

    public BasePageComponent parameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public BasePageComponent putParametersItem(String key, String parametersItem) {
        this.parameters.put(key, parametersItem);
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BasePageComponent {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    label: ").append(toIndentedString(label)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
        sb.append("}");
        return sb.toString();
    }

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
}
