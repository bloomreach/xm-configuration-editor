package com.bloomreach.xm.config.api.v2.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.validation.Valid;

import com.bloomreach.xm.config.api.exception.ChannelNotFoundException;
import com.bloomreach.xm.config.api.v2.model.SitemapItem;

import org.hippoecm.hst.configuration.site.HstSite;
import org.hippoecm.repository.util.NodeIterable;

import static com.bloomreach.xm.config.api.v2.utils.CommonUtils.getHstSite;
import static java.util.stream.Collectors.toMap;
import static org.hippoecm.hst.configuration.HstNodeTypes.GENERAL_PROPERTY_PARAMETER_NAMES;
import static org.hippoecm.hst.configuration.HstNodeTypes.GENERAL_PROPERTY_PARAMETER_VALUES;
import static org.hippoecm.hst.configuration.HstNodeTypes.NODENAME_HST_SITEMAP;
import static org.hippoecm.hst.configuration.HstNodeTypes.NODENAME_HST_WORKSPACE;
import static org.hippoecm.hst.configuration.HstNodeTypes.NODETYPE_HST_SITEMAPITEM;
import static org.hippoecm.hst.configuration.HstNodeTypes.SITEMAPITEM_PAGE_TITLE;
import static org.hippoecm.hst.configuration.HstNodeTypes.SITEMAPITEM_PROPERTY_COMPONENTCONFIGURATIONID;
import static org.hippoecm.hst.configuration.HstNodeTypes.SITEMAPITEM_PROPERTY_COMPONENT_CONFIG_MAPPING_NAMES;
import static org.hippoecm.hst.configuration.HstNodeTypes.SITEMAPITEM_PROPERTY_COMPONENT_CONFIG_MAPPING_VALUES;
import static org.hippoecm.hst.configuration.HstNodeTypes.SITEMAPITEM_PROPERTY_RELATIVECONTENTPATH;
import static org.hippoecm.repository.util.JcrUtils.getMultipleStringProperty;
import static org.hippoecm.repository.util.JcrUtils.getStringProperty;


public class ChannelSitemapOperationsApiServiceImpl implements ChannelSitemapOperationsApi {

    private final Session systemSession;

    public ChannelSitemapOperationsApiServiceImpl(final Session session) {
        this.systemSession = session;
    }

    @Override
    public List<SitemapItem> getChannelSitemap(final String channelId) throws ChannelNotFoundException, RepositoryException {
        final HstSite site = getHstSite(channelId);
        final List<SitemapItem> sitemapItems = new ArrayList<>();
        for (final Node sitemapItemNode : new NodeIterable(getSitemapNode(systemSession, site).getNodes())) {
            if (sitemapItemNode.isNodeType(NODETYPE_HST_SITEMAPITEM)) {
                sitemapItems.add(load(sitemapItemNode));
            }
        }
        return sitemapItems;
    }

    protected Node getSitemapNode(@Nonnull final Session session, @Nonnull final HstSite site) throws RepositoryException {
        return session.getNode(site.getConfigurationPath() + "/" + NODENAME_HST_WORKSPACE + "/" + NODENAME_HST_SITEMAP);
    }

    protected SitemapItem load(@Nonnull final Node sitemapItemNode) throws RepositoryException {
        final SitemapItem topLevelSitemapItem = loadData(sitemapItemNode);
        return topLevelSitemapItem;
    }

    private SitemapItem loadData(final Node sitemapItemNode) throws RepositoryException {

        final List<SitemapItem> childSitemapItems = new ArrayList<>();
        for (Node childSitemapItemNode : new NodeIterable(sitemapItemNode.getNodes())) {
            childSitemapItems.add(loadData(childSitemapItemNode));
        }

        final String[] parameterNames = getMultipleStringProperty(sitemapItemNode, GENERAL_PROPERTY_PARAMETER_NAMES, new String[0]);
        final String[] parameterValues = getMultipleStringProperty(sitemapItemNode, GENERAL_PROPERTY_PARAMETER_VALUES, new String[0]);

        final String[] componentConfigurationsMappingNames = getMultipleStringProperty(sitemapItemNode, SITEMAPITEM_PROPERTY_COMPONENT_CONFIG_MAPPING_NAMES, new String[0]);
        final String[] componentConfigurationsMappingValues = getMultipleStringProperty(sitemapItemNode, SITEMAPITEM_PROPERTY_COMPONENT_CONFIG_MAPPING_VALUES, new String[0]);

        return SitemapItem.builder()
                .name(sitemapItemNode.getName())
                .page(getStringProperty(sitemapItemNode, SITEMAPITEM_PROPERTY_COMPONENTCONFIGURATIONID, null))
                .pageTitle(getStringProperty(sitemapItemNode, SITEMAPITEM_PAGE_TITLE, null))
                .relativeContentPath(getStringProperty(sitemapItemNode, SITEMAPITEM_PROPERTY_RELATIVECONTENTPATH, null))
                .parameters(IntStream.range(0, parameterNames.length).boxed().collect(
                        toMap(i -> parameterNames[i], i -> parameterValues[i])))
                .doctypePages(IntStream.range(0, componentConfigurationsMappingNames.length).boxed().collect(
                        toMap(i -> componentConfigurationsMappingNames[i], i -> componentConfigurationsMappingValues[i])))
                .items(childSitemapItems)
                .build();
    }

    @Override
    public List<SitemapItem> putChannelSitemap(final String channelId, @Valid final List<SitemapItem> body) {
        return null;
    }
}

