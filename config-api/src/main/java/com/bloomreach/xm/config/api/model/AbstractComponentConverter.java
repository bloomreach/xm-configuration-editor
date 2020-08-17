package com.bloomreach.xm.config.api.model;

import com.bloomreach.xm.config.api.Builder;
import com.fasterxml.jackson.databind.util.StdConverter;

import java.util.Map;

public class AbstractComponentConverter extends StdConverter<Map<String, Object>, AbstractComponent> {
    @Override
    public AbstractComponent convert(Map<String, Object> value) {
        return Builder.buildComponent(value);
    }
}

