package com.bloomreach.xm.config.api.exception;

public class WorkspaceComponentNotFoundException extends Exception {

    public WorkspaceComponentNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public WorkspaceComponentNotFoundException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
