import React, {Fragment} from 'react';
import 'react-sortable-tree/style.css';
import SortableTree from "react-sortable-tree";
import NodeRendererDefault from "./fork/NodeRendererDefault";
import MenuItem from "@material-ui/core/MenuItem";
import Drawer from "@material-ui/core/Drawer";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Input from "@material-ui/core/Input";
import MaterialTable from "material-table";
import PropTypes from 'prop-types';
import {getId} from "./util";
import TextField from "@material-ui/core/TextField";

/**
 * This component's data is managed by its parent component. If treeData should change, inform the parent by calling
 * this.props.onTreeDataChange(newTreeData).
 */
class ImmutableComponentTree extends React.Component {

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

  handleNodeClick (rowInfo) {
    this.props.onDrawerDataChange(rowInfo.node, false);
    this.setState({drawerOpen: !this.state.drawerOpen})
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
            disabled={true}
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
            disabled={true}
            value={drawerData.title}
          />
        </FormControl>
      </ListItem>
      <ListItem key={"label"}>
        <FormControl fullWidth style={{marginBottom: '10px'}}>
          <InputLabel htmlFor="label">Label</InputLabel>
          <Input
            id="label"
            disabled={true}
            value={drawerData.label || ''}
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
            disabled={true}
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
                    canDrag={false}
                    canDrop={({nextParent}) => {return false}}
                    onChange={treeData => console.log(treeData)}
                    nodeContentRenderer={NodeRendererDefault}
                    generateNodeProps={rowInfo => ({
                      rowLabelClickEventHandler: (event) => this.handleNodeClick(rowInfo)
                    })}
      />
      <Drawer anchor={'right'} open={this.state.drawerOpen}
              onClose={() => this.setState({drawerOpen: false})}>
        {drawerData && this.getDrawer(drawerData, columns)}
      </Drawer>
    </Fragment>
  }
}

ImmutableComponentTree.propTypes = {
  treeData: PropTypes.array,
  onTreeDataChange: PropTypes.func,
  addAsFirstChild: PropTypes.bool,
  componentsForMenu: PropTypes.array, //components for the right context menu
  drawerData: PropTypes.object,
  onDrawerDataChange: PropTypes.func
}
export default ImmutableComponentTree;
