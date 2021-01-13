package com.bloomreach.xm.config.api.v2.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;

import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.exception.WorkspaceComponentNotFoundException;
import com.bloomreach.xm.config.api.v2.model.Page;

import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.configuration.experiencepage.ExperiencePageService;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.platform.configuration.components.HstComponentConfigurationService;
import org.hippoecm.hst.util.HstRequestUtils;
import org.hippoecm.repository.api.WorkflowException;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;

import static com.bloomreach.xm.config.api.Utils.checkoutCorrectBranch;
import static com.bloomreach.xm.config.api.Utils.getComponentConfig;
import static com.bloomreach.xm.config.api.Utils.getComponents;
import static com.bloomreach.xm.config.api.Utils.getDescription;
import static com.bloomreach.xm.config.api.Utils.getHandle;
import static com.bloomreach.xm.config.api.Utils.getHstSite;
import static com.bloomreach.xm.config.api.Utils.getMount;
import static com.bloomreach.xm.config.api.Utils.getObtainEditableInstanceWorkflow;
import static com.bloomreach.xm.config.api.Utils.getXPageModelFromVariantNode;
import static com.bloomreach.xm.config.api.Utils.getXPageUnpublishedNode;


public class PageDao {


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


    public Page save() {
        return null;
    }


    public Page getPage(final boolean isXpage, final HttpServletRequest request, final String channelId, final String pageName, final Session userSession) {
        return isXpage ? getXPage(request, channelId, pageName, userSession) : getDefaultPage(channelId, pageName, userSession);
    }

    private Page getDefaultPage(final String channelId, final String pageName, final Session userSession) {
        Page page = null;
        try {
            final HstComponentConfiguration config = getComponentConfig(getHstSite(channelId), pageName);
            page = createPageFromConfig(config, Page.PageType.PAGE, userSession);
        } catch (WorkspaceComponentNotFoundException e) {
            e.printStackTrace();
        } catch (ChannelNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return page;
    }

    private Page getXPage(final HttpServletRequest request, final String channelId, final String pageName, final Session userSession) {
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
        } catch (WorkflowException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (ChannelNotFoundException e) {
            e.printStackTrace();
        }
        return page;
    }
}
