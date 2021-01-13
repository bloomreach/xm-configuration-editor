package com.bloomreach.xm.config.api;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.ws.rs.InternalServerErrorException;

import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.exception.WorkspaceComponentNotFoundException;
import com.bloomreach.xm.config.api.v2.model.AbstractComponent;
import com.bloomreach.xm.config.api.v2.model.ManagedComponent;
import com.bloomreach.xm.config.api.v2.model.Page;
import com.bloomreach.xm.config.api.v2.model.StaticComponent;
import com.bloomreach.xm.config.api.v2.rest.ChannelCurrentPageOperationsApiServiceImpl;
import com.google.common.base.Predicates;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.configuration.HstNodeTypes;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.configuration.site.HstSite;
import org.hippoecm.hst.configuration.sitemap.HstSiteMap;
import org.hippoecm.hst.configuration.sitemap.HstSiteMapItem;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.container.site.CompositeHstSite;
import org.hippoecm.hst.core.internal.PreviewDecorator;
import org.hippoecm.hst.pagecomposer.jaxrs.services.experiencepage.XPageUtils;
import org.hippoecm.hst.platform.configuration.components.HstComponentConfigurationService;
import org.hippoecm.hst.platform.model.HstModel;
import org.hippoecm.hst.platform.model.HstModelRegistry;
import org.hippoecm.hst.site.HstServices;
import org.hippoecm.hst.site.request.ResolvedMountImpl;
import org.hippoecm.hst.site.request.ResolvedSiteMapItemImpl;
import org.hippoecm.repository.api.DocumentWorkflowAction;
import org.hippoecm.repository.api.HippoWorkspace;
import org.hippoecm.repository.api.WorkflowException;
import org.hippoecm.repository.api.WorkflowManager;
import org.hippoecm.repository.util.JcrUtils;
import org.hippoecm.repository.util.WorkflowUtils;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.cms7.services.hst.Channel;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bloomreach.xm.config.api.v2.model.Page.PageType.ABSTRACT;
import static org.hippoecm.hst.configuration.HstNodeTypes.NODENAME_HST_XPAGE;
import static org.hippoecm.hst.configuration.components.HstComponentConfiguration.Type.CONTAINER_COMPONENT;
import static org.hippoecm.repository.api.HippoNodeType.HIPPO_PROPERTY_BRANCH_ID;
import static org.hippoecm.repository.util.JcrUtils.getStringProperty;
import static org.hippoecm.repository.util.WorkflowUtils.Variant.UNPUBLISHED;
import static org.onehippo.repository.branch.BranchConstants.MASTER_BRANCH_ID;

public class Utils {

    public static final String CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER = "xm.config-editor.current-page.viewer";
    public static final String CONFIG_API_PERMISSION_CURRENT_PAGE_EDITOR = "xm.config-editor.current-page.editor";
    public static final String PROP_DESC = "hst:description";
    private static final String SYSTEM_USER = "system";
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }

    public static HstModelRegistry getHstModelRegistry() {
        HstModelRegistry hstModelRegistry = HippoServiceRegistry.getService(HstModelRegistry.class);
        if (hstModelRegistry == null) {
            LOGGER.info("Cannot create resolve URL's without hstModelRegistry");
            throw new IllegalStateException("Cannot create resolve URL's without hstModelRegistry");
        }
        return hstModelRegistry;
    }

    public static Node getXPageModelFromVariantNode(final Node pageNode) {
        try {
            return pageNode.getNode(NODENAME_HST_XPAGE);
        } catch (RepositoryException e) {
            LOGGER.error("Error while trying to get xpage variant from node", e);
        }
        return null;
    }

    public static Node getXPageUnpublishedNode(String channelId, String path, Session userSession) throws ChannelNotFoundException {
        try {
            final Node xPageHandle = getXPageHandle(channelId, path, userSession);
            final Optional<Node> unpublished = WorkflowUtils.getDocumentVariantNode(xPageHandle, UNPUBLISHED);

            if (!unpublished.isPresent()) {
                throw new WorkflowException("Expected unpublished variant to be present");
            }
            return unpublished.get();
        } catch (WorkflowException e) {
            LOGGER.error("Error while trying to access workflow {} ", e);
        }
        return null;
    }

    public static Node getXPageHandle(String channelId, String path, Session userSession) throws ChannelNotFoundException {
        final HstModelRegistry hstModelRegistry = getHstModelRegistry();

        final Mount mount = getMount(channelId);
        final HstModel platformModel = hstModelRegistry.getHstModel(ChannelCurrentPageOperationsApiServiceImpl.class.getClassLoader());

        try {
            final ResolvedMountImpl resolvedMount = (ResolvedMountImpl)platformModel.getVirtualHosts().matchMount(mount.getVirtualHost().getName(), mount.getMountPath());
            resolvedMount.setMount(mount);
            final ResolvedSiteMapItemImpl resolvedSiteMapItem = (ResolvedSiteMapItemImpl)resolvedMount.matchSiteMapItem("/" + path);

            final Optional<String> relativeContentPath = Optional.ofNullable(resolvedSiteMapItem.getRelativeContentPath());

            if (relativeContentPath.isPresent()) {
                final String contentRoot = mount.getHstSite().getChannel().getContentRoot();
                return userSession.getNode(contentRoot).getNode(resolvedSiteMapItem.getRelativeContentPath());
            }
        } catch (RepositoryException ignore) {
        }
        return null;
    }

    public static boolean isXPage(final String channelId, final String path, final Session userSession) throws ChannelNotFoundException {
        final Node xPageHandle = getXPageHandle(channelId, path, userSession);
        if (xPageHandle != null) {
            return XPageUtils.isXPageDocument(xPageHandle);
        }
        return false;
    }

    public static DocumentWorkflow getObtainEditableInstanceWorkflow(final Session userSession,
                                                                     final String handleId, String branchId) throws RepositoryException, WorkflowException {

        final DocumentWorkflow documentWorkflow = getDocumentWorkflow(userSession, handleId);
        try {
            if (Boolean.FALSE.equals(documentWorkflow.hints(branchId).get(DocumentWorkflowAction.obtainEditableInstance().getAction()))) {
                throw new InternalServerErrorException("Current xpage is locked and not editable!");
            }
        } catch (RemoteException e) {
            LOGGER.error("Exception while checking hints", e);
            throw new WorkflowException(e.getMessage());
        }

        return documentWorkflow;
    }

    public static DocumentWorkflow getDocumentWorkflow(Session userSession, String handleId) throws RepositoryException {
        Node handle = userSession.getNodeByIdentifier(handleId);
        HippoWorkspace workspace = (HippoWorkspace)userSession.getWorkspace();
        WorkflowManager workflowManager = workspace.getWorkflowManager();
        return (DocumentWorkflow)workflowManager.getWorkflow("default", handle);
    }

    public static Node getHandle(final Node node) {
        try {
            if (node.isNodeType("hippo:handle")) {
                return node;
            }
            return getHandle(node.getParent());
        } catch (RepositoryException e) {
            LOGGER.error("Error while trying to get the handle of a document", e);
        }
        return null;
    }

    public static void closeSession(final Session session) {
        if (session != null && session.isLive()) {
            session.logout();
        }
    }

    public static HstComponentConfiguration getComponentConfig(final HstSite hstSite, final String pageName) throws WorkspaceComponentNotFoundException, ChannelNotFoundException {
        final String componentConfigurationId = getHstSiteMapItemFromPath(pageName, hstSite.getSiteMap()).getComponentConfigurationId();
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
    public static boolean isFirstLevelChildOfHstComponents(final HstComponentConfiguration componentConfig) {
        return componentConfig.getId().startsWith("hst:components/") && componentConfig.getId().chars().filter(ch -> ch == '/').count() == 1;
    }

    public static HstSite getHstSite(final String channelId) throws ChannelNotFoundException {
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

    public static Mount getMount(final String channelId) throws ChannelNotFoundException {
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
            return getMountStream(hstModel, masterChannelId)
                    .findFirst()
                    .orElseThrow(() -> new ChannelNotFoundException("Channel with id: " + channelId + " not found"));
        } else {
            return getMountStream(hstModel, channelId)
                    .findFirst()
                    .orElseThrow(() -> new ChannelNotFoundException("Channel with id: " + channelId + " not found"));
        }
    }

    public static Stream<HstSite> getHstSiteStream(final HstModel hstModel, final String masterChannelId) {
        final PreviewDecorator pd = HstServices.getComponentManager().getComponent(PreviewDecorator.class);
        return pd.decorateVirtualHostsAsPreview(hstModel.getVirtualHosts()).getMountsByHostGroup(getVirtualHostGroupName()).stream()
                .filter(m -> m.getChannel() != null && m.getChannel().getId().equals(masterChannelId))
                .map(Mount::getHstSite);
    }

    public static Stream<Mount> getMountStream(final HstModel hstModel, final String masterChannelId) {
        final PreviewDecorator pd = HstServices.getComponentManager().getComponent(PreviewDecorator.class);
        return pd.decorateVirtualHostsAsPreview(hstModel.getVirtualHosts()).getMountsByHostGroup(getVirtualHostGroupName()).stream()
                .filter(m -> m.getChannel() != null && m.getChannel().getId().equals(masterChannelId));
    }

    /**
     * @return The virtual host name of the hst platform which should be the same as the browsed channels'
     * virtual host group
     */
    public static String getVirtualHostGroupName() {
        return RequestContextProvider.get().getResolvedMount().getMount().getVirtualHost().getHostGroupName();
    }

    public static boolean isWorkspaceComponent(final HstComponentConfiguration componentConfiguration) {
        return componentConfiguration.getCanonicalStoredLocation().contains("/hst:workspace");
    }

    public static HstComponentConfiguration getXPageTemplate(final HstSite hstSite, final Node xpageNode) throws RepositoryException {
        if (xpageNode.isNodeType(NODENAME_HST_XPAGE) && xpageNode.hasProperty(HstNodeTypes.XPAGE_PROPERTY_PAGEREF)) {
            return getXPageTemplate(hstSite, xpageNode.getProperty(HstNodeTypes.XPAGE_PROPERTY_PAGEREF).getString());
        }
        return null;
    }

    public static HstComponentConfiguration getXPageTemplate(final HstSite hstSite, final String ref) {
        return hstSite.getComponentsConfiguration().getXPages().get(ref);
    }

    public static HstSiteMapItem getHstSiteMapItemFromPath(final String path, final HstSiteMap siteMap) {
        if (path.contains("/")) {
            final List<String> segments = Arrays.asList(StringUtils.split(path, "/"));
            if (segments != null && !segments.isEmpty()) {
                HstSiteMapItem foundSiteMapItem = null;
                for (String segment : segments) {
                    if (foundSiteMapItem == null) {
                        foundSiteMapItem = siteMap.getSiteMapItem(segment);
                        continue;
                    }
                    foundSiteMapItem = foundSiteMapItem.getChild(segment);
                }
                return foundSiteMapItem;
            }
        }
        return siteMap.getSiteMapItem(path);
    }

    /**
     * optionally checks out the right branch if the branch to be changed is in version history and not the unpublished
     * variant
     * Returns TRUE if a branch was checked out, FALSE if it wasn't needed
     */
    public static boolean checkoutCorrectBranch(final DocumentWorkflow documentWorkflow,
                                                final String selectedBranchId) throws WorkflowException, RepositoryException {

        try {

            if (!Boolean.TRUE.equals(documentWorkflow.hints(selectedBranchId).get("checkoutBranch"))) {
                // there is only master branch, so no need to check out a branch
                return false;
            }
        } catch (RemoteException | RepositoryException e) {
            throw new WorkflowException(e.getMessage());
        }

        final String targetBranchId = selectedBranchId;

        // validate that the targetBranchId is the SAME as the branch ID belonging to the request identifier node (this
        // can be a workspace container item or a container item in version history! This check is to avoid that for
        // some reason, there is a mismatch between the container item branch and the CMS session context branch

        final Optional<Node> unpublished = WorkflowUtils.getDocumentVariantNode(documentWorkflow.getNode(), UNPUBLISHED);

        if (!unpublished.isPresent()) {
            throw new WorkflowException("Expected unpublished variant to be present");
        }

        final String currentBranchId = getStringProperty(unpublished.get(), HIPPO_PROPERTY_BRANCH_ID, MASTER_BRANCH_ID);
        if (currentBranchId.equals(targetBranchId)) {
            LOGGER.debug(String.format("target branch '%s' is current unpublished, no need to invoke checkoutBranch workflow",
                    targetBranchId));
            return false;
        }

        documentWorkflow.checkoutBranch(targetBranchId);
        return true;
    }

    /**
     * @param componentConfig PaaS component to read the hst:descrtiption property from.
     * @param session         for lookup of the hst:descrtiption property. This field is not part of the api
     */
    public static String getDescription(final HstComponentConfiguration componentConfig, final Session session) {
        try {
            final Node configNode = JcrUtils.getNodeIfExists(componentConfig.getCanonicalStoredLocation(), session);
            if (configNode != null && configNode.hasProperty(PROP_DESC)) {
                return configNode.getProperty(PROP_DESC).getString();
            }
        } catch (RepositoryException ignored) {
        }
        return null;
    }

    public static Function<HstComponentConfiguration, AbstractComponent> configToComponentMapper(Session session) {
        return componentConfiguration -> {
            AbstractComponent.AbstractComponentBuilder<?, ?> builder = null;

            if (componentConfiguration.getComponentType().equals(HstComponentConfiguration.Type.COMPONENT)) {
                builder = StaticComponent.builder();
                ((StaticComponent.StaticComponentBuilder)builder).components(getComponents(componentConfiguration, ABSTRACT, session))
                        .type(AbstractComponent.TypeEnum.STATIC);

            } else if (componentConfiguration.getComponentType().equals(CONTAINER_COMPONENT)) {
                builder = ManagedComponent.builder()
                        .label(componentConfiguration.getLabel())
                        .type(AbstractComponent.TypeEnum.MANAGED);
            }

            AbstractComponent component = builder
                    .name(componentConfiguration.getName())
                    .description(getDescription(componentConfiguration, session))
                    .parameters(componentConfiguration.getParameters())
                    .xtype(AbstractComponent.XtypeEnum.fromValue(componentConfiguration.getXType()))
                    .build();

            return component;
        };
    }

    public static List<AbstractComponent> getComponents(final HstComponentConfiguration config, final Page.PageType type, final Session session) {
        Predicate<HstComponentConfiguration> filter = Predicates.alwaysTrue();
        switch (type) {
            case PAGE:
                filter = componentConfiguration -> componentConfiguration.getCanonicalStoredLocation().contains(componentConfiguration.getParent().getCanonicalStoredLocation());
                break;
            case XPAGE:
                filter = componentConfiguration -> ((HstComponentConfigurationService)componentConfiguration).isXpageLayoutComponent() || componentConfiguration.isExperiencePageComponent();
                break;
        }

        List<AbstractComponent> components = config.getChildren().values().stream()
                .filter(filter)
                .map(configToComponentMapper(session)).collect(Collectors.toList());

        return components;
    }

    /**
     * @return request-scoped {@link Session} session with system privileges
     * @throws InternalServerErrorException
     */
    public static Session getImpersonatedSession(final Session systemSession) throws InternalServerErrorException {
        try {
            return systemSession.impersonate(new SimpleCredentials(SYSTEM_USER, new char[]{}));
        } catch (RepositoryException ex) {
            LOGGER.error(ex.getMessage());
            throw new InternalServerErrorException("Internal server error", ex);
        }
    }

}
