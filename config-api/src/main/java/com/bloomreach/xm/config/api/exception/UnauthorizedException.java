package com.bloomreach.xm.config.api.exception;

public class UnauthorizedException extends Exception {

    public UnauthorizedException(String errorMessage) {
        super(errorMessage);
    }

    public UnauthorizedException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
