/*
 *  Copyright 2024 Bloomreach
 */
package com.bloomreach.xm.config.api.v2.model;

public class ConfigApiPermissions {

    private final boolean isCurrentPageViewAllowed;
    private final boolean isCurrentPageEditAllowed;
    private final boolean isConfigUser;

    public ConfigApiPermissions(final boolean isCurrentPageViewAllowed, final boolean isCurrentPageEditAllowed, final boolean isConfigUser) {
        this.isCurrentPageViewAllowed = isCurrentPageViewAllowed;
        this.isCurrentPageEditAllowed = isCurrentPageEditAllowed;
        this.isConfigUser = isConfigUser;
    }

    public boolean isCurrentPageViewAllowed() {
        return isCurrentPageViewAllowed;
    }

    public boolean isCurrentPageEditAllowed() {
        return isCurrentPageEditAllowed;
    }

    public boolean isConfigUser() {
        return isConfigUser;
    }
}
