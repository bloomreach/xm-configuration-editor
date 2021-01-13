package com.bloomreach.xm.config.api.v2.rest;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.exception.UnauthorizedException;
import com.bloomreach.xm.config.api.exception.WorkspaceComponentNotFoundException;
import com.bloomreach.xm.config.api.v2.dao.PageDao;
import com.bloomreach.xm.config.api.v2.model.Page;

import org.hippoecm.hst.pagecomposer.jaxrs.services.helpers.LockHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bloomreach.xm.config.api.Utils.CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER;
import static com.bloomreach.xm.config.api.Utils.ensureUserIsAuthorized;
import static com.bloomreach.xm.config.api.Utils.getUserSession;
import static com.bloomreach.xm.config.api.Utils.isXPage;


public class ChannelFlexPageOperationsApiServiceImpl implements ChannelFlexPageOperationsApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelFlexPageOperationsApiServiceImpl.class);

    private final Session systemSession;
    private final LockHelper lockHelper;
    private final PageDao pageDao;

    public ChannelFlexPageOperationsApiServiceImpl(final Session session) {
        this.systemSession = session;
        this.lockHelper = new LockHelper();
        this.pageDao = new PageDao();
    }

    /**
     * Get a channel page
     */
    public Page getChannelPage(HttpServletRequest request, String channelId, String pagePath) throws ChannelNotFoundException, WorkspaceComponentNotFoundException, UnauthorizedException {
        ensureUserIsAuthorized(request, CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER, systemSession);
        final Session userSession = getUserSession(request, systemSession);
        final boolean isXPage = isXPage(channelId, pagePath, userSession);
        Page page = this.pageDao.getPage(isXPage, request, channelId, pagePath, userSession);
        return page;
    }

    /**
     * Create or update a channel page
     */
    public Page putChannelPage(HttpServletRequest request, String channelId, String pagePath, Page body) {
        // TODO: Implement...

        return body;
    }


    /**
     * Unlocks a given hst component and it's descendents
     *
     * @param configNode page node to be unlocked quietly
     */
    private void unlockQuietly(final Node configNode) {
        try {
            if (configNode != null) {
                lockHelper.unlock(configNode);
                configNode.getSession().save();
            }
        } catch (RepositoryException ex) {
            LOGGER.error(ex.getMessage());
        }
    }


}

