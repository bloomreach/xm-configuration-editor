package com.bloomreach.xm.config.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.bloomreach.xm.config.api.model.AbstractComponent;
import com.bloomreach.xm.config.api.model.BasePageComponent;
import com.bloomreach.xm.config.api.model.ManagedComponent;
import com.bloomreach.xm.config.api.model.Page;
import com.bloomreach.xm.config.api.model.StaticComponent;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.value.StringValue;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.configuration.site.HstSite;
import org.hippoecm.hst.pagecomposer.jaxrs.services.helpers.LockHelper;
import org.hippoecm.repository.util.JcrUtils;
import org.hippoecm.repository.util.NodeIterable;
import org.onehippo.repository.util.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hippoecm.hst.configuration.HstNodeTypes.GENERAL_PROPERTY_PARAMETER_NAMES;
import static org.hippoecm.hst.configuration.HstNodeTypes.GENERAL_PROPERTY_PARAMETER_VALUES;
import static org.hippoecm.hst.configuration.HstNodeTypes.NODETYPE_HST_CONTAINERCOMPONENT;
import static org.hippoecm.hst.configuration.components.HstComponentConfiguration.Type.CONTAINER_COMPONENT;

public class Builder {

    public static final String STATIC_COMPONENT_LOCATION = "/hippo:configuration/hippo:modules/configapi/hippo:moduleconfig/static";
    public static final String COMPONENT_REGISTRTY_RELPATH = "/hst:workspace/hst:components";
    public static final String PROP_DESC = "hst:description";
    private static final Logger log = LoggerFactory.getLogger(Builder.class);
    //component node locations used as templates.
    private static final String MANAGED_COMPONENT_LOCATION = "/hippo:configuration/hippo:modules/configapi/hippo:moduleconfig/managed";
    private static final String PROP_LABEL = "hst:label";
    private static final String PROP_XTYPE = "hst:xtype";

    /**
     * Builds a PaaS model page hierarchy from a given SaaS page model
     *
     * @param session        request-scoped, system-privileged session to be used
     * @param component      Page model to be built in jcr
     * @param parentNodePath Hst component node path of the workspace page being manipulated
     * @param userId         of the currently logged-in cms user. Used for leaving locks on containers
     * @throws RepositoryException
     */
    public static void buildPageNode(final Session session, final BasePageComponent component, final String parentNodePath, final String userId) throws RepositoryException {
        //create a temporary storage node which will carry container nodes of the previous page structure.
        final Node storageNode = getTemporaryStorageNode(session);
        buildNodeInternal(storageNode, session, component, parentNodePath, userId);
        storageNode.remove();
    }

    public static void buildXPageNode(final Session session, final HstComponentConfiguration xpageTemplate, final BasePageComponent component, final String parentNodePath, final String userId) throws RepositoryException {
        //create a temporary storage node which will carry container nodes of the previous page structure.
        final Node storageNode = getTemporaryStorageNode(session);
        log.info("created temporary storage node" + storageNode.getPath());
        buildXNodeInternal(storageNode, xpageTemplate, session, component, parentNodePath, userId);
        log.info("removing temporary storage node" + storageNode.getPath());
        storageNode.remove();

    }

    public static void buildComponentNode(final Session session, final AbstractComponent component, final HstSite hstSite, final String userId) throws RepositoryException {
        //check if such a component already exists. If so, we have to "update" (recreate with same state) it.
        final String componentRegistryPath = hstSite.getConfigurationPath() + COMPONENT_REGISTRTY_RELPATH;
        final Node componentRegistryNode = session.getNode(componentRegistryPath);
        final Node storageNode = getTemporaryStorageNode(session);
        for (Node componentNode : new NodeIterable(componentRegistryNode.getNodes())) {
            if (componentNode.getName().equals(component.getName())) {
                storeContainerNodesTemporarily(componentNode, storageNode);
                componentNode.remove();
            }
        }
        if (component instanceof StaticComponent) {
            final StaticComponent staticComponent = (StaticComponent)component;
            final Node copiedNode = JcrUtils.copy(session, STATIC_COMPONENT_LOCATION, componentRegistryPath + "/" + staticComponent.getName());
            setBasePagePropsOnNode(copiedNode, staticComponent);
            for (AbstractComponent childComp : staticComponent.getComponents()) {
                buildNodeInternal(storageNode, session, childComp, copiedNode.getPath(), userId);
            }
        } else if (component instanceof ManagedComponent) {
            final ManagedComponent managedComponent = (ManagedComponent)component;
            final Node managedComponentNode = JcrUtils.copy(session, MANAGED_COMPONENT_LOCATION, componentRegistryPath + "/" + component.getName());
            setManagedComponentPropsOnNode(managedComponentNode, managedComponent);
            if (storageNode.hasNode(managedComponent.getName())) {
                final Node previouslyStoredNode = storageNode.getNode(managedComponent.getName());
                for (Node containerItemComponentNode : new NodeIterable(previouslyStoredNode.getNodes())) {
                    JcrUtils.copy(session, containerItemComponentNode.getPath(), managedComponentNode.getPath() + "/" + containerItemComponentNode.getName());
                }
            }
        }
        storageNode.remove();
    }


    private static void buildXNodeInternal(final Node storageNode, final HstComponentConfiguration xpageTemplate, final Session session, final BasePageComponent component, final String parentNodePath, final String userId) throws RepositoryException {
        log.info("Storage Path: " + storageNode.getPath());
        log.info("Component Name: " + component.getName());
        log.info("Parent Node Path " + parentNodePath);
        log.info("userID " + userId);
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
            setBasePagePropsOnNode(staticComponentNode, staticComponent);
            for (Object childComp : staticComponent.getComponents()) {
                buildXNodeInternal(storageNode, xpageTemplate, session, (BasePageComponent)childComp, newNodePath, userId);
            }
        } else if (component instanceof Page) {
            log.info("starting with page" + component.getName());
            final Page page = (Page)component;
            final Node pageNode = session.getNode(parentNodePath);
            setBasePagePropsOnNode(pageNode, page);
            //remove all children nodes from the hst component page node but remember the container nodes
            //container nodes are to be put back according to the incoming page model structure
            storeContainerNodesTemporarily(pageNode, storageNode);
            //get all containers from the template
            final List<String> collect = xpageTemplate.getChildren()
                    .values().stream()
                    .flatMap(componentConfiguration -> componentConfiguration.getChildren().values().stream())
                    .filter(componentConfiguration -> CONTAINER_COMPONENT.equals(componentConfiguration.getComponentType()))
                    .map(componentConfiguration -> componentConfiguration.getHippoIdentifier())
                    .collect(Collectors.toList());
            //remove all components but the ones coming from the xpage template
            for (Node childNode : new NodeIterable(pageNode.getNodes())) {
                if (!collect.contains(childNode.getName())) {
                    childNode.remove();
                }
            }
            for (BasePageComponent childComp : page.getComponents()) {
                log.info("building page ... ");
                log.info(childComp.getName());
                buildXNodeInternal(storageNode, xpageTemplate, session, childComp, parentNodePath, userId);
            }
        }
    }

    private static void buildNodeInternal(final Node storageNode, final Session session, final BasePageComponent component, final String parentNodePath, final String userId) throws RepositoryException {
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
            int componentCount = 1;
            String newNodePath = parentNodePath + "/" + StringUtils.substringBeforeLast(staticComponent.getName(), "-") + "-" + componentCount;
            Node staticComponentNode;
            while (session.nodeExists(newNodePath)) {
                componentCount++;
                newNodePath = parentNodePath + "/" + StringUtils.substringBeforeLast(staticComponent.getName(), "-") + "-" + componentCount;
            }
            staticComponentNode = JcrUtils.copy(session, STATIC_COMPONENT_LOCATION, newNodePath);
            setBasePagePropsOnNode(staticComponentNode, staticComponent);
            for (Object childComp : staticComponent.getComponents()) {
                buildNodeInternal(storageNode, session, (BasePageComponent)childComp, newNodePath, userId);
            }
        } else if (component instanceof Page) {
            final Page page = (Page)component;
            final Node pageNode = session.getNode(parentNodePath);
            setBasePagePropsOnNode(pageNode, page);
            //remove all children nodes from the hst component page node but remember the container nodes
            //container nodes are to be put back according to the incoming page model structure
            storeContainerNodesTemporarily(pageNode, storageNode);
            for (Node childNode : new NodeIterable(pageNode.getNodes())) {
                childNode.remove();
            }
            for (BasePageComponent childComp : page.getComponents()) {
                buildNodeInternal(storageNode, session, childComp, parentNodePath, userId);
            }
        }
    }

    /**
     * Builds an AbstractComponent from an HstCoponentConfiguration. PaaS model -> SaaS model
     *
     * @param componentConfig Hst's
     * @return A SaaS like AbstractComponent component
     */

    public static AbstractComponent buildComponent(final HstComponentConfiguration componentConfig, final Session session) {
        if (componentConfig.getComponentType().equals(CONTAINER_COMPONENT)) {
            final ManagedComponent managedComponent = new ManagedComponent();
            setBasePageProperties(managedComponent, componentConfig, session);
            return managedComponent;
        } else if (componentConfig.getComponentType().equals(HstComponentConfiguration.Type.COMPONENT)) {
            final StaticComponent staticComponent = new StaticComponent();
            staticComponent.setComponents(new ArrayList<>());
            setBasePageProperties(staticComponent, componentConfig, session);
            //depth-first traversal of child components
            componentConfig.getChildren().values().stream()
                    .forEach(childConfig -> {
                        final AbstractComponent childComponent = buildComponent(childConfig, session);
                        staticComponent.addComponentsItem(childComponent);
                    });
            return staticComponent;
        }
        return null;
    }

    public static AbstractComponent buildXComponent(final HstComponentConfiguration componentConfig, final Session session) {
        if (componentConfig.getComponentType().equals(CONTAINER_COMPONENT)) {
            final ManagedComponent managedComponent = new ManagedComponent();
            setBasePageProperties(managedComponent, componentConfig, session);
            return managedComponent;
        } else if (componentConfig.getComponentType().equals(HstComponentConfiguration.Type.COMPONENT)) {
            final StaticComponent staticComponent = new StaticComponent();
            staticComponent.setComponents(new ArrayList<>());
            setBasePageProperties(staticComponent, componentConfig, session);
            //depth-first traversal of child components
            componentConfig.getChildren().values().stream()
                    .filter(childConfig -> childConfig.getId().contains("hst:experiencePage")) //todo do better
                    .forEach(childConfig -> {
                        final AbstractComponent childComponent = buildComponent(childConfig, session);
                        staticComponent.addComponentsItem(childComponent);
                    });
            return staticComponent;
        }
        return null;
    }

    /**
     * Builds an AbstractComponent from a json coming from frontend
     *
     * @param jsonNode front end json
     * @return A SaaS like AbstractComponent component
     */
    public static AbstractComponent buildComponent(final Map<String, Object> jsonNode) {
        if (jsonNode.containsKey("managed") && jsonNode.get("managed").equals(true)) {
            final ManagedComponent managedComponent = new ManagedComponent();
            setBasePageProperties(managedComponent, jsonNode);
            return managedComponent;
        } else {
            final StaticComponent staticComponent = new StaticComponent();
            setBasePageProperties(staticComponent, jsonNode);
            if (jsonNode.containsKey("components")) {
                final ArrayList<LinkedHashMap<String, Object>> childrenComponents = (ArrayList<LinkedHashMap<String, Object>>)jsonNode.get("components");
                childrenComponents.forEach(childComponent -> {
                    staticComponent.addComponentsItem(buildComponent(childComponent));
                });
            }
            return staticComponent;
        }
    }

    /**
     * @param componentConfig to build the page from
     * @param session         for lookup of the hst:description field.
     * @return a page component without children from a given HstComponent config
     */
    public static Page buildPage(final HstComponentConfiguration componentConfig, final Session session, final Page.TypeEnum type) {
        final Page page = new Page();
        page.setName(componentConfig.getName());
        page.setLabel(componentConfig.getName());
        page.setParameters(componentConfig.getParameters());
        setDescriptionField(page, componentConfig, session);
        page.setType(type);
        return page;
    }

    private static Node getTemporaryStorageNode(final Session session) throws RepositoryException {
        return session.getRootNode().addNode(UUID.randomUUID().toString().toLowerCase(), JcrConstants.NT_UNSTRUCTURED);
    }

    private static void setBasePagePropsOnNode(final Node componentNode, final BasePageComponent basePageComponent) throws RepositoryException {
        if (!StringUtils.isBlank(basePageComponent.getDescription())) {
            componentNode.setProperty(PROP_DESC, new StringValue(basePageComponent.getDescription()));
        }
        setHstParameters(componentNode, basePageComponent);
    }

    private static void setManagedComponentPropsOnNode(final Node managedComponentNode, final ManagedComponent managedComponent) throws RepositoryException {
        setBasePagePropsOnNode(managedComponentNode, managedComponent);
        if (!StringUtils.isBlank(managedComponent.getLabel())) {
            managedComponentNode.setProperty(PROP_LABEL, new StringValue(managedComponent.getLabel()));
        }
        if (!StringUtils.isBlank(managedComponent.getXtype())) {
            managedComponentNode.setProperty(PROP_XTYPE, new StringValue(managedComponent.getXtype()));
        }
    }

    private static void setHstParameters(final Node componentNode, final BasePageComponent component) throws RepositoryException {
        final Map<String, String> parameters = component.getParameters();
        if (parameters != null && !parameters.keySet().isEmpty()) {
            componentNode.setProperty(GENERAL_PROPERTY_PARAMETER_NAMES, parameters.keySet().stream().map(StringValue::new).toArray(StringValue[]::new));
            componentNode.setProperty(GENERAL_PROPERTY_PARAMETER_VALUES, parameters.values().stream().map(StringValue::new).toArray(StringValue[]::new));
        }
    }

    private static void renameNode(final Node node, final String newName) throws RepositoryException {
        node.getSession().move(node.getPath(), node.getParent().getPath() + "/" + newName);
    }

    private static void storeContainerNodesTemporarily(final Node configNode, final Node storageNode) throws RepositoryException {
        if (configNode.getPrimaryNodeType().getName().equals(NODETYPE_HST_CONTAINERCOMPONENT)) {
            JcrUtils.copy(configNode.getSession(), configNode.getPath(), storageNode.getPath() + "/" + configNode.getName());
        }
        for (Node childNode : new NodeIterable(configNode.getNodes())) {
            storeContainerNodesTemporarily(childNode, storageNode);
        }
    }

    private static void storeXContainerNodesTemporarily(final Node configNode, final Node storageNode) throws RepositoryException {
        if (configNode.getPrimaryNodeType().getName().equals(NODETYPE_HST_CONTAINERCOMPONENT)) {
            JcrUtils.copy(configNode.getSession(), configNode.getPath(), storageNode.getPath() + "/" + configNode.getProperty("hippo:identifier").getString());
        }
        for (Node childNode : new NodeIterable(configNode.getNodes())) {
            storeXContainerNodesTemporarily(childNode, storageNode);
        }
    }

    private static void setBasePageProperties(final AbstractComponent component, final Map<String, Object> node) {
        component.setLabel((String)node.get("label"));
        component.setDescription((String)node.get("description"));
        component.setName((String)node.get("name"));
        component.setXtype(getXtype((String)node.get("xtype")));
        component.setParameters((Map<String, String>)node.get("parameters"));
    }


    private static void setBasePageProperties(final AbstractComponent component, final HstComponentConfiguration componentConfig, final Session session) {
        component.setName(componentConfig.getName());
        component.setXtype(getXtype(componentConfig.getXType()));
        component.setParameters(componentConfig.getParameters());
        component.setLabel(componentConfig.getLabel());
        setDescriptionField(component, componentConfig, session);
    }

    /**
     * @param component       SaaS component to set the label attribute to.
     * @param componentConfig PaaS component to read the hst:descrtiption property from.
     * @param session         for lookup of the hst:descrtiption property. This field is not part of the api
     */
    private static void setDescriptionField(final BasePageComponent component, final HstComponentConfiguration componentConfig, final Session session) {
        //need to get hold of the component node to read the hst:description field.
        //This field is mapped to the label field of the BasePageComponent
        component.setDescription(componentConfig.getName()); //default label value is the component name
        try {
            final Node configNode = JcrUtils.getNodeIfExists(componentConfig.getCanonicalStoredLocation(), session);
            if (configNode != null && configNode.hasProperty(PROP_DESC)) {
                component.setDescription(configNode.getProperty(PROP_DESC).getString());
            }
        } catch (RepositoryException ignored) {
        }
    }

    private static AbstractComponent.XtypeEnum getXtype(final String xTypeStr) {
        if (AbstractComponent.XtypeEnum.NOMARKUP.getValue().equals(xTypeStr)) {
            return AbstractComponent.XtypeEnum.NOMARKUP;
        } else if (AbstractComponent.XtypeEnum.ORDEREDLIST.getValue().equals(xTypeStr)) {
            return AbstractComponent.XtypeEnum.ORDEREDLIST;
        } else if (AbstractComponent.XtypeEnum.SPAN.getValue().equals(xTypeStr)) {
            return AbstractComponent.XtypeEnum.SPAN;
        } else if (AbstractComponent.XtypeEnum.UNORDEREDLIST.getValue().equals(xTypeStr)) {
            return AbstractComponent.XtypeEnum.UNORDEREDLIST;
        } else if (AbstractComponent.XtypeEnum.VBOX.getValue().equals(xTypeStr)) {
            return AbstractComponent.XtypeEnum.VBOX;
        }
        return null;
    }
}
