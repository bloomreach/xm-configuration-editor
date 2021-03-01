package com.bloomreach.xm.config.api;

import com.bloomreach.xm.config.api.exception.*;
import com.bloomreach.xm.config.api.v2.rest.ChannelFlexPageOperationsApiServiceImpl;
import com.bloomreach.xm.config.api.v2.rest.ChannelOtherOperationsApiServiceImpl;
import com.bloomreach.xm.config.api.v2.rest.ChannelSitemapOperationsApiServiceImpl;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.onehippo.repository.jaxrs.CXFRepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsService;
import org.onehippo.repository.jaxrs.api.ManagedUserSessionInvoker;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class ConfigApiModule extends AbstractReconfigurableDaemonModule {

    private static final String ENDPOINT = "/config";

    @Override
    protected void doConfigure(Node node) throws RepositoryException {
    }

    @Override
    protected void doInitialize(Session session) throws RepositoryException {
        RepositoryJaxrsService.addEndpoint(
                new CXFRepositoryJaxrsEndpoint(ENDPOINT)
                        .invoker(new ManagedUserSessionInvoker(session))
                        .singleton(new ChannelFlexPageOperationsApiServiceImpl(session))
                        .singleton(new ChannelOtherOperationsApiServiceImpl(session))
                        .singleton(new ChannelSitemapOperationsApiServiceImpl(session))
                        .singleton(new JacksonJsonProvider())
                        //exception mappers
                        .singleton(new ChannelNotFoundExceptionMapper())
                        .singleton(new WorkspaceComponentNotFoundExceptionMapper())
                        .singleton(new InternalServerErrorExceptionMapper())
                        .singleton(new PageLockedExceptionMapper())
                        .singleton(new UnauthorizedExceptionMapper()));
    }

    @Override
    protected void doShutdown() {
        RepositoryJaxrsService.removeEndpoint(ENDPOINT);
    }


}