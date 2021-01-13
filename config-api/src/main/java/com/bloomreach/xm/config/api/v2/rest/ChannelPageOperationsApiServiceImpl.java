package com.bloomreach.xm.config.api.v2.rest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bloomreach.xm.config.api.ConfigApiResource;
import com.bloomreach.xm.config.api.Utils;
import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.exception.UnauthorizedException;
import com.bloomreach.xm.config.api.exception.WorkspaceComponentNotFoundException;
import com.bloomreach.xm.config.api.model.ConfigApiPermissions;
import com.bloomreach.xm.config.api.v2.dao.PageDao;
import com.bloomreach.xm.config.api.v2.model.AbstractComponent;
import com.bloomreach.xm.config.api.v2.model.Page;

import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.pagecomposer.jaxrs.services.helpers.LockHelper;
import org.hippoecm.repository.api.HippoSession;
import org.onehippo.cms7.services.cmscontext.CmsSessionContext;
import org.onehippo.cms7.utilities.servlet.HttpSessionBoundJcrSessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bloomreach.xm.config.api.Utils.CONFIG_API_PERMISSION_CURRENT_PAGE_EDITOR;
import static com.bloomreach.xm.config.api.Utils.CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER;
import static com.bloomreach.xm.config.api.Utils.configToComponentMapper;
import static com.bloomreach.xm.config.api.Utils.getHstSite;
import static com.bloomreach.xm.config.api.Utils.isXPage;


public class ChannelPageOperationsApiServiceImpl implements ChannelPageOperationsApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelPageOperationsApiServiceImpl.class);
    private static final String SYSTEM_USER = "system";

    private final Session systemSession;
    private final LockHelper lockHelper;
    private final PageDao pageDao;

    public ChannelPageOperationsApiServiceImpl(final Session session) {
        this.systemSession = session;
        this.lockHelper = new LockHelper();
        this.pageDao = new PageDao();
    }

    /**
     * @return request-scoped {@link Session} session with system privileges
     * @throws InternalServerErrorException
     */
    private Session getImpersonatedSession() throws InternalServerErrorException {
        try {
            return systemSession.impersonate(new SimpleCredentials(SYSTEM_USER, new char[]{}));
        } catch (RepositoryException ex) {
            LOGGER.error(ex.getMessage());
            throw new InternalServerErrorException("Internal server error", ex);
        }
    }

    /**
     * Get a channel page
     */
    public Page getChannelPage(HttpServletRequest request, String channelId, String pageName, Boolean resolved) throws ChannelNotFoundException, WorkspaceComponentNotFoundException, UnauthorizedException {
        ensureUserIsAuthorized(request, CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER);
        final Session userSession = getUserSession(request);
        final boolean isXPage = isXPage(channelId, pageName, userSession);
        Page page = this.pageDao.getPage(isXPage, request, channelId, pageName, userSession);
        return page;
    }

    /**
     * Create or update a channel page
     */
    public Page putChannelPage(@Context HttpServletRequest request, String channelId, String pageName, Page body) {
        // TODO: Implement...

        return null;
    }

    private void ensureUserIsAuthorized(final HttpServletRequest request, final String requiredUserRole) throws UnauthorizedException {
        final HippoSession userSession = (HippoSession)getUserSession(request);
        if (!userSession.isUserInRole(requiredUserRole)) {
            throw new UnauthorizedException(String.format("User %s does not have the (implied) userrole: %s", userSession.getUserID(), requiredUserRole));
        }
    }


    @Path("/acl")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response hasPermission(@Context final HttpServletRequest request) throws InternalServerErrorException {
        return Response.ok(getConfigApiPermissions(request)).build();
    }

    @Path("/channels/{channel_id}/components")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllComponents(@PathParam("channel_id") String channelId) throws ChannelNotFoundException {
        final Map<String, HstComponentConfiguration> componentConfigurations = getHstSite(channelId).getComponentsConfiguration().getComponentConfigurations();
        final List<AbstractComponent> components = componentConfigurations.values().stream()
                .filter(Utils::isFirstLevelChildOfHstComponents)
                .map(configToComponentMapper(systemSession))
                .collect(Collectors.toList());
        return Response.ok(components).build();
    }

    /**
     * @param httpServletRequest belonging to the config api call
     * @return Session of the user who logged into the CMS. No need to log this session out.
     */
    private Session getUserSession(final HttpServletRequest httpServletRequest) {
        final HttpSession httpSession = httpServletRequest.getSession(false);
        final CmsSessionContext cmsSessionContext = CmsSessionContext.getContext(httpSession);
        final SimpleCredentials credentials = cmsSessionContext.getRepositoryCredentials();
        try {
            return HttpSessionBoundJcrSessionHolder.getOrCreateJcrSession(ConfigApiResource.class.getName() + ".session",
                    httpSession, credentials, systemSession.getRepository()::login);
        } catch (RepositoryException e) {
            LOGGER.error("Repo exception", e);
            throw new InternalServerErrorException("Error on getting user session");
        }
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

    private ConfigApiPermissions getConfigApiPermissions(final HttpServletRequest request) {
        final HippoSession userSession = (HippoSession)getUserSession(request);
        return new ConfigApiPermissions(
                userSession.isUserInRole(CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER),
                userSession.isUserInRole(CONFIG_API_PERMISSION_CURRENT_PAGE_EDITOR));
    }

}

