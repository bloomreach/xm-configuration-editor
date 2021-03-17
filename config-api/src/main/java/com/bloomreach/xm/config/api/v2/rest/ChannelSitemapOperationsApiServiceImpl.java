package com.bloomreach.xm.config.api.v2.rest;

import java.net.URI;
import java.util.List;

import javax.jcr.Session;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.v2.dao.SiteMapDao;
import com.bloomreach.xm.config.api.v2.model.SitemapItem;

import static com.bloomreach.xm.config.api.v2.utils.CommonUtils.closeSession;
import static com.bloomreach.xm.config.api.v2.utils.CommonUtils.getHstSite;
import static com.bloomreach.xm.config.api.v2.utils.CommonUtils.getImpersonatedSession;


public class ChannelSitemapOperationsApiServiceImpl implements ChannelSitemapOperationsApi {

    private final Session systemSession;
    private final SiteMapDao siteMapDao;

    public ChannelSitemapOperationsApiServiceImpl(final Session session) {
        this.systemSession = session;
        this.siteMapDao = new SiteMapDao(systemSession);
    }

    public List<SitemapItem> getChannelSitemap(final String channelId) throws ChannelNotFoundException {
        return siteMapDao.get(getHstSite(channelId));
    }


    @Override
    public Response putChannelSitemap(final String channelId, @Valid final List<SitemapItem> body) throws ChannelNotFoundException {
        final Session session = getImpersonatedSession(systemSession);
        try {
            this.siteMapDao.save(getHstSite(channelId), body);
            return Response.created(URI.create(String.format("/channels/%s/sitemap", channelId))).build();
        } finally {
            closeSession(session);
        }
    }
}

