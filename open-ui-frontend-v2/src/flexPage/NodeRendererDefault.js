import React, {Component} from 'react';
import PropTypes from 'prop-types';
import './node-renderer-default.css';
import Icon from "@material-ui/core/Icon";
import {isDescendant} from "react-sortable-tree";

// very simple className utility for creating a classname string...
// Falsy arguments are ignored:
//
// const active = true
// const className = classnames(
//    "class1",
//    !active && "class2",
//    active && "class3"
// ); // returns -> class1 class3";
//
function classnames(...classes) {
  // Use Boolean constructor as a filter callback
  // Allows for loose type truthy/falsey checks
  // Boolean("") === false;
  // Boolean(false) === false;
  // Boolean(undefined) === false;
  // Boolean(null) === false;
  // Boolean(0) === false;
  // Boolean("classname") === true;
  return classes.filter(Boolean).join(' ');
}

class NodeRendererDefault extends Component {
  render () {
    const {
      scaffoldBlockPxWidth,
      toggleChildrenVisibility,
      connectDragPreview,
      connectDragSource,
      isDragging,
      canDrop,
      canDrag,
      node,
      title,
      subtitle,
      draggedNode,
      path,
      treeIndex,
      isSearchMatch,
      isSearchFocus,
      buttons,
      className,
      style,
      didDrop,
      treeId,
      isOver, // Not needed, but preserved for other renderers
      parentNode, // Needed for dndManager
      rowDirection,
      rowLabelClickEventHandler, //click event handler for title and subtitle area
      ...otherProps
    } = this.props;
    const nodeTitle = title || node.title;
    const nodeSubtitle = subtitle || node.subtitle;
    const rowDirectionClass = rowDirection === 'rtl' ? 'rst__rtl' : null;

    let handle;
    if (canDrag) {
      if (typeof node.children === 'function' && node.expanded) {
        // Show a loading symbol on the handle when the children are expanded
        //  and yet still defined by a function (a callback to fetch the children)
        handle = (
          <div className="rst__loadingHandle">
            <div className="rst__loadingCircle">
              {[...new Array(12)].map((_, index) => (
                <div
                  // eslint-disable-next-line react/no-array-index-key
                  key={index}
                  className={classnames(
                    'rst__loadingCirclePoint',
                    rowDirectionClass
                  )}
                />
              ))}
            </div>
          </div>
        );
      } else {
        // Show the handle used to initiate a drag-and-drop
        if (node.component.type === 'managed') {
          handle = connectDragSource(<div className="rst__moveHandle" style={{padding: '10px'}}>
            <Icon className="fa fa-columns" style={{color: 'grey', fontSize: 20}}/>
          </div>, {
            dropEffect: 'copy',
          });
        } else {
          handle = connectDragSource(<div className="rst__moveHandle" style={{padding: '10px'}}>
            <Icon className="fa fa-puzzle-piece" style={{color: 'grey', fontSize: 20}}/>
          </div>, {
            dropEffect: 'copy',
          });
        }

      }
    }
    if (treeIndex === 0) {
      if (node.component.type === 'abstract') {
        handle = (
          <div className="rst__loadingHandle" style={{padding: '10px'}}>
            <Icon className="fa fa-font" style={{color: 'grey', fontSize: 20}}/>
          </div>
        );
      } else if (node.component.type === 'xpage') {
        handle = (
          <div className="rst__loadingHandle" style={{padding: '10px'}}>
            <Icon className="fab fa-xing" style={{color: 'grey', fontSize: 20}}/>
          </div>
        );
      } else if (node.component.type === 'managed') {
        handle = (
          <div className="rst__loadingHandle" style={{padding: '10px'}}>
            <Icon className="fa fa-columns" style={{color: 'grey', fontSize: 20}}/>
          </div>
        );
      } else {
        handle = (
          <div className="rst__loadingHandle" style={{padding: '10px'}}>
            <Icon className="fa fa-puzzle-piece" style={{color: 'grey', fontSize: 20}}/>
          </div>
        );
      }

    }

    const isDraggedDescendant = draggedNode && isDescendant(draggedNode, node);
    const isLandingPadActive = !didDrop && isDragging;

    let buttonStyle = {left: -0.5 * scaffoldBlockPxWidth};
    if (rowDirection === 'rtl') {
      buttonStyle = {right: -0.5 * scaffoldBlockPxWidth};
    }

    return (
      <div style={{height: '100%'}} {...otherProps}>
        {toggleChildrenVisibility &&
        node.children &&
        (node.children.length > 0 || typeof node.children === 'function') && (
          <div>
            <button
              type="button"
              aria-label={node.expanded ? 'Collapse' : 'Expand'}
              className={classnames(
                node.expanded ? 'rst__collapseButton' : 'rst__expandButton',
                rowDirectionClass
              )}
              style={buttonStyle}
              onClick={() =>
                toggleChildrenVisibility({
                  node,
                  path,
                  treeIndex,
                })
              }
            />

            {node.expanded && !isDragging && (
              <div
                style={{width: scaffoldBlockPxWidth}}
                className={classnames('rst__lineChildren', rowDirectionClass)}
              />
            )}
          </div>
        )}

        <div className={classnames('rst__rowWrapper', rowDirectionClass)}>
          {/* Set the row preview to be used during drag and drop */}
          {connectDragPreview(
            <div
              className={classnames(
                'rst__row',
                isLandingPadActive && 'rst__rowLandingPad',
                isLandingPadActive && !canDrop && 'rst__rowCancelPad',
                isSearchMatch && 'rst__rowSearchMatch',
                isSearchFocus && 'rst__rowSearchFocus',
                rowDirectionClass,
                className
              )}
              style={{
                opacity: isDraggedDescendant ? 0.5 : 1,
                ...style,
              }}
            >
              {handle}
              <div
                className={classnames(
                  'rst__rowContents',
                  !canDrag && 'rst__rowContentsDragDisabled',
                  rowDirectionClass
                )}
              >
                <div className={classnames('rst__rowLabel', rowDirectionClass)}
                     style={{cursor: 'pointer'}}
                     onClick={(event) => this.props.rowLabelClickEventHandler && this.props.rowLabelClickEventHandler(event)}>
                  <span style={(!nodeTitle ? {color: 'grey'} : {})}
                        className={classnames(
                          'rst__rowTitle',
                          node.subtitle && 'rst__rowTitleWithSubtitle'
                        )}>
                    {typeof nodeTitle === 'function'
                      ? nodeTitle({
                        node,
                        path,
                        treeIndex,
                      })
                      : (!nodeTitle ? node.jcrNodeName : nodeTitle)}
                  </span>

                  {nodeSubtitle && (
                    <span className="rst__rowSubtitle">
                      {typeof nodeSubtitle === 'function'
                        ? nodeSubtitle({
                          node,
                          path,
                          treeIndex,
                        })
                        : nodeSubtitle}
                    </span>
                  )}
                </div>

                <div className="rst__rowToolbar">
                  {buttons.map((btn, index) => (
                    <div
                      key={index} // eslint-disable-line react/no-array-index-key
                      className="rst__toolbarButton"
                    >
                      {btn}
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    );
  }
}

NodeRendererDefault.defaultProps = {
  isSearchMatch: false,
  isSearchFocus: false,
  canDrag: false,
  toggleChildrenVisibility: null,
  buttons: [],
  className: '',
  style: {},
  parentNode: null,
  draggedNode: null,
  canDrop: false,
  title: null,
  subtitle: null,
  rowDirection: 'ltr',
};

NodeRendererDefault.propTypes = {
  node: PropTypes.shape({}).isRequired,
  title: PropTypes.oneOfType([PropTypes.func, PropTypes.node]),
  subtitle: PropTypes.oneOfType([PropTypes.func, PropTypes.node]),
  path: PropTypes.arrayOf(
    PropTypes.oneOfType([PropTypes.string, PropTypes.number])
  ).isRequired,
  treeIndex: PropTypes.number.isRequired,
  treeId: PropTypes.string.isRequired,
  isSearchMatch: PropTypes.bool,
  isSearchFocus: PropTypes.bool,
  canDrag: PropTypes.bool,
  scaffoldBlockPxWidth: PropTypes.number.isRequired,
  toggleChildrenVisibility: PropTypes.func,
  buttons: PropTypes.arrayOf(PropTypes.node),
  className: PropTypes.string,
  style: PropTypes.shape({}),

  // Drag and drop API functions
  // Drag source
  connectDragPreview: PropTypes.func.isRequired,
  connectDragSource: PropTypes.func.isRequired,
  parentNode: PropTypes.shape({}), // Needed for dndManager
  isDragging: PropTypes.bool.isRequired,
  didDrop: PropTypes.bool.isRequired,
  draggedNode: PropTypes.shape({}),
  // Drop target
  isOver: PropTypes.bool.isRequired,
  canDrop: PropTypes.bool,

  // rtl support
  rowDirection: PropTypes.string,
};



export default NodeRendererDefault;