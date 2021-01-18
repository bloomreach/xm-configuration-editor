//package com.bloomreach.xm.config.api;
//
//import java.io.IOException;
//
//import com.bloomreach.xm.config.api.model.Page;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.junit.Test;
//
//import static org.junit.Assert.assertEquals;
//
//public class TestPage {
//
//    @Test
//    public void testPageDeserialization() throws IOException {
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        final Page page = mapper.readValue(getClass().getResource("/page.json"), Page.class);
//
//        assertEquals("xpage", page.getType());
//
//    }
//}
