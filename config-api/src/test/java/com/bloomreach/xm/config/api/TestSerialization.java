package com.bloomreach.xm.config.api;

import java.io.IOException;

import com.bloomreach.xm.config.api.v2.model.Page;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestSerialization {

    @Test
    public void testPageDeserialization() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        final Page page = mapper.readValue(getClass().getResource("/page.json"), Page.class);
        assertTrue("test2-contentpage".equals(page.getName()));
    }
}
