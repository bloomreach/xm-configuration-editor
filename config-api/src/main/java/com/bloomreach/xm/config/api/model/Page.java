package com.bloomreach.xm.config.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(converter = PageConverter.class)
public class Page extends BasePageComponent {
    public enum TypeEnum {
        ABSTRACT("abstract"),
        PAGE("page"),
        XPAGE("xpage");

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
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
    }

    @Schema(required = true, description = "page type defines the usage and extendability: only abstract pages may be extended")
    /**
     * page type defines the usage and extendability: only abstract pages may be extended
     **/
    private TypeEnum type = null;

    @Schema(description = "the name of the (abstract) page extended by this page.")
    /**
     * the name of the (abstract) page extended by this page.
     **/
    private String _extends = null;

    @Schema(description = "")
    private List<AbstractComponent> components = new ArrayList<>();

    /**
     * page type defines the usage and extendability: only abstract pages may be extended
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
     * the name of the (abstract) page extended by this page.
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
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    _extends: ").append(toIndentedString(_extends)).append("\n");
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
