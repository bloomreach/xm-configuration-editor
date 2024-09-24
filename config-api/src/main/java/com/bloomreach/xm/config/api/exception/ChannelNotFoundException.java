/*
 *  Copyright 2024 Bloomreach
 */
package com.bloomreach.xm.config.api.exception;

public class ChannelNotFoundException extends Exception {

    public ChannelNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public ChannelNotFoundException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
