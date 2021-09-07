package com.bloomreach.xm.config.api.v2.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@JsonTypeName("managed")
public class ManagedComponent extends AbstractComponent {

    @Schema(description = "default display label for this component")
    /**
     * default display label for this component
     **/
    private String label = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean fromTemplate = null;

    @JsonCreator
    public ManagedComponent(@JsonProperty("name") final String name, @JsonProperty("description") final String description, @JsonProperty("parameters") final Map<String, String> parameters, @JsonProperty("xtype") final XtypeEnum xtype, @JsonProperty("type") final TypeEnum type, @JsonProperty("label") final String label, @JsonProperty("fromTemplate") final boolean fromTemplate) {
        super(name, description, parameters, xtype, type);
        this.label = label;
        this.fromTemplate = fromTemplate;
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

    public Boolean isFromTemplate() {
        return fromTemplate;
    }

    public void setFromTemplate(Boolean fromTemplate) {
        this.fromTemplate = fromTemplate;
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
