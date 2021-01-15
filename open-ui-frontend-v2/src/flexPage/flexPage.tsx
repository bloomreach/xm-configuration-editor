import React from "react";
import {PageProperties, UiScope} from "@bloomreach/ui-extension";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import {Button} from "@material-ui/core";
import SaveOutlinedIcon from "@material-ui/icons/SaveOutlined";
import {ChannelFlexPageOperationsApi, ChannelOtherOperationsApi} from "../api";
import {AbstractComponent, Page} from "../api/models";
import {convertPageToTreeModel, getPageNameFromPagePath, TreeModel} from "./page-util";
import PageEditor from "./PageEditor";
import PositionedSnackbar from "../common/PositionedSnackbar";
import {Color} from "@material-ui/lab";
import {ACLConsumer} from "../ACLContext";

type FlexPageState = {
  channelId?: string
  path?: string
  page?: Page
  components?: Array<AbstractComponent>
  treeModel?: TreeModel
  saveDisabled: boolean
  snackbarOpen: boolean
  snackbarMessage: string
  snackbarSeverity: Color
}
type FlexPageProps = {
  ui: UiScope
}

class FlexPage extends React.Component<FlexPageProps, FlexPageState> {
  private pageOperationsApi: ChannelFlexPageOperationsApi;
  private otherOperationsApi: ChannelOtherOperationsApi;

  constructor (props: FlexPageProps) {
    super(props);

    const baseOptions = {withCredentials: true};
    const basePath = `${this.props.ui.baseUrl}ws/config/v2`;

    this.pageOperationsApi = new ChannelFlexPageOperationsApi({
      baseOptions: baseOptions
    }, basePath);

    this.otherOperationsApi = new ChannelOtherOperationsApi({
      baseOptions: baseOptions
    }, basePath);

    this.state = {
      saveDisabled: true,
      snackbarOpen: false,
      snackbarSeverity: 'success',
      snackbarMessage: ''
    }
  }

  componentDidMount (): void {
    this.props.ui.channel.page.get().then((page: PageProperties) => {
      this.updatePage(page);
    });
    this.props.ui.channel.page.on('navigate', pageProperties => this.reset(pageProperties));
  }

  reset (pageProperties: PageProperties) {
    this.setState({
      treeModel: undefined,
      page: undefined,
      channelId: undefined,
      path: undefined
    }, () => this.updatePage(pageProperties))
  }

  updatePage (page: PageProperties) {
    const channelId = page.channel.id;
    const path = getPageNameFromPagePath(page.path);
    this.pageOperationsApi.getChannelPage(channelId, path).then(response =>
      this.setState({
        treeModel: response.status === 200 ? convertPageToTreeModel(response.data) : undefined,
        channelId: channelId,
        path: path,
        saveDisabled: true
      }, () => this.state.treeModel ? this.logSnackBar('Flex Page updated') : this.logSnackBarError('No Flex Page Found'))
    ).catch(reason => {
      this.logSnackBarError(reason.response?.data?.errorMessage);
    });
    this.otherOperationsApi.getAllComponents(channelId).then(response => this.setState({components: response.data}));
  }

  onPageModelChange (page: Page): void {
    console.log('changed..', page);
    this.setState({saveDisabled: false, page: page});
  }

  render () {
    return <>
      <AppBar position="sticky" variant={'outlined'} color={'default'}>
        <Toolbar variant={'dense'}>
          <ACLConsumer>
            {permissions => permissions?.currentPageEditAllowed &&
              <Button
                disabled={this.state.saveDisabled}
                variant="outlined"
                color="primary"
                style={{marginRight: '10px'}}
                startIcon={<SaveOutlinedIcon/>}
                onClick={() => this.savePage()}
              >
                Save
              </Button>
            }
          </ACLConsumer>
        </Toolbar>
      </AppBar>
      {this.state.treeModel &&
      <PageEditor key={this.state.treeModel?.id} treeModel={this.state.treeModel} onPageModelChange={page => this.onPageModelChange(page)} components={this.state.components}/>
      }
      <PositionedSnackbar open={this.state.snackbarOpen} message={this.state.snackbarMessage}
                          severity={this.state.snackbarSeverity} onClose={() => this.setState({snackbarOpen: false})}/>
    </>
  }

  logSnackBar (message: string) {
    this.setState({snackbarOpen: true, snackbarMessage: message, snackbarSeverity: 'success'});
  }

  logSnackBarError (message: string) {
    this.setState({snackbarOpen: true, snackbarMessage: message, snackbarSeverity: 'error'});
  }

  savePage () {
    this.state.channelId && this.state.path && this.pageOperationsApi.putChannelPage(this.state.channelId, this.state.path, this.state.page).then(value => {
      this.logSnackBar('save successful');
      this.props.ui.channel.refresh().then(() => {
        console.info('channel refreshed');
      });
      this.props.ui.channel.page.refresh().then(() => {
        console.info('page refreshed');
      });

      this.props.ui.channel.page.get().then((pageProperties: PageProperties) => this.reset(pageProperties));
    }).catch(reason => this.logSnackBarError(reason.response?.data?.errorMessage));
  }
}

export default FlexPage