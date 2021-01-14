import {TreeItem} from "react-sortable-tree";
import {AbstractComponent, ManagedComponent, Page, StaticComponent} from "../api/models";
import {getId, isNotEmptyOrNull} from "../common/common-utils";
import {JSONSchema7} from "json-schema";
import {Nullable} from "../api/models/nullable";

export interface ComponentTreeItem extends TreeItem {
  id: string
  children: ComponentTreeItem[],
  component: Page | StaticComponent | ManagedComponent | AbstractComponent
}

export interface TreeModel {
  id: Readonly<string>,
  page: Readonly<Page>,
  treeData: ComponentTreeItem[]
}

/**
 * Converts the node object of react tree ui to the component object that server understands
 **/

export function nodeToComponent (node: ComponentTreeItem) {
  const component: Page | StaticComponent | ManagedComponent | AbstractComponent = {
    ...node.component,
    components: undefined
  };

  node.children && node.children.forEach(nodeChild => {
    if (!component.components) {
      component.components = []
    }
    component.components.push(nodeToComponent(nodeChild))
  });

  return component;
}

/**
 * Converts the component object coming from server to the node object that react ui understands
 **/
export function componentToNode (component: AbstractComponent | StaticComponent | Page | ManagedComponent) {
  const node =
    ({
      id: `${component.name}-${getId()}`,
      component: {...component, components: []},
      title: component.description ? component.description : `${component.name}`,
      expanded: true,
      children: [],
    }) as ComponentTreeItem;

  isNotEmptyOrNull(component.components) &&
  component.components != null &&
  component.components.forEach(child => node.children.push((componentToNode(child)) as ComponentTreeItem));
  return node;
}

enum ComponentType {
  PAGE = 'page',
  XPAGE = 'xpage',
  ABSTRACT = 'abstract',
  MANAGED = 'managed',
  STATIC = 'static',
}


export function getSchemaForComponentType (type: Nullable<string>) {
  let schema = null;
  switch (type) {
    case ComponentType.PAGE:
    case ComponentType.XPAGE:
    case ComponentType.ABSTRACT:
      schema = {...pageSchema} as JSONSchema7;
      Object.assign(schema.properties?.name, {readOnly: true});
      Object.assign(schema.properties?.type, {readOnly: true});
      Object.assign(schema.properties?.extends, {readOnly: true});
      break;
    case ComponentType.MANAGED:
      schema = {...managedComponentSchema};
      Object.assign(schema.properties?.name, {readOnly: true});
      break;
    case ComponentType.STATIC:
      schema = {...staticComponentSchema};
      Object.assign(schema.properties?.definition, {readOnly: true});
      break;
  }
  return schema
}

const managedComponentSchema: JSONSchema7 = {
  type: "object",
  properties: {
    name: {
      type: "string",
    },
    description: {
      type: "string"
    },
    label: {
      type: "string",
    },
    parameters: {
      "type": "object",
      "additionalProperties": {
        "type": "string"
      }
    },
    xtype: {
      type: "string",
      "enum":
        [
          "hst.nomarkup",
          "hst.span",
          "hst.orderedlist",
          "hst.unorderedlist",
          "hst.vbox",
        ],
      "default": 'hst.nomarkup',
    }
  }
};

const staticComponentSchema: JSONSchema7 = {
  type: "object",
  properties: {
    name: {
      type: "string",
    },
    description: {
      type: "string"
    },
    parameters: {
      "type": "object",
      "additionalProperties": {
        "type": "string"
      }
    },
    definition: {
      type: "string",
    }
  }
};

export const addPageSchema = {
  type: "object",
  properties: {
    type: {
      type: "string",
      "enum":
        [
          "abstract",
          "page",
          "xpage",
        ],
      "enumNames":
        [
          "Abstract Page",
          "Page",
          "X Page",
        ]
    },
    extends: {
      type: "string"
    },
    name: {
      type: "string",
    },
    description: {
      type: "string"
    },
    parameters: {
      "type": "object",
      "additionalProperties": {
        "type": "string"
      }
    }
  }
};

const pageSchema = {
  type: "object",
  properties: {
    type: {
      type: "string",
      "enum":
        [
          "abstract",
          "page",
          "xpage",
        ],
      "enumNames":
        [
          "Abstract Page",
          "Page",
          "X Page",
        ]
    },
    extends: {
      type: "string"
    },
    name: {
      type: "string",
    },
    description: {
      type: "string"
    },
    parameters: {
      "type": "object",
      "additionalProperties": {
        "type": "string"
      }
    }
  }
};

export function getPageNameFromPagePath (pagePath: string) {
  return pagePath === "/" || !pagePath ? "/root" : pagePath
}

export function convertPageToTreeModel (page: Page): TreeModel {
  console.log('converting page to tree model', page)
  return {
    id: getId(),
    page: page,
    treeData: [componentToNode(page)]
  } as TreeModel
}
