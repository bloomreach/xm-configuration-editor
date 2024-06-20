/*
 *  Copyright 2024 Bloomreach
 */
package com.bloomreach.xm.config.api.v2.rest;

import java.net.URI;

import javax.jcr.Session;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;

import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.exception.PageLockedException;
import com.bloomreach.xm.config.api.exception.UnauthorizedException;
import com.bloomreach.xm.config.api.exception.WorkspaceComponentNotFoundException;
import com.bloomreach.xm.config.api.v2.dao.PageDao;
import com.bloomreach.xm.config.api.v2.model.Page;

import org.hippoecm.hst.pagecomposer.jaxrs.services.helpers.LockHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bloomreach.xm.config.api.v2.utils.CommonUtils.CONFIG_API_PERMISSION_CURRENT_PAGE_EDITOR;
import static com.bloomreach.xm.config.api.v2.utils.CommonUtils.CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER;
import static com.bloomreach.xm.config.api.v2.utils.CommonUtils.ensureUserIsAuthorized;
import static com.bloomreach.xm.config.api.v2.utils.CommonUtils.closeSession;
import static com.bloomreach.xm.config.api.v2.utils.CommonUtils.getImpersonatedSession;

public class ChannelFlexPageOperationsApiServiceImpl implements ChannelFlexPageOperationsApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelFlexPageOperationsApiServiceImpl.class);

    private final Session systemSession;
    private final LockHelper lockHelper;
    private final PageDao pageDao;

    public ChannelFlexPageOperationsApiServiceImpl(final Session session) {
        this.systemSession = session;
        this.lockHelper = new LockHelper();
        this.pageDao = new PageDao(systemSession, lockHelper);
    }

    /**
     * Get a channel page
     */
    public Page getChannelPage(HttpServletRequest request, String channelId, String pagePath) throws ChannelNotFoundException, UnauthorizedException, WorkspaceComponentNotFoundException {
        ensureUserIsAuthorized(request, CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER, systemSession);
        return this.pageDao.getPage(request, channelId, pagePath);
    }

    /**
     * Create or update a channel page
     */
    public Response putChannelPage(HttpServletRequest request, String channelId, String pagePath, Page page) throws UnauthorizedException, ChannelNotFoundException, WorkspaceComponentNotFoundException, PageLockedException {
        ensureUserIsAuthorized(request, CONFIG_API_PERMISSION_CURRENT_PAGE_EDITOR, systemSession);
        final Session session = getImpersonatedSession(systemSession);
        try {
            this.pageDao.save(page, pagePath, request, channelId, session);
        } finally {
            closeSession(session);
        }
        return Response.created(URI.create(String.format("/channels/%s/pages/%s", channelId, pagePath))).build();
    }


}

