package com.bloomreach.xm.config.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public class StaticComponent extends AbstractComponent {

    @Schema(description = "reference to a catalog component item by its <group>/<name> identifier")
    /**
     * reference to a catalog component item by its <group>/<name> identifier
     **/
    private String definition = null;

    @Schema(description = "")
    private List<AbstractComponent> components = new ArrayList<>();

    /**
     * reference to a catalog component item by its &lt;group&gt;/&lt;name&gt; identifier
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

    /**
     * Get components
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


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class StaticComponent {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    definition: ").append(toIndentedString(definition)).append("\n");
        sb.append("    components: ").append(toIndentedString(components)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private static String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
