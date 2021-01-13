package com.bloomreach.xm.config.api.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class ManagedComponent extends AbstractComponent {

    @Schema(description = "default display label for this component")
    /**
     * default display label for this component
     **/
    private String label = null;

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
     * default display label for this component
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

    public ManagedComponent label(String label) {
        this.label = label;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ManagedComponent {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    label: ").append(toIndentedString(label)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
