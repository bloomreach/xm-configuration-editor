import React, {Fragment} from 'react';
import 'react-sortable-tree/style.css';
import SortableTree, {addNodeUnderParent, removeNode} from "react-sortable-tree";
import NodeRendererDefault from "./fork/NodeRendererDefault";
import PopupState, {bindMenu, bindTrigger} from "material-ui-popup-state/index";
import MoreHorizOutlinedIcon from "@material-ui/icons/MoreHorizOutlined";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import Typography from "@material-ui/core/Typography";
import {Delete} from "@material-ui/icons";
import Drawer from "@material-ui/core/Drawer";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Input from "@material-ui/core/Input";
import MaterialTable from "material-table";
import PropTypes from 'prop-types';
import {deepCopy, getId, updateNodeIds} from "./util";
import TextField from "@material-ui/core/TextField";
import Icon from "@material-ui/core/Icon";

/**
 * This component's data is managed by its parent component. If treeData should change, inform the parent by calling
 * this.props.onTreeDataChange(newTreeData).
 */
class ComponentTree extends React.Component {

  constructor (props) {
    super(props);

    this.state = {
      addAsFirstChild: this.props.addAsFirstChild || false,
      columns: [
        {field: 'key', title: 'Key'},
        {field: 'value', title: 'Value'}
      ],
    }
  }

  onMove (treeData, node, nextParentNode, prevPath, prevTreeIndex, nextPath, nextTreeIndex) {

  }

  getMenuItemForManageableComponent (componentTitle, rowInfo, popupState, key, newNode) {
    return (
      <MenuItem onClick={event => this.add(rowInfo, popupState, true, newNode)} key={key}>
        <ListItemIcon>
          <Icon className="fa fa-columns" fontSize={'small'}/>
        </ListItemIcon>
        <Typography variant="inherit">Add {componentTitle}</Typography>
      </MenuItem>
    )
  }

  getMenuItemForStaticComponent (componentName, rowInfo, popupState, key, newNode) {
    return (
      <MenuItem onClick={event => this.add(rowInfo, popupState, false, newNode)} key={key}>
        <ListItemIcon>
          <Icon className="fa fa-puzzle-piece" fontSize={'small'}/>
        </ListItemIcon>
        <Typography variant="inherit">Add {componentName}</Typography>
      </MenuItem>
    )
  }

  getStaticComponentMenu (rowInfo) {
    return <PopupState variant="popover" popupId="demo-popup-menu">
      {(popupState) => (
        <React.Fragment>
          <MoreHorizOutlinedIcon {...bindTrigger(popupState)}/>
          <Menu {...bindMenu(popupState)}>
            {this.props.componentsForMenu && this.props.componentsForMenu.map((treeDataArrayObj, index) => {
              let type = treeDataArrayObj.treeData[0].type;
              let title = treeDataArrayObj.treeData[0].title;
              return type === 'component' ? this.getMenuItemForStaticComponent(title, rowInfo, popupState, index, treeDataArrayObj.treeData[0])
                : this.getMenuItemForManageableComponent(title, rowInfo, popupState, index, treeDataArrayObj.treeData[0]);
            })}
            <MenuItem disabled={rowInfo.treeIndex === 0} onClick={event => this.delete(rowInfo, popupState)}>
              <ListItemIcon>
                <Delete fontSize="small"/>
              </ListItemIcon>
              <Typography variant="inherit">Delete Component</Typography>
            </MenuItem>
          </Menu>
        </React.Fragment>
      )}
    </PopupState>
  }

  getManageableComponentMenu (rowInfo) {
    return <PopupState variant="popover" popupId="popup-menu">
      {(popupState) => (
        <React.Fragment>
          <MoreHorizOutlinedIcon {...bindTrigger(popupState)}/>
          <Menu {...bindMenu(popupState)}>
            <MenuItem disabled={rowInfo.treeIndex === 0} onClick={event => this.delete(rowInfo, popupState)}>
              <ListItemIcon>
                <Delete fontSize="small"/>
              </ListItemIcon>
              <Typography variant="inherit">Delete Component</Typography>
            </MenuItem>
          </Menu>
        </React.Fragment>
      )}
    </PopupState>
  }

  handleNodeClick (rowInfo) {
    this.props.onDrawerDataChange(rowInfo.node, false);
    this.setState({drawerOpen: !this.state.drawerOpen})
  }

  handleDescriptionChange (value) {
    let node = this.props.drawerData;
    node.title = value;
    this.props.onDrawerDataChange(node, true);
  }

  handleLabelChange (value) {
    let node = this.props.drawerData;
    node.label = value;
    this.props.onDrawerDataChange(node, true);
  }

  handleXtypeChange (value) {
    let node = this.props.drawerData;
    node.xtype = value;
    this.props.onDrawerDataChange(node, true);
  }

  getStaticComponentDrawer (drawerData, columns) {
    return <List>
      <ListItem key={"name"}>
        <FormControl fullWidth style={{marginBottom: '10px'}}>
          <InputLabel htmlFor="jcr-node-name">JCR Node Name</InputLabel>
          <Input
            id="jcr-node-name"
            value={drawerData.jcrNodeName}
            disabled={true}
          />
        </FormControl>
      </ListItem>
      <ListItem key={"description"}>
        <FormControl fullWidth style={{marginBottom: '10px'}}>
          <InputLabel htmlFor="description">Description</InputLabel>
          <Input
            id="description"
            value={drawerData.title}
            onChange={event => this.handleDescriptionChange(event.target.value)}
          />
        </FormControl>
      </ListItem>
      {/*have to keep the following getId() otherwise react won't rerender*/}
      <ListItem key={`parameters-${getId()}`}>
        {this.getParametersTable(drawerData, columns)}
      </ListItem>
    </List>

  }

  getManageableComponentDrawer (drawerData, columns) {
    return <List>
      <ListItem key={"name"}>
        <FormControl fullWidth style={{marginBottom: '10px'}}>
          <InputLabel htmlFor="jcr-node-name">JCR Node Name</InputLabel>
          <Input
            id="jcr-node-name"
            value={drawerData.jcrNodeName}
            disabled={true}
          />
        </FormControl>
      </ListItem>
      <ListItem key={"description"}>
        <FormControl fullWidth style={{marginBottom: '10px'}}>
          <InputLabel htmlFor="description">Description</InputLabel>
          <Input
            id="description"
            value={drawerData.title}
            onChange={event => this.handleDescriptionChange(event.target.value)}
          />
        </FormControl>
      </ListItem>
      <ListItem key={"label"}>
        <FormControl fullWidth style={{marginBottom: '10px'}}>
          <InputLabel htmlFor="label">Label</InputLabel>
          <Input
            id="label"
            value={drawerData.label || ''}
            onChange={event => this.handleLabelChange(event.target.value)}
          />
        </FormControl>
      </ListItem>
      <ListItem key={`xtype-${getId()}`}>
        <FormControl>
          <TextField
            id="xtype"
            select
            fullWidth
            margin="dense"
            label="XType"
            value={drawerData.xtype || ''}
            onChange={event => this.handleXtypeChange(event.target.value)}
          >
            <MenuItem value={'hst.vbox'}>hst.vbox</MenuItem>
            <MenuItem value={'hst.unorderedlist'}>hst.unorderedlist</MenuItem>
            <MenuItem value={'hst.orderedlist'}>hst.orderedlist</MenuItem>
            <MenuItem value={'hst.span'}>hst.span</MenuItem>
            <MenuItem value={'hst.nomarkup'}>hst.nomarkup</MenuItem>
          </TextField>
        </FormControl>
      </ListItem>
      {/*have to keep the following getId() otherwise react won't rerender*/}
      <ListItem key={`parameters-${getId()}`}>
        {this.getParametersTable(drawerData, columns)}
      </ListItem>
    </List>
  }

  getParametersTable (drawerData, columns) {
    return <MaterialTable
      title="Parameters"
      columns={columns}
      data={drawerData.parameters}
      options={{
        search: false,
        paging: false,
        sorting: false
      }}
      editable={{
        onRowAdd: newData =>
          new Promise((resolve, reject) => {
            setTimeout(() => {
              resolve();
              // setData([...data, newData]);
              if (!drawerData.parameters) {
                drawerData.parameters = [];
              }
              drawerData.parameters.push(newData);
              this.props.onDrawerDataChange(drawerData, true); //inform parent, needed for keeping revision history in
                                                               // current page tab
            }, 300)
          }),
        onRowUpdate: (newData, oldData) =>
          new Promise((resolve, reject) => {
            setTimeout(() => {
              resolve();
              if (oldData) {
                let parameters = drawerData.parameters;
                parameters[parameters.indexOf(oldData)] = newData;
                this.props.onDrawerDataChange(drawerData, true); //inform parent, needed for keeping revision history
                                                                 // in current page tab
              }
            }, 300)
          }),
        onRowDelete: oldData =>
          new Promise((resolve, reject) => {
            setTimeout(() => {
              resolve()
              const index = oldData.tableData.id;
              let parameters = drawerData.parameters;
              parameters.splice(index, 1);
              this.props.onDrawerDataChange(drawerData, true); //inform parent, needed for keeping revision history in
                                                               // current page tab
            }, 300)
          }),
      }}
    />

  }

  getDrawer (drawerData, columns) {
    return drawerData.type === 'component' ?
      this.getStaticComponentDrawer(drawerData, columns) :
      this.getManageableComponentDrawer(drawerData, columns);
  }

  render () {
    const {drawerData} = this.props || {};
    const {columns} = this.state || [];

    return <Fragment>
      <SortableTree style={{minHeight: '70px', width: '100%'}} reactVirtualizedListProps={{autoHeight: true}}
                    isVirtualized={false}
                    treeData={this.props.treeData}
                    getNodeKey={({node}) => node.id}
                    onChange={treeData => this.props.onTreeDataChange(treeData)}
                    canNodeHaveChildren={node => (node.type !== 'container')}
                    onMoveNode={({treeData, node, nextParentNode, prevPath, prevTreeIndex, nextPath, nextTreeIndex}) =>
                      this.onMove(treeData, node, nextParentNode, prevPath, prevTreeIndex, nextPath, nextTreeIndex)}
                    canDrag={({treeIndex}) => treeIndex !== 0}
                    canDrop={({nextParent}) => nextParent !== null}
                    nodeContentRenderer={NodeRendererDefault}
                    generateNodeProps={rowInfo => ({
                      buttons: [rowInfo.node.type !== 'container' ? this.getStaticComponentMenu(rowInfo) : this.getManageableComponentMenu(rowInfo)],
                      rowLabelClickEventHandler: (event) => this.handleNodeClick(rowInfo)
                    })}
      />
      <Drawer anchor={'right'} open={this.state.drawerOpen}
              onClose={() => this.setState({drawerOpen: false})}>
        {drawerData && this.getDrawer(drawerData, columns)}
      </Drawer>
    </Fragment>
  }

  add (rowInfo, popupState, manageable, newNode) {
    //need to copy incoming node to update nodeIds of subtree. This is to avoid node id clashes
    const copyOfNewNode = deepCopy(newNode);
    updateNodeIds(copyOfNewNode);

    const getNodeKey = ({node}) => node.id;
    this.props.onTreeDataChange(
      addNodeUnderParent({
      treeData: this.props.treeData,
      parentKey: rowInfo.node.id,
      expandParent: true,
      getNodeKey,
      newNode: copyOfNewNode,
      addAsFirstChild: this.state.addAsFirstChild,
    }
    ).treeData)
    popupState.close();
  }

  delete (rowInfo, popupState) {
    const getNodeKey = ({node}) => node.id;
    this.props.onTreeDataChange(removeNode({
      treeData: this.props.treeData,
      path: rowInfo.path,
      getNodeKey,
      ignoreCollapsed: true
    }).treeData)
    popupState.close();
  }

}

ComponentTree.propTypes = {
  treeData: PropTypes.array,
  onTreeDataChange: PropTypes.func,
  addAsFirstChild: PropTypes.bool,
  componentsForMenu: PropTypes.array, //components for the right context menu
  drawerData: PropTypes.object,
  onDrawerDataChange: PropTypes.func
}

export default ComponentTree;
