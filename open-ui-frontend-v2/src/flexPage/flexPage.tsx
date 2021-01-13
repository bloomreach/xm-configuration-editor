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

type FlexPageState = {
  channelId?: string
  path?: string
  page?: Page
  components?: Array<AbstractComponent>
  treeModel?: TreeModel
  saveDisabled: boolean
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
      baseOptions: {withCredentials: true}
    }, basePath);

    this.state = {
      saveDisabled: true
    }
  }

  componentDidMount (): void {
    this.props.ui.channel.page.get().then((page: PageProperties) => {
      this.updatePage(page);
    });
    this.props.ui.channel.page.on('navigate', pageProperties => this.updatePage(pageProperties));
  }

  updatePage (page: PageProperties) {
    console.log('update page...', page);
    const channelId = page.channel.id;
    const path = getPageNameFromPagePath(page.path);
    this.pageOperationsApi.getChannelPage(channelId, path).then(response =>
      this.setState({
        treeModel: response.status === 200 ? convertPageToTreeModel(response.data) : undefined,
        channelId: channelId,
        path: path,
        saveDisabled: true
      })
    ).catch(reason => {
      console.log('something went wrong...', reason);
    });
    this.otherOperationsApi.getAllComponents(channelId).then(response => this.setState({components: response.data}));
  }

  onPageModelChange (page: Page): void {
    console.log('changed..', page);
    this.setState({saveDisabled: false, page: page});
  }

  render () {
    return <>
      <AppBar position="sticky" variant={'outlined'} color={'default'}  >
        <Toolbar variant={'dense'}>
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
        </Toolbar>
      </AppBar>
      {this.state.treeModel &&
      <PageEditor key={this.state.treeModel?.id} treeModel={this.state.treeModel} onPageModelChange={page => this.onPageModelChange(page)} components={this.state.components}/>
      }
    </>
  }

  private savePage () {
    console.log('save.. on success', this.state.page);
    this.setState({saveDisabled: true});
  }
}

export default FlexPage