/*
 *  Copyright 2024 Bloomreach
 */
package com.bloomreach.xm.config.api.v2.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.SuperBuilder;

/**
 * the components that are defined in the current page
 **/

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ManagedComponent.class),
        @JsonSubTypes.Type(value = StaticComponent.class)
})
@SuperBuilder
@Schema(description = "the components that are defined in the current page")
public class AbstractComponent {

    @JsonCreator
    public AbstractComponent(@JsonProperty("name") final String name, @JsonProperty("description") final String description, @JsonProperty("parameters") final Map<String, String> parameters, @JsonProperty("xtype") final XtypeEnum xtype, @JsonProperty("type") final TypeEnum type) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
        this.xtype = xtype;
        this.type = type;
    }

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "identifying name of this component within its page")
    /**
     * identifying name of this component within its page
     **/
    private String name = null;

    @Schema(description = "description for this component")
    /**
     * description for this component
     **/
    private String description = null;

    @Schema(description = "a map of string parameters (names/values) for this component")
    /**
     * a map of string parameters (names/values) for this component
     **/
    private Map<String, String> parameters = null;
    @Schema(description = "the layout used for rendering inner containers")
    /**
     * the layout used for rendering inner containers
     **/
    private XtypeEnum xtype = null;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "the type of this component")
    /**
     * the type of this component
     **/
    private TypeEnum type = null;

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
     * identifying name of this component within its page
     *
     * @return name
     **/
    @JsonProperty("name")
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * description for this component
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

    public AbstractComponent description(String description) {
        this.description = description;
        return this;
    }

    /**
     * a map of string parameters (names/values) for this component
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

    public AbstractComponent parameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public AbstractComponent putParametersItem(String key, String parametersItem) {
        this.parameters.put(key, parametersItem);
        return this;
    }

    /**
     * the layout used for rendering inner containers
     *
     * @return xtype
     **/
    @JsonProperty("xtype")
    public String getXtype() {
        if (xtype == null) {
            return null;
        }
        return xtype.getValue();
    }

    public void setXtype(XtypeEnum xtype) {
        this.xtype = xtype;
    }

    public AbstractComponent xtype(XtypeEnum xtype) {
        this.xtype = xtype;
        return this;
    }

    /**
     * the type of this component
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AbstractComponent {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
        sb.append("    xtype: ").append(toIndentedString(xtype)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("}");
        return sb.toString();
    }


    public enum XtypeEnum {
        VBOX("hst.vbox"),
        UNORDEREDLIST("hst.unorderedlist"),
        ORDEREDLIST("hst.orderedlist"),
        SPAN("hst.span"),
        NOMARKUP("hst.nomarkup");

        private String value;

        XtypeEnum(String value) {
            this.value = value;
        }

        @JsonCreator
        public static XtypeEnum fromValue(String text) {
            for (XtypeEnum b : XtypeEnum.values()) {
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

    public enum TypeEnum {
        STATIC("static"),
        MANAGED("managed");

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        @JsonCreator
        public static TypeEnum fromValue(String text) {
            for (TypeEnum b : TypeEnum.values()) {
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
