package com.bloomreach.xm.config.api.model;

import com.bloomreach.xm.config.api.Builder;
import com.fasterxml.jackson.databind.util.StdConverter;

import java.util.ArrayList;
import java.util.Map;

public class PageConverter extends StdConverter<Map<String, Object>, Page> {

    @Override
    public Page convert(Map<String, Object> value) {
        final Page page = new Page();
        page.setName((String) value.get("name"));
        page.setDescription((String) value.get("description"));
        page.setLabel((String) value.get("label"));
        page.setParameters((Map<String, String>) value.get("parameters"));
        page.setType((String) value.get("type"));
        if (value.containsKey("components")) {
            ((ArrayList<Map<String, Object>>) value.get("components")).forEach(child -> {
                page.addComponentsItem(Builder.buildComponent(child));
            });
        }
        return page;
    }
}

