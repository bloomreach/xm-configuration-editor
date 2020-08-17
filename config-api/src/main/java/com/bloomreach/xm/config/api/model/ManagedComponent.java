package com.bloomreach.xm.config.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ManagedComponent extends AbstractComponent {

    /**
     * flag indicating if the component is a StaticComponent (default, false) or ManagedComponent (aka container, true)
     *
     * @return managed
     **/
    private Boolean managed = true;

    @JsonProperty("managed")
    @Override
    public Boolean isManaged() {
        return managed;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ManagedComponent {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
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
