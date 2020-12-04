package com.bloomreach.xm.config.api;

import java.net.URI;
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

import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.configuration.experiencepage.ExperiencePageService;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.configuration.site.HstSite;
import org.hippoecm.hst.pagecomposer.jaxrs.services.exceptions.ClientException;
import org.hippoecm.hst.pagecomposer.jaxrs.services.helpers.LockHelper;
import org.hippoecm.hst.pagecomposer.jaxrs.services.util.ContainerUtils;
import org.hippoecm.hst.platform.model.HstModelRegistry;
import org.hippoecm.hst.util.HstRequestUtils;
import org.hippoecm.repository.api.HippoSession;
import org.hippoecm.repository.api.WorkflowException;
import org.hippoecm.repository.util.JcrUtils;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.cms7.services.cmscontext.CmsSessionContext;
import org.onehippo.cms7.utilities.servlet.HttpSessionBoundJcrSessionHolder;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bloomreach.xm.config.api.Builder.buildComponent;
import static com.bloomreach.xm.config.api.Builder.buildPage;
import static com.bloomreach.xm.config.api.Builder.buildPageNode;
import static com.bloomreach.xm.config.api.Builder.buildXComponent;
import static com.bloomreach.xm.config.api.Builder.buildXPageNode;
import static com.bloomreach.xm.config.api.Utils.CONFIG_API_PERMISSION_CURRENT_PAGE_EDITOR;
import static com.bloomreach.xm.config.api.Utils.CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER;
import static com.bloomreach.xm.config.api.Utils.closeSession;
import static com.bloomreach.xm.config.api.Utils.getComponentConfig;
import static com.bloomreach.xm.config.api.Utils.getHandle;
import static com.bloomreach.xm.config.api.Utils.getHstSite;
import static com.bloomreach.xm.config.api.Utils.getMount;
import static com.bloomreach.xm.config.api.Utils.getObtainEditableInstanceWorkflow;
import static com.bloomreach.xm.config.api.Utils.getXPageModelFromVariantNode;
import static com.bloomreach.xm.config.api.Utils.getXPageTemplate;
import static com.bloomreach.xm.config.api.Utils.getXPageUnpublishedNode;
import static com.bloomreach.xm.config.api.Utils.isXPage;

public class ConfigApiResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigApiResource.class);
    private static final String SYSTEM_USER = "system";

    private final Session systemSession;
    private final LockHelper lockHelper;
    private HstModelRegistry hstModelRegistry;

    public ConfigApiResource(final Session session) {
        this.systemSession = session;
        this.lockHelper = new LockHelper();
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


    @Path("/channels/{channel_id}/pages/{page_name:.*}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    public Response putPageWithName(@Context HttpServletRequest request, @PathParam("channel_id") String channelId, @PathParam("page_name") String pageName, final Page page)
            throws ChannelNotFoundException, WorkspaceComponentNotFoundException, PageLockedException, UnauthorizedException {
        ensureUserIsAuthorized(request, CONFIG_API_PERMISSION_CURRENT_PAGE_EDITOR);
        final Session userSession = getUserSession(request);
        final Session session = getImpersonatedSession();

        if (Page.TypeEnum.XPAGE.getValue().equals(page.getType())) {
            try {
                final String cmsSessionActiveBranchId = HstRequestUtils.getCmsSessionActiveBranchId(request);

                final Node xPageUnpublishedNode = getXPageUnpublishedNode(channelId, pageName, userSession);
                final Node handle = getHandle(xPageUnpublishedNode);
                final DocumentWorkflow documentWorkflow = getObtainEditableInstanceWorkflow(userSession, handle.getIdentifier(), cmsSessionActiveBranchId);
//                todo check why this is failing
//                documentWorkflow.checkoutBranch(cmsSessionActiveBranchId);
                final Node xpage = getXPageModelFromVariantNode(xPageUnpublishedNode);
                final HstComponentConfiguration xPageTemplate = getXPageTemplate(getHstSite(channelId), xpage);
                Session workflowSession = documentWorkflow.getWorkflowContext().getInternalWorkflowSession();
                buildXPageNode(workflowSession, xPageTemplate, page, xpage.getPath(), userSession.getUserID());
                documentWorkflow.saveUnpublished(); //todo check why this is breaking
                return Response.created(URI.create(String.format("/channels/%s/pages/%s", channelId, pageName))).build();
            } catch (RepositoryException | WorkflowException e) {
                LOGGER.error(e.getMessage());
                throw new InternalServerErrorException("Internal server error", e);
            }
        } else {
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
    }

    @Path("/channels/{channel_id}/pages/{page_name:.*}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPageByName(@Context HttpServletRequest request, @PathParam("channel_id") String channelId, @PathParam("page_name") String pageName) throws ChannelNotFoundException, WorkspaceComponentNotFoundException, UnauthorizedException {
        ensureUserIsAuthorized(request, CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER);
        final Session userSession = getUserSession(request);
        if (isXPage(channelId, pageName, userSession)) {
            //works with different channels!
            final Mount mount = getMount(channelId);
            // experience page!
            final Node unpublishedNode = getXPageUnpublishedNode(channelId, pageName, userSession);
            final Node xPageModelFromVariantNode = getXPageModelFromVariantNode(unpublishedNode);
            // this is a node of type hst:component, fetch the hst component configuration for it
            final ExperiencePageService experiencePageService = HippoServiceRegistry.getService(ExperiencePageService.class);

            final HstComponentConfiguration config = experiencePageService.loadExperiencePage(xPageModelFromVariantNode, mount.getHstSite(),
                    this.getClass().getClassLoader());

            final Page page = buildPage(config, systemSession, Page.TypeEnum.XPAGE);

            final List<HstComponentConfiguration> immediateChildren = config.getChildren().values().stream()
                    .filter(childConfig -> childConfig.getCanonicalStoredLocation().contains("hst:xpages"))  //todo do better
                    .collect(Collectors.toList());
            immediateChildren.forEach(childConfig -> {
                final AbstractComponent component = buildXComponent(childConfig, systemSession);
                page.addComponentsItem(component);
            });
            page.setType(Page.TypeEnum.XPAGE);
            return Response.ok(page).build();
        } else {
            final HstComponentConfiguration componentConfig = getComponentConfig(getHstSite(channelId), pageName);
            final Page page = buildPage(componentConfig, systemSession, Page.TypeEnum.PAGE);
            final List<HstComponentConfiguration> immediateChildren = componentConfig.getChildren().values().stream()
                    .filter(childConfig -> childConfig.getCanonicalStoredLocation().contains(componentConfig.getCanonicalStoredLocation()))
                    .collect(Collectors.toList());
            immediateChildren.forEach(childConfig -> {
                final AbstractComponent component = buildComponent(childConfig, systemSession);
                page.addComponentsItem(component);
            });
            return Response.ok(page).build();
        }
    }

    @Path("/channels/{channel_id}/components")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllComponents(@PathParam("channel_id") String channelId) throws ChannelNotFoundException {
        final Map<String, HstComponentConfiguration> componentConfigurations = getHstSite(channelId).getComponentsConfiguration().getComponentConfigurations();
        final List<AbstractComponent> components = componentConfigurations.values().stream()
                .filter(Utils::isFirstLevelChildOfHstComponents)
                .map(componentConfig -> Builder.buildComponent(componentConfig, systemSession))
                .collect(Collectors.toList());
        return Response.ok(components).build();
    }

    @Path("/acl")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response hasPermission(@Context final HttpServletRequest request) throws InternalServerErrorException {
        return Response.ok(getConfigApiPermissions(request)).build();
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

    private void ensureUserIsAuthorized(final HttpServletRequest request, final String requiredUserRole) throws UnauthorizedException {
        final HippoSession userSession = (HippoSession)getUserSession(request);
        if (!userSession.isUserInRole(requiredUserRole)) {
            throw new UnauthorizedException(String.format("User %s does not have the (implied) userrole: %s", userSession.getUserID(), requiredUserRole));
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