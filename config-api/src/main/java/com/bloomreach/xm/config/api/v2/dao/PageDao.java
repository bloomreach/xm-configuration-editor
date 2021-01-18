package com.bloomreach.xm.config.api.v2.dao;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;

import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.exception.PageLockedException;
import com.bloomreach.xm.config.api.exception.WorkspaceComponentNotFoundException;
import com.bloomreach.xm.config.api.v2.model.AbstractComponent;
import com.bloomreach.xm.config.api.v2.model.ManagedComponent;
import com.bloomreach.xm.config.api.v2.model.Page;
import com.bloomreach.xm.config.api.v2.model.StaticComponent;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.configuration.experiencepage.ExperiencePageService;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.configuration.site.HstSite;
import org.hippoecm.hst.pagecomposer.jaxrs.services.exceptions.ClientException;
import org.hippoecm.hst.pagecomposer.jaxrs.services.helpers.LockHelper;
import org.hippoecm.hst.platform.configuration.components.HstComponentConfigurationService;
import org.hippoecm.hst.util.HstRequestUtils;
import org.hippoecm.repository.api.WorkflowException;
import org.hippoecm.repository.util.JcrUtils;
import org.hippoecm.repository.util.NodeIterable;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.checkoutCorrectBranch;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.getComponentConfig;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.getComponents;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.getDescription;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.getHandle;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.getHstSite;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.getMount;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.getObtainEditableInstanceWorkflow;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.getTemporaryStorageNode;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.getUserSession;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.getXPageModelFromVariantNode;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.getXPageTemplate;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.getXPageUnpublishedNode;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.isXPage;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.renameNode;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.setAbstractComponentPropsOnNode;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.setManagedComponentPropsOnNode;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.setPagePropsOnNode;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.storeContainerNodesTemporarily;
import static com.bloomreach.xm.config.api.v2.utils.FlexPageUtils.unlockQuietly;
import static org.hippoecm.hst.configuration.components.HstComponentConfiguration.Type.CONTAINER_COMPONENT;


public class PageDao {

    public static final String STATIC_COMPONENT_LOCATION = "/hippo:configuration/hippo:modules/configapi/hippo:moduleconfig/static";
    private static final Logger LOGGER = LoggerFactory.getLogger(PageDao.class);
    private static final String MANAGED_COMPONENT_LOCATION = "/hippo:configuration/hippo:modules/configapi/hippo:moduleconfig/managed";

    private final Session systemSession;
    private final LockHelper lockHelper;


    public PageDao(final Session systemSession, final LockHelper lockHelper) {
        this.systemSession = systemSession;
        this.lockHelper = lockHelper;
    }

    private static void createNodeFromComponent(final Node storageNode, final Session session, final AbstractComponent component, final String parentNodePath, final String userId) throws RepositoryException {
        if (component instanceof ManagedComponent) {
            final ManagedComponent managedComponent = (ManagedComponent)component;
            final String newNodePath = parentNodePath + "/" + managedComponent.getName();
            Node managedComponentNode;
            if (!session.nodeExists(newNodePath)) {
                managedComponentNode = JcrUtils.copy(session, MANAGED_COMPONENT_LOCATION, newNodePath);
            } else {
                managedComponentNode = session.getNode(newNodePath);
            }
            //leave a lock on the container node to let hst know that there are "unpublished changes".
            //aka the yellow exclamation mark
            new LockHelper().acquireLock(managedComponentNode, userId, 0);
            setManagedComponentPropsOnNode(managedComponentNode, managedComponent);
            //have to rename the managedComponentNode to uuid so they are always unique
            renameNode(managedComponentNode, UUID.randomUUID().toString().toLowerCase());
            //check storagenode if previously stored container exists there.
            //If so, copy over the container item in it
            if (storageNode.hasNode(managedComponent.getName())) {
                final Node previouslyStoredNode = storageNode.getNode(managedComponent.getName());
                for (Node containerItemComponentNode : new NodeIterable(previouslyStoredNode.getNodes())) {
                    JcrUtils.copy(session, containerItemComponentNode.getPath(), managedComponentNode.getPath() + "/" + containerItemComponentNode.getName());
                }
            }
        } else if (component instanceof StaticComponent) {
            final StaticComponent staticComponent = (StaticComponent)component;
            String newNodePath = parentNodePath + "/" + component.getName();
            int componentCount = 0;
            Node staticComponentNode;
            while (session.nodeExists(newNodePath)) {
                componentCount++;
                newNodePath = parentNodePath + "/" + StringUtils.substringBeforeLast(staticComponent.getName(), "-") + "-" + componentCount;
            }
            staticComponentNode = JcrUtils.copy(session, STATIC_COMPONENT_LOCATION, newNodePath);
            setAbstractComponentPropsOnNode(staticComponentNode, staticComponent);
            if (staticComponent.getComponents() != null && !staticComponent.getComponents().isEmpty()) {
                for (AbstractComponent childComponent : staticComponent.getComponents()) {
                    createNodeFromComponent(storageNode, session, childComponent, newNodePath, userId);
                }
            }

        }
    }

    /**
     * Builds a PaaS model page hierarchy from a given SaaS page model
     *
     * @param session        request-scoped, system-privileged session to be used
     * @param page           Page model to be built in jcr
     * @param parentNodePath Hst component node path of the workspace page being manipulated
     * @param userId         of the currently logged-in cms user. Used for leaving locks on containers
     * @throws RepositoryException
     */
    public void updateNodeFromPage(final HstSite hstSite, final Page page, final String parentNodePath, final Session session, final String userId) throws RepositoryException {
        //create a temporary storage node which will carry container nodes of the previous page structure.
        final Node temporaryStorageNode = getTemporaryStorageNode(session);

        final Node pageNode = session.getNode(parentNodePath);
        setPagePropsOnNode(pageNode, page);
        //remove all children nodes from the hst component page node but remember the container nodes
        //container nodes are to be put back according to the incoming page model structure
        storeContainerNodesTemporarily(pageNode, temporaryStorageNode);

        final List<String> sealedComponents = page.getType().equals(Page.PageType.XPAGE.getValue()) ? getXPageTemplate(hstSite, pageNode).getChildren()
                .values().stream()
                .flatMap(componentConfiguration -> componentConfiguration.getChildren().values().stream())
                .filter(componentConfiguration -> CONTAINER_COMPONENT.equals(componentConfiguration.getComponentType()))
                .map(componentConfiguration -> componentConfiguration.getHippoIdentifier())
                .collect(Collectors.toList()) :
                Collections.emptyList();

        //remove all components but the ones coming from the xpage template
        for (Node childNode : new NodeIterable(pageNode.getNodes())) {
            if (!sealedComponents.contains(childNode.getName())) {
                childNode.remove();
            }
        }

        for (AbstractComponent component : page.getComponents()) {
            createNodeFromComponent(temporaryStorageNode, session, component, parentNodePath, userId);
        }
//        updateNodeFromComponent(temporaryStorageNode, session, page, parentNodePath, userId);

        temporaryStorageNode.remove();
    }

    public void save(Page page, String path, HttpServletRequest request, String channelId, Session persistableSession) throws ChannelNotFoundException, WorkspaceComponentNotFoundException, PageLockedException {
        final Session userSession = getUserSession(request, systemSession);
        final HstSite hstSite = getHstSite(channelId);

        switch (Page.PageType.fromValue(page.getType())) {
            case XPAGE:
                try {
                    final Node xPageUnpublishedNode = getXPageUnpublishedNode(channelId, path, userSession);
                    final Node xpage = getXPageModelFromVariantNode(xPageUnpublishedNode);

                    final String cmsSessionActiveBranchId = HstRequestUtils.getCmsSessionActiveBranchId(request);
                    final Node handle = getHandle(xPageUnpublishedNode);

                    final DocumentWorkflow documentWorkflow = getObtainEditableInstanceWorkflow(userSession, handle.getIdentifier(), cmsSessionActiveBranchId);
                    final Session workflowSession = documentWorkflow.getWorkflowContext().getInternalWorkflowSession();

                    updateNodeFromPage(hstSite, page, xpage.getPath(), workflowSession, userSession.getUserID());

                    documentWorkflow.saveUnpublished();
                } catch (RepositoryException | WorkflowException e) {
                    LOGGER.error(e.getMessage());
                    throw new InternalServerErrorException("Internal server error", e);
                }
                break;
            case PAGE:
                Node configNode = null;
                try {
                    final HstComponentConfiguration componentConfig = getComponentConfig(hstSite, path);
                    configNode = JcrUtils.getNodeIfExists(componentConfig.getCanonicalStoredLocation(), persistableSession);
                    lockHelper.acquireLock(configNode, userSession.getUserID(), 0);
                    updateNodeFromPage(hstSite, page, componentConfig.getCanonicalStoredLocation(), persistableSession, userSession.getUserID());
                    persistableSession.save();
                } catch (ClientException ex) {
                    LOGGER.error(ex.getMessage());
                    throw new PageLockedException("Cannot update page hierarchy as it's locked by " + ex.getParameterMap().get("lockedBy"));
                } catch (RepositoryException ex) {
                    LOGGER.error(ex.getMessage());
                    unlockQuietly(configNode, lockHelper); //we only want to unlock here.Publishing changes in EM will release the lock
                    throw new InternalServerErrorException("Internal server error", ex);
                }
                break;
        }
    }

    private Page createPageFromConfig(final HstComponentConfiguration config, Page.PageType type, final Session session) throws RepositoryException {
        final String referenceComponent = ((HstComponentConfigurationService)config).getReferenceComponent();
        final String ext = referenceComponent != null ? referenceComponent.substring(referenceComponent.lastIndexOf('/') + 1) : null;
        final Page page = Page.builder()
                .name(config.getName())
                .description(getDescription(config, session))
                .parameters(config.getParameters())
                .type(type)
                ._extends(ext)
                .components(getComponents(config, type, session))
                .build();
        return page;
    }

    public Page getPage(final HttpServletRequest request, final String channelId, final String pageName) throws ChannelNotFoundException, WorkspaceComponentNotFoundException {
        final Session userSession = getUserSession(request, systemSession);
        final boolean isXpage = isXPage(channelId, pageName, userSession);
        return isXpage ? getXPage(request, channelId, pageName, userSession) : getLandingPage(channelId, pageName, userSession);
    }

    private Page getLandingPage(final String channelId, final String pageName, final Session userSession) throws ChannelNotFoundException, WorkspaceComponentNotFoundException {
        Page page = null;
        try {
            final HstComponentConfiguration config = getComponentConfig(getHstSite(channelId), pageName);
            page = createPageFromConfig(config, Page.PageType.PAGE, userSession);
        } catch (RepositoryException e) {
            LOGGER.error("error while trying to retrieve landing page", e);
            throw new InternalServerErrorException("Internal server error: " + e.getLocalizedMessage(), e);
        }
        return page;
    }

    private Page getXPage(final HttpServletRequest request, final String channelId, final String pageName, final Session userSession) throws ChannelNotFoundException {
        Page page = null;
        try {
            final Node xPageUnpublishedNode = getXPageUnpublishedNode(channelId, pageName, userSession);
            final Node handle = getHandle(xPageUnpublishedNode);

            final String cmsSessionActiveBranchId = HstRequestUtils.getCmsSessionActiveBranchId(request);

            final DocumentWorkflow documentWorkflow = getObtainEditableInstanceWorkflow(userSession, handle.getIdentifier(), cmsSessionActiveBranchId);
            checkoutCorrectBranch(documentWorkflow, cmsSessionActiveBranchId);

            final Node xPageModelFromVariantNode = getXPageModelFromVariantNode(xPageUnpublishedNode);
            // this is a node of type hst:component, fetch the hst component configuration for it
            final ExperiencePageService experiencePageService = HippoServiceRegistry.getService(ExperiencePageService.class);

            final Mount mount = getMount(channelId);
            final HstComponentConfiguration config = experiencePageService.loadExperiencePage(xPageModelFromVariantNode, mount.getHstSite(),
                    this.getClass().getClassLoader());

            page = createPageFromConfig(config, Page.PageType.XPAGE, userSession);
        } catch (WorkflowException | RepositoryException e) {
            LOGGER.error("error while trying to retrieve x page", e);
            throw new InternalServerErrorException("Internal server error: " + e.getLocalizedMessage(), e);
        }
        return page;
    }




}
