/*
 *  Copyright 2024 Bloomreach
 */
package com.bloomreach.xm.config.api.v2.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import jakarta.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.bloomreach.xm.config.api.v2.model.SitemapItem;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.configuration.site.HstSite;
import org.hippoecm.repository.util.NodeIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
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

public class SiteMapDao {

    private static final Logger log = LoggerFactory.getLogger(SiteMapDao.class);
    private final Session systemSession;


    public SiteMapDao(final Session systemSession) {
        this.systemSession = systemSession;
    }

    protected Node getSitemapNode(@Nonnull final Session session, @Nonnull final HstSite site) throws RepositoryException {
        return session.getNode(site.getConfigurationPath() + "/" + NODENAME_HST_WORKSPACE + "/" + NODENAME_HST_SITEMAP);
    }

    public void save(final HstSite site, final List<SitemapItem> siteMap) {
        try {
            final Node sitemapNode = getSitemapNode(systemSession, site);
            for (SitemapItem childItem : emptyIfNull(siteMap)) {
                final String sitemapItemName = childItem.getName();
                if (sitemapNode.hasNode(sitemapItemName)) {
                    sitemapNode.getNode(sitemapItemName).remove();
                }
                final Node sitemapItemNode = sitemapNode.addNode(sitemapItemName, NODETYPE_HST_SITEMAPITEM);
                save(childItem, sitemapItemNode);
            }
        } catch (RepositoryException e) {
            log.error("An exception occurred while attempting to save.", e);
        }

    }

    protected void save(final SitemapItem sitemapItem, final Node sitemapItemNode) throws RepositoryException {
        if (StringUtils.isNotBlank(sitemapItem.getPage())) {
            sitemapItemNode.setProperty(
                    SITEMAPITEM_PROPERTY_COMPONENTCONFIGURATIONID,
                    sitemapItem.getPage());
        }
        if (StringUtils.isNotBlank(sitemapItem.getPageTitle())) {
            sitemapItemNode.setProperty(SITEMAPITEM_PAGE_TITLE, sitemapItem.getPageTitle());
        }
        if (StringUtils.isNotBlank(sitemapItem.getRelativeContentPath())) {
            sitemapItemNode.setProperty(SITEMAPITEM_PROPERTY_RELATIVECONTENTPATH, sitemapItem.getRelativeContentPath());
        }
        if (MapUtils.isNotEmpty(sitemapItem.getParameters())) {
            sitemapItemNode.setProperty(GENERAL_PROPERTY_PARAMETER_NAMES, sitemapItem.getParameters().keySet().toArray(new String[0]));
            sitemapItemNode.setProperty(GENERAL_PROPERTY_PARAMETER_VALUES, sitemapItem.getParameters().values().toArray(new String[0]));
        }
        if (MapUtils.isNotEmpty(sitemapItem.getDoctypePages())) {
            sitemapItemNode.setProperty(SITEMAPITEM_PROPERTY_COMPONENT_CONFIG_MAPPING_NAMES, sitemapItem.getDoctypePages().keySet().toArray(new String[0]));
            sitemapItemNode.setProperty(SITEMAPITEM_PROPERTY_COMPONENT_CONFIG_MAPPING_VALUES,
                    sitemapItem.getDoctypePages().values().stream()
                            .toArray(String[]::new));
        }

        //process children
        for (SitemapItem childItem : emptyIfNull(sitemapItem.getItems())) {
            final String childItemName = childItem.getName();
            final Node childItemNode = sitemapItemNode.addNode(childItemName, NODETYPE_HST_SITEMAPITEM);
            save(childItem, childItemNode);
        }
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

    public List<SitemapItem> get(final HstSite site) {
        final List<SitemapItem> sitemapItems = new ArrayList<>();
        try {
            for (final Node sitemapItemNode : new NodeIterable(getSitemapNode(systemSession, site).getNodes())) {
                if (sitemapItemNode.isNodeType(NODETYPE_HST_SITEMAPITEM)) {
                    sitemapItems.add(load(sitemapItemNode));
                }
            }
        } catch (RepositoryException e) {
            log.error("An exception occurred while attempting to get.", e);
        }
        return sitemapItems;
    }
}
