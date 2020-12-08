import React, {Fragment} from 'react';
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import 'react-sortable-tree/style.css';
import {toggleExpandedForAll} from 'react-sortable-tree';
import Grid from "@material-ui/core/Grid";
import SaveOutlinedIcon from '@material-ui/icons/SaveOutlined';
import IconButton from "@material-ui/core/IconButton";
import ExpandLessOutlinedIcon from '@material-ui/icons/ExpandLessOutlined';
import RefreshOutlinedIcon from '@material-ui/icons/RefreshOutlined';
import ExpandMoreOutlinedIcon from '@material-ui/icons/ExpandMoreOutlined';
import {getAllComponents, getPageWithName, getUrl, putPageWithName} from "./config-api";
import PositionedSnackbar from "./PositionedSnackbar";
import ComponentTree from "./component-tree";
import {componentToNode, convertComponentsToTreeDataArray, nodeToComponent} from "./util";
import {ACLConsumer} from "./ACLContext";
import ImmutableComponentTree from "./immutable-component-tree";

class CurrentPage extends React.Component {

  constructor (props) {
    super(props);

    this.ui = props.ui;
    this.state = {
      addAsFirstChild: false,
      treeData: [],
      snackbarOpen: false,
      snackbarMessage: '',
      snackbarSeverity: 'success',
      treeChanged: false,
      // treeDataRevisionArray: [],
      drawerData: {}
    }

    this.expandAll = this.expandAll.bind(this);
    this.collapseAll = this.collapseAll.bind(this);
    this.updateTreeData = this.updateTreeData.bind(this);
    this.handleDrawerDataChange = this.handleDrawerDataChange.bind(this);
  }

  handleNavigate = (page) => {
    console.log('navigate....');
    this.setState({currentPagePath: page.path, currentChannelId: page.channel.id});
    this.updateTreeData([]);
    this.updateComponentHierarchy(getPageWithName(this.state.baseUrl, page.channel.id, page.path));
  }

  refresh = () => {
    console.log('refresh');
    this.updateTreeData([]);
    this.updateComponentHierarchy(getPageWithName(this.state.baseUrl, this.state.currentChannelId, this.state.currentPagePath));
  }

  componentDidMount () {
    const ui = this.ui;
    this.setState({baseUrl: ui.baseUrl});
    // ui.channel.on()
    ui.channel.page.on('navigate', this.handleNavigate);
    ui.channel.page.get().then(page => {
      this.handleNavigate(page);
      getAllComponents(ui.baseUrl, page.channel.id).then(result => {
        const treeData = convertComponentsToTreeDataArray(result);
        this.setState({components: treeData, currentPagePath: page.path, currentChannelId: page.channel.id});
      });
    });
  }

  updateTreeData (treeData) {
    if (treeData.length > 0) {
      this.setState({treeChanged: true});
    }
    this.setState({treeData});
  }

  expand (expanded) {
    this.setState({
      treeData: toggleExpandedForAll({
        treeData: this.state.treeData,
        expanded,
      }),
    });
  }

  expandAll () {
    this.expand(true);
  }

  collapseAll () {
    this.expand(false);
  }

  getCurrentPageToolBar () {
    return <ACLConsumer>
      {permissions =>
        <Toolbar>
          {permissions?.currentPageEditAllowed &&
          <IconButton
            edge="start"
            color="inherit"
            aria-label="Save"
            onClick={() => this.handleSave()}
            disabled={!this.state.treeChanged}>
            <SaveOutlinedIcon/>
          </IconButton>
          }
          <IconButton
            edge="start"
            color="inherit"
            aria-label="Refresh"
            onClick={() => this.refresh()}
          >
            <RefreshOutlinedIcon/>
          </IconButton>
          <IconButton
            edge="start"
            color="inherit"
            aria-label="Collapse All"
            onClick={this.collapseAll}
          >
            <ExpandLessOutlinedIcon/>
          </IconButton>
          <IconButton
            edge="start"
            color="inherit"
            aria-label="Expand All"
            onClick={this.expandAll}
          >
            <ExpandMoreOutlinedIcon/>
          </IconButton>
        </Toolbar>
      }
    </ACLConsumer>
  }

  render () {
    const {treeData} = this.state || [];
    const validTree = treeData && treeData.length > 0;

    return <ACLConsumer>
      {permissions =>
        <Fragment>
          <Grid container spacing={2}>
            <Grid item xs={12}>

              <AppBar position="sticky" variant={'outlined'} color={'default'}>
                {this.getCurrentPageToolBar()}
              </AppBar>

              {validTree && permissions?.currentPageEditAllowed &&
              <ComponentTree treeData={treeData} onTreeDataChange={this.updateTreeData}
                             componentsForMenu={this.state.components} onDrawerDataChange={this.handleDrawerDataChange} drawerData={this.state.drawerData}/>
              }
              {validTree && !permissions?.currentPageEditAllowed && permissions?.currentPageViewAllowed &&
              <ImmutableComponentTree treeData={treeData}
                                      onDrawerDataChange={this.handleDrawerDataChange} drawerData={this.state.drawerData}/>
              }

            </Grid>
          </Grid>
          < PositionedSnackbar open={this.state.snackbarOpen} vertical={'bottom'} horizontal={'center'}
                               handleClose={() => this.handleSnackbarClose()} message={this.state.snackbarMessage}
                               severity={this.state.snackbarSeverity}/>

        </Fragment>
      }
    </ACLConsumer>
  }

  updateComponentHierarchy (responsePromise) {
    this.reset();
    responsePromise.then(newPageResponse => {
      const treeData = componentToNode(newPageResponse.data);
      this.updateTreeData([treeData]);
      this.openSnackbar("Page hierarchy updated");
      return newPageResponse.data
    }).catch(exception => {
      console.log(exception.response.data.errorMessage)
      this.openSnackbar(exception.response.data.errorMessage, 'error');
    });
  }

  handleSave () {
    let treeDataToPage = nodeToComponent(this.state.treeData[0]);
    if (treeDataToPage.type === null || treeDataToPage.type === undefined) {
      treeDataToPage.type = "page";
    }
    const ui = this.ui;
    ui.channel.page.get().then(page => {
      putPageWithName(this.state.baseUrl, page.channel.id, page.path, treeDataToPage)
        .then(response => {
          if (response.status === 201 && response.headers.location) {
            this.updateComponentHierarchy(getUrl(response.headers.location));

            ui.channel.refresh().then(() => {
              console.log('channel refreshed');
            })
            ui.channel.page.refresh().then(() => {
              console.log('page refreshed');
            });
          }
          return response.data
        }).catch(exception => {
        console.log(exception.response.data.errorMessage);
        this.openSnackbar(exception.response.data.errorMessage, 'error');
      });
    });
  }

  reset () {
    this.setState({treeChanged: false}); //reset
  }

  openSnackbar (message, severity) {
    this.setState({snackbarMessage: message, snackbarOpen: true, snackbarSeverity: severity})
  }

  handleSnackbarClose () {
    this.setState({snackbarOpen: false, snackbarMessage: '', snackbarSeverity: 'success'});
  }

  handleDrawerDataChange (drawerData, treeDataChanged) {
    this.setState({drawerData: drawerData});
    if (treeDataChanged) {
      this.updateTreeData(this.state.treeData)
    }
  }
}

export default CurrentPage;
