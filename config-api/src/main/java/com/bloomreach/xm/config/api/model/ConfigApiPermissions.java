package com.bloomreach.xm.config.api.model;

public class ConfigApiPermissions {

    private final boolean isCurrentPageViewAllowed;
    private final boolean isCurrentPageEditAllowed;

    public ConfigApiPermissions(boolean isCurrentPageViewAllowed, boolean isCurrentPageEditAllowed) {
        this.isCurrentPageViewAllowed = isCurrentPageViewAllowed;
        this.isCurrentPageEditAllowed = isCurrentPageEditAllowed;
    }

    public boolean isCurrentPageViewAllowed() {
        return isCurrentPageViewAllowed;
    }

    public boolean isCurrentPageEditAllowed() {
        return isCurrentPageEditAllowed;
    }
}
