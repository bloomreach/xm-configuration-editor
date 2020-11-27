/**
 * Converts the node object of react tree ui to the component object that server understands
 **/

// export function nodeToComponent (node) {
//   let component = {
//     name: node.jcrNodeName,
//     managed: node.type === 'container',
//     parameters: {},
//     label: node.label,
//     description: node.title,
//     xtype: node.xtype,
//     type: node.type
//   }
//   node.children && node.children.forEach(c => {
//     if (!component.components) {
//       component.components = []
//     }
//     component.components.push(nodeToComponent(c))
//   });
//
//   node.parameters && node.parameters.forEach(paramObj => {
//     component.parameters[paramObj['key']] = paramObj['value'];
//   })
//   return component;
// }

import {TreeItem} from "react-sortable-tree";
import {AbstractComponent, Page} from "./api/models";

/**
 * Converts the component object coming from server to the node object that react ui understands
 **/
export function componentToNode (component: AbstractComponent | Page, handle?: string) {
  const node =
    ({
      id: `${component.name}-${getId()}`,
      jcrNodeName: component.name,
      type: component.type,
      title: component.description,
      // label: component.label,
      // xtype: component.xtype,
      expanded: true,
      parameters: [],
      children: [],
      handle: handle
    }) as TreeItem;

  // @ts-ignore
  isNotEmptyOrNull(component.components) && component.components.forEach(c => node.children.push((componentToNode(c, handle)) as TreeItem));

  // @ts-ignore
  isNotEmptyOrNull(component.parameters) && Object.entries(component.parameters).forEach((key, value) => {
    console.log(key);
    console.log(value);
  })
  return node;
}

export function getId () {
  // Math.random should be unique because of its seeding algorithm.
  // Convert it to base 36 (numbers + letters), and grab the first 9 characters
  // after the decimal.
  return Math.random().toString(36).substr(2, 9);
}

export function isNotEmptyOrNull (array: any) {
  return (typeof array !== 'undefined' && array.length > 0);
}

// export function convertComponentsToTreeDataArray (components: Array<AbstractComponent>) {
//   const trees: Array<Object> = [];
//   if (isNotEmptyOrNull(components)) {
//     components.forEach(component => {
//       const handle = `${component.name}-${getId()}`
//       trees.push({
//           id: handle,
//           name: component.description,
//           treeData: [componentToNode(component, handle)]
//         }
//       )
//     })
//   }
//   return trees;
// }

/**
 * Update ids of a node and its children. Useful for avoiding clash of ids when adding nodes under same nodes
 * @param node
 */
// export function updateNodeIds (node) {
//   node.id = node.title + getId();
//   node.children && node.children.forEach(childNode => {
//     updateNodeIds(childNode);
//   });
// }

/**
 * Perform a deep copy of a json
 */
export function deepCopy (obj: Object) {
  return JSON.parse(JSON.stringify(obj));
}