package com.bloomreach.xm.config.api.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExceptionModel {

    private final String errorMessage;
    private final int statusCode;

    public ExceptionModel(String errorMessage, int statusCode) {
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

    @JsonProperty(value = "errorMessage")
    public String getErrorMessage() {
        return errorMessage;
    }

    @JsonProperty(value = "statusCode")
    public int getStatusCode() {
        return statusCode;
    }
}
