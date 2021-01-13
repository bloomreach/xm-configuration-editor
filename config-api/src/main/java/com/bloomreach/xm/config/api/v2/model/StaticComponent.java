package com.bloomreach.xm.config.api.v2.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.SuperBuilder;

@SuperBuilder
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
