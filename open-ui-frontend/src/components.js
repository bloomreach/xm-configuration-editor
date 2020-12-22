import React, {Fragment} from 'react';
import 'react-sortable-tree/style.css';
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import Accordion from "@material-ui/core/Accordion";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import Divider from "@material-ui/core/Divider";
import AccordionActions from "@material-ui/core/AccordionActions";
import Button from "@material-ui/core/Button";
import AddOutlinedIcon from '@material-ui/icons/Add';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import IconButton from "@material-ui/core/IconButton";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import TextField from "@material-ui/core/TextField";
import SaveOutlinedIcon from "@material-ui/icons/SaveOutlined";
import DeleteOutlinedIcon from "@material-ui/icons/DeleteOutlined";
import {deleteComponentWithName, getAllComponents, putComponentWithName} from './config-api';
import {componentToNode, convertComponentsToTreeDataArray, getId, nodeToComponent} from './util';
import MenuItem from "@material-ui/core/MenuItem";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Switch from "@material-ui/core/Switch";
import ImmutableComponentTree from "./immutable-component-tree";

class Components extends React.Component {

  constructor (props) {
    super(props);

    this.ui = props.ui;
    this.state = {
      columns: [
        {title: 'Name', field: 'name'},
        {title: 'Value', field: 'value'}
      ],
      components: [],
      open: false,
      componentType: 'static',
      drawerData: {}
    }

    this.componentName = React.createRef();
    this.jcrNodeName = React.createRef();

    this.addComponent = this.addComponent.bind(this);
    this.deleteComponent = this.deleteComponent.bind(this);
    this.handleOpen = this.handleOpen.bind(this);
    this.handleClose = this.handleClose.bind(this);
    this.handleDrawerDataChange = this.handleDrawerDataChange.bind(this);
    this.onUpdateComponent = this.onUpdateComponent.bind(this);
  }

  getComponents () {
    return this.state.components;
  }

  componentDidMount () {
    const ui = this.ui;
    this.setState({baseUrl: ui.baseUrl});
    ui.channel.page.get().then(page => page.channel.id).then(channelId => {
      getAllComponents(this.state.baseUrl, channelId).then(result => {
        const treeData = convertComponentsToTreeDataArray(result);
        this.setState({components: treeData});
      });
      this.setState({channelId: channelId});
    })
  }

  deleteComponent (item) {
    deleteComponentWithName(this.state.baseUrl, this.state.channelId, item.treeData[0].jcrNodeName).then(result => {
      this.setState(state => {
        const components = state.components.filter((i) => i.id !== item.id);
        return {
          components,
        };
      });
    }).catch(reason => {
      console.error('something went wrong deleting the component' + item.name, reason);
    })
  }

  addComponent (jcrNodeName, name) {
    putComponentWithName(this.state.baseUrl, this.state.channelId, jcrNodeName, {
      name: jcrNodeName,
      managed: this.state.componentType === 'manageable' ? true : false,
      parameters: {},
      label: name,
      description: name,
      xtype: 'hst.vbox'
    }).then(component => {
      this.setState(state => {
        let id = getId();
        const newTreeItem = {
          name: name,
          id: id,
          treeData: [
            componentToNode(component, id),
          ],
          changed: false //because it's already created
        }
        const components = state.components.concat(newTreeItem);
        return {
          components
        };
      });
    })
    this.handleClose();
  }

  handleClose () {
    this.setState({open: false})
  }

  handleOpen () {
    this.setState({open: true})
  }

  handleComponentType (event) {
    this.setState({componentType: event.target.value})
  }

  onTreeDataChange (item, index, newTreeData) {
    let components = this.state.components;
    components[index] = {
      id: item.id,
      name: item.name,
      treeData: newTreeData,
      changed: true,
    }
    this.setState({components: components})
  }

  onUpdateComponent = i => {
    this.setState(state => {
      const components = this.state.components.map((item) => {
        if (item.id === i.handle) {
          item.changed = true;
          return item;
        }
        return item;
      });
      return {
        components,
      };
    });
  };

  handleDrawerDataChange (drawerData) {
    this.onUpdateComponent(drawerData);
    this.setState({drawerData: drawerData});
  }

  handleSave (item) {
    let node = item.treeData[0];
    let component = nodeToComponent(node);
    putComponentWithName(this.state.baseUrl, this.state.channelId, component.name, component).then(response => {
      console.info(response.data);
    }).catch(exception => {
      console.error(exception.response.data.errorMessage);
      this.openSnackbar(exception.response.data.errorMessage, 'error');
    });
  }

  render () {
    const {open} = this.state || false;
    const {componentType} = this.state || 'static';

    return <Fragment>
      <AppBar position="sticky" variant={'outlined'} color={'default'}>
        <Toolbar>
          <IconButton
            edge="start"
            color="inherit"
            aria-label="Add"
            disabled={true}
            onClick={event => this.handleOpen()}
          >
            <AddOutlinedIcon/>
          </IconButton>
          <FormControlLabel
            edge="end"
            control={
              <Switch
                checked={true}
                name="developer"
                color="primary"
                edge="end"
              />
            }
            label="Developer"
          />
        </Toolbar>
      </AppBar>
      <Dialog open={open} onClose={this.handleClose} aria-labelledby="form-dialog-title">
        <DialogTitle>Add Component</DialogTitle>
        <DialogContent>
          <TextField
            id="component-type"
            select
            fullWidth
            margin="dense"
            label="Component Type"
            value={componentType}
            onChange={event => this.handleComponentType(event)}
          >
            <MenuItem value={'manageable'}>Manageable</MenuItem>
            <MenuItem value={'static'}>Static</MenuItem>
          </TextField>
          <TextField
            autoFocus
            inputRef={this.componentName}
            margin="dense"
            id="description"
            label="Description"
            type="text"
            fullWidth
            required={true}
          />
          <TextField
            inputRef={this.jcrNodeName}
            margin="dense"
            id="jcr-node-name"
            label="JCR Node Name"
            type="text"
            fullWidth
            required={true}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={this.handleClose} color="primary">
            Cancel
          </Button>
          {/*todo add validator before click on create */}
          <Button disabled={true} onClick={(event) => this.addComponent(this.jcrNodeName.current.value, this.componentName.current.value)} color="primary">
            Create
          </Button>
        </DialogActions>
      </Dialog>
      {this.getComponents().map((item, index) => (
        <Accordion key={index}>
          <AccordionSummary
            expandIcon={<ExpandMoreIcon/>}
            aria-controls="panel1c-content"
            id="panel1c-header"
          >
            <Typography>{item.name}</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <ImmutableComponentTree key={index} treeData={item.treeData}
                                    onTreeDataChange={(newTreeData) => this.onTreeDataChange(item, index, newTreeData)}
                                    componentsForMenu={this.state.components}
                                    drawerData={this.state.drawerData}
                                    onDrawerDataChange={(drawerData) => this.handleDrawerDataChange(drawerData)}/>
          </AccordionDetails>
          <Divider/>
          <AccordionActions>
            <IconButton
              disabled={true}
              edge="start"
              color="inherit"
              aria-label="Delete"
              onClick={event => this.deleteComponent(item)}
            >
              <DeleteOutlinedIcon/>
            </IconButton>
            <IconButton
              edge="start"
              color="inherit"
              aria-label="Save"
              // disabled={!item.changed}
              disabled={true}
              onClick={event => this.handleSave(item)}
            >
              <SaveOutlinedIcon/>
            </IconButton>
          </AccordionActions>
        </Accordion>
      ))}
    </Fragment>
  }

}

export default Components;
