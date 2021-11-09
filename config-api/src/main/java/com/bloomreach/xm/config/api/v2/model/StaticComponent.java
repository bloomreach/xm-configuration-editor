package com.bloomreach.xm.config.api.v2.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.SuperBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder
@JsonTypeName("static")
public class StaticComponent extends AbstractComponent {

    @Schema(description = "the catalog component definition this component is based on")
    /**
     * the catalog component definition this component is based on
     **/
    private String definition = null;
    /**
     * the components that are defined in the current page
     **/
    private List<AbstractComponent> components = null;

    @JsonCreator
    public StaticComponent(@JsonProperty("name") final String name, @JsonProperty("description") final String description, @JsonProperty("parameters") final Map<String, String> parameters, @JsonProperty("xtype") final XtypeEnum xtype, @JsonProperty("type") final TypeEnum type, @JsonProperty("definition") String definition, @JsonProperty("componentClassName") final String componentClassName) {
        super(name, description, parameters, xtype, type);
        this.definition = definition;
        this.componentClassName = componentClassName;
    }

    private String componentClassName = null;

    public void setComponentClassName(String componentClassName) {
        this.componentClassName = componentClassName;
    }

    public AbstractComponent componentClassName(String componentClassName) {
        this.componentClassName = componentClassName;
        return this;
    }

    public String getComponentClassName() {
        return componentClassName;
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

    public StaticComponent components(List<AbstractComponent> components) {
        this.components = components;
        return this;
    }

    public StaticComponent addComponentsItem(AbstractComponent componentsItem) {
        this.components.add(componentsItem);
        return this;
    }

    /**
     * the catalog component definition this component is based on
     *
     * @return definition
     **/
    @JsonProperty("definition")
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public StaticComponent definition(String definition) {
        this.definition = definition;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class StaticComponent {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    definition: ").append(toIndentedString(definition)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
