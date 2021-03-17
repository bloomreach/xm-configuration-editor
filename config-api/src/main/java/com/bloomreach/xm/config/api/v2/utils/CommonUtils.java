package com.bloomreach.xm.config.api.v2.utils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.InternalServerErrorException;

import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.exception.UnauthorizedException;
import com.bloomreach.xm.config.api.v2.model.AbstractComponent;
import com.bloomreach.xm.config.api.v2.model.ConfigApiPermissions;
import com.bloomreach.xm.config.api.v2.model.ManagedComponent;
import com.bloomreach.xm.config.api.v2.model.Page;
import com.bloomreach.xm.config.api.v2.model.StaticComponent;
import com.bloomreach.xm.config.api.v2.rest.ChannelOtherOperationsApiServiceImpl;
import com.google.common.base.Predicates;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.configuration.site.HstSite;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.container.site.CompositeHstSite;
import org.hippoecm.hst.core.internal.PreviewDecorator;
import org.hippoecm.hst.platform.configuration.components.HstComponentConfigurationService;
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

import static org.hippoecm.hst.configuration.components.HstComponentConfiguration.Type.CONTAINER_COMPONENT;

public class CommonUtils {

    public static final String CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER = "xm.config-editor.current-page.viewer";
    public static final String CONFIG_API_PERMISSION_CURRENT_PAGE_EDITOR = "xm.config-editor.current-page.editor";
    public static final String CONFIG_API_PERMISSION_GLOBAL_USER = "xm.config-editor.user";
    public static final String PROP_DESC = "hst:description";
    private static final String SYSTEM_USER = "system";
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

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

    public static void closeSession(final Session session) {
        if (session != null && session.isLive()) {
            session.logout();
        }
    }

    private CommonUtils() {
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

    public static Function<HstComponentConfiguration, AbstractComponent> configToComponentMapper(Session session, Page.PageType type) {
        return componentConfiguration -> {
            AbstractComponent.AbstractComponentBuilder<?, ?> builder = null;

            if (componentConfiguration.getComponentType().equals(HstComponentConfiguration.Type.COMPONENT)) {
                builder = StaticComponent.builder();
                ((StaticComponent.StaticComponentBuilder)builder).components(getComponents(componentConfiguration, type, session))
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

    public static Stream<HstSite> getHstSiteStream(final HstModel hstModel, final String masterChannelId) {
        final PreviewDecorator pd = HstServices.getComponentManager().getComponent(PreviewDecorator.class);
        return pd.decorateVirtualHostsAsPreview(hstModel.getVirtualHosts()).getMountsByHostGroup(getVirtualHostGroupName()).stream()
                .filter(m -> m.getChannel() != null && m.getChannel().getId().equals(masterChannelId))
                .map(Mount::getHstSite);
    }

    public static void ensureUserIsAuthorized(final HttpServletRequest request, final String requiredUserRole, final Session systemSession) throws UnauthorizedException {
        final HippoSession userSession = (HippoSession)getUserSession(request, systemSession);
        if (!userSession.isUserInRole(requiredUserRole)) {
            throw new UnauthorizedException(String.format("User %s does not have the (implied) userrole: %s", userSession.getUserID(), requiredUserRole));
        }
    }

    /**
     * @param httpServletRequest belonging to the config api call
     * @return Session of the user who logged into the CMS. No need to log this session out.
     */
    public static Session getUserSession(final HttpServletRequest httpServletRequest, final Session systemSession) {
        final HttpSession httpSession = httpServletRequest.getSession(false);
        final CmsSessionContext cmsSessionContext = CmsSessionContext.getContext(httpSession);
        final SimpleCredentials credentials = cmsSessionContext.getRepositoryCredentials();
        try {
            return HttpSessionBoundJcrSessionHolder.getOrCreateJcrSession(ChannelOtherOperationsApiServiceImpl.class.getName() + ".session",
                    httpSession, credentials, systemSession.getRepository()::login);
        } catch (RepositoryException e) {
            LOGGER.error("Repo exception", e);
            throw new InternalServerErrorException("Error on getting user session");
        }
    }


    public static ConfigApiPermissions getConfigApiPermissions(final HttpServletRequest request, final Session systemSession) {
        final HippoSession userSession = (HippoSession)getUserSession(request, systemSession);
        return new ConfigApiPermissions(
                userSession.isUserInRole(CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER),
                userSession.isUserInRole(CONFIG_API_PERMISSION_CURRENT_PAGE_EDITOR),
                userSession.isUserInRole(CONFIG_API_PERMISSION_GLOBAL_USER));
    }

    public static boolean isWorkspaceComponent(final HstComponentConfiguration componentConfiguration) {
        return componentConfiguration.getCanonicalStoredLocation().contains("/hst:workspace");
    }

    /**
     * @return The virtual host name of the hst platform which should be the same as the browsed channels'
     * virtual host group
     */
    public static String getVirtualHostGroupName() {
        return RequestContextProvider.get().getResolvedMount().getMount().getVirtualHost().getHostGroupName();
    }

    public static List<AbstractComponent> getComponents(final HstComponentConfiguration config, final Page.PageType type, final Session session) {
        Predicate<HstComponentConfiguration> filter = Predicates.alwaysTrue();
        switch (type) {
            case PAGE:
                filter = componentConfiguration -> componentConfiguration.getCanonicalStoredLocation().contains(componentConfiguration.getParent().getCanonicalStoredLocation());
                break;
            case XPAGE:
                filter = componentConfiguration -> {
                    boolean isXpageLayoutComponent = ((HstComponentConfigurationService)componentConfiguration).isXpageLayoutComponent();
                    boolean isExperiencePageComponent = componentConfiguration.isExperiencePageComponent();
                    return !(isXpageLayoutComponent && isExperiencePageComponent) && (isExperiencePageComponent || isXpageLayoutComponent);
                };
                break;
        }

        List<AbstractComponent> components = config.getChildren().values().stream()
                .filter(filter)
                .map(configToComponentMapper(session, type)).collect(Collectors.toList());

        return components;
    }

}
