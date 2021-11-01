package com.bloomreach.xm.config.api.v2.model;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Page {

    @Schema(required = true, description = "identifying name of this page within its channel")
    /**
     * identifying name of this page within its channel
     **/
    private String name = null;
    @Schema(description = "description for this page")
    /**
     * description for this page
     **/
    private String description = null;
    @Schema(description = "a map of string parameters (names/values) for this page")
    private String componentClassName = null;

    /**
     * a map of string parameters (names/values) for this page
     **/
    private Map<String, String> parameters = null;
    @Schema(required = true, description = "page type defines the usage. Only abstract pages can be extended. Valid values: abstract, page, xpage")
    /**
     * page type defines the usage. Only abstract pages can be extended. Valid values: abstract, page, xpage
     **/
    private PageType type = null;
    @Schema(description = "the name of the (abstract) page extended by this page")
    /**
     * the name of the (abstract) page extended by this page
     **/
    private String _extends = null;
    @Schema(description = "the components that are defined in the current page")
    /**
     * the components that are defined in the current page
     **/
    private List<AbstractComponent> components = null;

    @JsonCreator
    public Page(@JsonProperty("name") final String name, @JsonProperty("description") final String description, @JsonProperty("parameters") final Map<String, String> parameters, @JsonProperty("type") final PageType type, @JsonProperty("extends") final String _extends, @JsonProperty("componentClassName") final String componentClassName) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
        this.type = type;
        this._extends = _extends;
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

    /**
     * identifying name of this page within its channel
     *
     * @return name
     **/
    @JsonProperty("name")
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * description for this page
     *
     * @return description
     **/
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("componentClassName")
    public String getComponentClassName() {
        return componentClassName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Page description(String description) {
        this.description = description;
        return this;
    }

    public void setComponentClassName(String componentClassName) {
        this.componentClassName = componentClassName;
    }

    public Page componentClassName(String componentClassName) {
        this.componentClassName = componentClassName;
        return this;
    }

    /**
     * a map of string parameters (names/values) for this page
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

    public Page parameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public Page putParametersItem(String key, String parametersItem) {
        this.parameters.put(key, parametersItem);
        return this;
    }

    /**
     * page type defines the usage. Only abstract pages can be extended. Valid values: abstract, page, xpage
     *
     * @return type
     **/
    @JsonProperty("type")
    @NotNull
    public String getType() {
        if (type == null) {
            return null;
        }
        return type.getValue();
    }

    /**
     * the name of the (abstract) page extended by this page
     *
     * @return _extends
     **/

    @JsonProperty("extends")
    public String getExtends() {
        return _extends;
    }

    public void setExtends(String _extends) {
        this._extends = _extends;
    }

    public Page _extends(String _extends) {
        this._extends = _extends;
        return this;
    }

    /**
     * the components that are defined in the current page
     *
     * @return components
     **/
    @JsonProperty("components")
    public List<AbstractComponent> getComponents() {
        return components;
    }

    public void setComponents(List<AbstractComponent> components) {
        this.components = components;
    }

    public Page components(List<AbstractComponent> components) {
        this.components = components;
        return this;
    }

    public Page addComponentsItem(AbstractComponent componentsItem) {
        this.components.add(componentsItem);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Page {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    _extends: ").append(toIndentedString(_extends)).append("\n");
        sb.append("    components: ").append(toIndentedString(components)).append("\n");
        sb.append("}");
        return sb.toString();
    }


    public enum PageType {
        ABSTRACT("abstract"),
        PAGE("page"),
        XPAGE("xpage");

        private String value;

        PageType(String value) {
            this.value = value;
        }

        @JsonCreator
        public static PageType fromValue(String text) {
            for (PageType b : PageType.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
