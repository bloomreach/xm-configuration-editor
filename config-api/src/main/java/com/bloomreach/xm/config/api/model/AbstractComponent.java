package com.bloomreach.xm.config.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonDeserialize(converter = AbstractComponentConverter.class)
public class AbstractComponent extends BasePageComponent {
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

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
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
    }

    @Schema(description = "")
    private XtypeEnum xtype = null;

    @Schema(description = "flag indicating if the component is a StaticComponent (default, false) or ManagedComponent (aka container, true)")
    /**
     * flag indicating if the component is a StaticComponent (default, false) or ManagedComponent (aka container, true)
     **/
    private Boolean managed = false;

    /**
     * Get xtype
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
     * flag indicating if the component is a StaticComponent (default, false) or ManagedComponent (aka container, true)
     *
     * @return managed
     **/
    @JsonProperty("managed")
    public Boolean isManaged() {
        return managed;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AbstractComponent {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    xtype: ").append(toIndentedString(xtype)).append("\n");
        sb.append("    managed: ").append(toIndentedString(managed)).append("\n");
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
