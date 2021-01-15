package com.bloomreach.xm.config.api.v2.rest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;

import com.bloomreach.xm.config.api.Utils;
import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.exception.UnauthorizedException;
import com.bloomreach.xm.config.api.v2.model.AbstractComponent;
import com.bloomreach.xm.config.api.v2.model.Page;

import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bloomreach.xm.config.api.Utils.CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER;
import static com.bloomreach.xm.config.api.Utils.configToComponentMapper;
import static com.bloomreach.xm.config.api.Utils.ensureUserIsAuthorized;
import static com.bloomreach.xm.config.api.Utils.getConfigApiPermissions;
import static com.bloomreach.xm.config.api.Utils.getHstSite;


public class ChannelOtherOperationsApiServiceImpl implements ChannelOtherOperationsApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelOtherOperationsApiServiceImpl.class);

    private final Session systemSession;

    public ChannelOtherOperationsApiServiceImpl(final Session session) {
        this.systemSession = session;
    }

    public Response hasPermission(final HttpServletRequest request) throws InternalServerErrorException {
        return Response.ok(getConfigApiPermissions(request, systemSession)).build();
    }

    public Response getAllComponents(final HttpServletRequest request, String channelId) throws ChannelNotFoundException, UnauthorizedException {
        ensureUserIsAuthorized(request, CONFIG_API_PERMISSION_CURRENT_PAGE_VIEWER, systemSession);
        final Map<String, HstComponentConfiguration> componentConfigurations = getHstSite(channelId).getComponentsConfiguration().getComponentConfigurations();
        final List<AbstractComponent> components = componentConfigurations.values().stream()
                .filter(Utils::isFirstLevelChildOfHstComponents)
                .map(configToComponentMapper(systemSession, Page.PageType.ABSTRACT))
                .collect(Collectors.toList());
        return Response.ok(components).build();
    }

}

