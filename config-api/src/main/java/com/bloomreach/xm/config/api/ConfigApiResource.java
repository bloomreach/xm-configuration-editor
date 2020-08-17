package com.bloomreach.xm.config.api;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.exception.PageLockedException;
import com.bloomreach.xm.config.api.exception.UnauthorizedException;
import com.bloomreach.xm.config.api.exception.WorkspaceComponentNotFoundException;
import com.bloomreach.xm.config.api.model.AbstractComponent;
import com.bloomreach.xm.config.api.model.ConfigApiPermissions;
import com.bloomreach.xm.config.api.model.Page;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.configuration.site.HstSite;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.container.site.CompositeHstSite;
import org.hippoecm.hst.core.internal.PreviewDecorator;
import org.hippoecm.hst.pagecomposer.jaxrs.services.exceptions.ClientException;
import org.hippoecm.hst.pagecomposer.jaxrs.services.helpers.LockHelper;
import org.hippoecm.hst.platform.model.HstModel;
import org.hippoecm.hst.platform.model.HstModelRegistry;
import org.hippoecm.hst.site.HstServices;
import org.hippoecm.repository.api.HippoSession;
import org.hippoecm.repository.util.JcrUtils;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.cms7.services.cmscontext.CmsSessionContext;
import org.onehippo.cms7.services.hst.Channel;
import org.onehippo.cms7.utilities.servlet.HttpSessionBoundJcrSessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bloomreach.xm.config.api.Builder.buildComponent;
import static com.bloomreach.xm.config.api.Builder.buildComponentNode;
import static com.bloomreach.xm.config.api.Builder.buildPage;
import static com.bloomreach.xm.config.api.Builder.buildPageNode;

public class ConfigApiResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigApiResource.class);
    private static final String SYSTEM_USER = "system";
    private static final String CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER = "xm.config-editor.current-page.viewer";
    private static final String CONFIG_API_PERMISSION_CURRENT_PAGE_EDITOR = "xm.config-editor.current-page.editor";
    private final Session systemSession;
    private final LockHelper lockHelper;

    public ConfigApiResource(final Session session) {
        this.systemSession = session;
        this.lockHelper = new LockHelper();
    }

    @Path("/channels/{channel_id}/pages/{page_name}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    public Response putPageWithName(@Context HttpServletRequest request, @PathParam("channel_id") String channelId, @PathParam("page_name") String pageName, final Page page)
            throws ChannelNotFoundException, WorkspaceComponentNotFoundException, PageLockedException, UnauthorizedException {
        ensureUserIsAuthorized(request, CONFIG_API_PERMISSION_CURRENT_PAGE_EDITOR);
        final Session session = getImpersonatedSession();
        final Session userSession = getUserSession(request);
        final HstSite hstSite = getHstSite(channelId);
        final HstComponentConfiguration componentConfig = getComponentConfig(hstSite, pageName);
        Node configNode = null;
        try {
            configNode = JcrUtils.getNodeIfExists(componentConfig.getCanonicalStoredLocation(), session);
            lockHelper.acquireLock(configNode, userSession.getUserID(), 0);
            buildPageNode(session, page, componentConfig.getCanonicalStoredLocation(), userSession.getUserID());
            session.save();
        } catch (ClientException ex) {
            LOGGER.error(ex.getMessage());
            throw new PageLockedException("Cannot update page hierarchy as it's locked by " + ex.getParameterMap().get("lockedBy"));
        } catch (RepositoryException ex) {
            LOGGER.error(ex.getMessage());
            unlockQuietly(configNode); //we only want to unlock here.Publishing changes in EM will release the lock
            throw new InternalServerErrorException("Internal server error", ex);
        } finally {
            closeSession(session);
        }
        return Response.created(URI.create(String.format("/channels/%s/pages/%s", channelId, pageName))).build();
    }

    @Path("/channels/{channel_id}/pages/{page_name}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPageByName(@Context HttpServletRequest request, @PathParam("channel_id") String channelId, @PathParam("page_name") String pageName) throws ChannelNotFoundException, WorkspaceComponentNotFoundException, UnauthorizedException {
        ensureUserIsAuthorized(request, CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER);
        final HstComponentConfiguration componentConfig = getComponentConfig(getHstSite(channelId), pageName);
        final Page page = buildPage(componentConfig, systemSession);
        final List<HstComponentConfiguration> immediateChildren = componentConfig.getChildren().values().stream()
                .filter(childConfig -> childConfig.getCanonicalStoredLocation().contains(componentConfig.getCanonicalStoredLocation()))
                .collect(Collectors.toList());
        immediateChildren.forEach(childConfig -> {
            final AbstractComponent component = buildComponent(childConfig, systemSession);
            page.addComponentsItem(component);
        });
        return Response.ok(page).build();
    }

    @Path("/channels/{channel_id}/components")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllComponents(@PathParam("channel_id") String channelId) throws ChannelNotFoundException {
        final Map<String, HstComponentConfiguration> componentConfigurations = getHstSite(channelId).getComponentsConfiguration().getComponentConfigurations();
        final List<AbstractComponent> components = componentConfigurations.values().stream()
                .filter(this::isFirstLevelChildOfHstComponents)
                .map(componentConfig -> Builder.buildComponent(componentConfig, systemSession))
                .collect(Collectors.toList());
        return Response.ok(components).build();
    }

//    @Path("/channels/{channel_id}/components/{component_name}")
//    @PUT
//    @Produces({MediaType.APPLICATION_JSON})
//    public Response putComponentWithName(@Context HttpServletRequest request, @PathParam("channel_id") String channelId, @PathParam("component_name") String componentName, final AbstractComponent component) throws ChannelNotFoundException {
//        final Session requestSession = getImpersonatedSession();
//        final HstSite hstSite = getHstSite(channelId);
//        try {
//            //todo authorize request
//            //todo locking
//            //todo move component registry to live which will affect how hstSite object is got.
//            buildComponentNode(requestSession, component, hstSite, getUserSession(request).getUserID());
//            requestSession.save();
//        } catch (RepositoryException ex) {
//            LOGGER.error(ex.getMessage());
//            throw new InternalServerErrorException("Internal server error", ex);
//        } finally {
//            closeSession(requestSession);
//        }
//        return Response.status(201).location(URI.create(String.format("/channels/%s/components/%s", channelId, componentName))).entity(component).build();
//    }
//
//    @Path("/channels/{channel_id}/components/{component_name}")
//    @DELETE
//    @Produces({MediaType.APPLICATION_JSON})
//    public Response deleteComponentWithName(@Context HttpServletRequest request, @PathParam("channel_id") String channelId, @PathParam("component_name") String componentName) throws ChannelNotFoundException {
//        final Session requestSession = getImpersonatedSession();
//        final HstSite hstSite = getHstSite(channelId);
//        try {
//            //todo authorize request
//            //todo locking
//            //todo actual delete, now only in the frontend
//            //check if such a  component already exists. If so, we have to update it.
////            final String componentRegistryPath = hstSite.getConfigurationPath() + "/hst:workspace/hst:components"; //TODO refactor
////            final Node componentRegistryNode = requestSession.getNode(componentRegistryPath);
////            componentRegistryNode.remove();
////            requestSession.save();
//
//        } finally {
//            closeSession(requestSession);
//        }
//        return Response.ok().build();
//    }

    @Path("/acl")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response hasPermission(@Context final HttpServletRequest request) throws InternalServerErrorException {
        return Response.ok(getConfigApiPermissions(request)).build();
    }

    private void ensureUserIsAuthorized(final HttpServletRequest request, final String requiredUserRole) throws UnauthorizedException {
        final HippoSession userSession = (HippoSession)getUserSession(request);
        if (!userSession.isUserInRole(requiredUserRole)) {
            throw new UnauthorizedException(String.format("User %s does not have the (implied) userrole: %s", userSession.getUserID(), requiredUserRole));
        }
    }

    private ConfigApiPermissions getConfigApiPermissions(final HttpServletRequest request) {
        final HippoSession userSession = (HippoSession)getUserSession(request);
        return new ConfigApiPermissions(
                userSession.isUserInRole(CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER),
                userSession.isUserInRole(CONFIG_API_PERMISSION_CURRENT_PAGE_EDITOR));
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

    private void closeSession(final Session session) {
        if (session != null && session.isLive()) {
            session.logout();
        }
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

    private HstComponentConfiguration getComponentConfig(final HstSite hstSite, final String pageName) throws WorkspaceComponentNotFoundException, ChannelNotFoundException {
        final String componentConfigurationId = hstSite.getSiteMap().getSiteMapItem(pageName).getComponentConfigurationId();
        final HstComponentConfiguration componentConfig = hstSite.getComponentsConfiguration().getComponentConfiguration(componentConfigurationId);
        if (componentConfig == null || !isWorkspaceComponent(componentConfig)) {
            throw new WorkspaceComponentNotFoundException("Could not find workspace hst component for page: " + pageName);
        }
        return componentConfig;
    }

    /**
     * @param componentConfig Hst Component config to check
     * @return true if given hst component config is the immediate child of hst:components node
     */
    private boolean isFirstLevelChildOfHstComponents(final HstComponentConfiguration componentConfig) {
        return componentConfig.getId().startsWith("hst:components/") && componentConfig.getId().chars().filter(ch -> ch == '/').count() == 1;
    }

    private HstSite getHstSite(final String channelId) throws ChannelNotFoundException {
        final HstModelRegistry service = HippoServiceRegistry.getService(HstModelRegistry.class);
        String branchId = null;
        HstModel hstModel = null;
        for (HstModel model : service.getHstModels()) {
            final Channel channel = model.getVirtualHosts().getChannelById(getVirtualHostGroupName(), channelId);
            if (channel != null) {
                hstModel = model;
                branchId = channel.getBranchId();
                break;
            }
        }
        if (hstModel == null) {
            throw new ChannelNotFoundException("Channel with id: " + channelId + " not found");
        }

        final String finalBranchId = branchId;
        if (finalBranchId != null) {
            String masterChannelId = StringUtils.remove(channelId, branchId + "-");
            LOGGER.debug("fetching hstSite for project branch: {}", finalBranchId);
            return getHstSiteStream(hstModel, masterChannelId)
                    .map(hstSite -> ((CompositeHstSite)hstSite).getBranches().get(finalBranchId))
                    .findFirst()
                    .orElseThrow(() -> new ChannelNotFoundException("Channel with id: " + channelId + " not found"));
        } else {
            return getHstSiteStream(hstModel, channelId)
                    .findFirst()
                    .orElseThrow(() -> new ChannelNotFoundException("Channel with id: " + channelId + " not found"));
        }
    }

    private Stream<HstSite> getHstSiteStream(final HstModel hstModel, final String masterChannelId) {
        final PreviewDecorator pd = HstServices.getComponentManager().getComponent(PreviewDecorator.class);
        return pd.decorateVirtualHostsAsPreview(hstModel.getVirtualHosts()).getMountsByHostGroup(getVirtualHostGroupName()).stream()
                .filter(m -> m.getChannel() != null && m.getChannel().getId().equals(masterChannelId))
                .map(Mount::getHstSite);
    }

    /**
     * @return The virtual host name of the hst platform which should be the same as the browsed channels'
     * virtual host group
     */
    private String getVirtualHostGroupName() {
        return RequestContextProvider.get().getResolvedMount().getMount().getVirtualHost().getHostGroupName();
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

    private boolean isWorkspaceComponent(final HstComponentConfiguration componentConfiguration) {
        return componentConfiguration.getCanonicalStoredLocation().contains("/hst:workspace");
    }

}