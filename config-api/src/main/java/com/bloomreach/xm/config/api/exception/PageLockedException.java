/*
 *  Copyright 2024 Bloomreach
 */
package com.bloomreach.xm.config.api.exception;

public class PageLockedException extends Exception {

    public PageLockedException(String errorMessage) {
        super(errorMessage);
    }

    public PageLockedException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
