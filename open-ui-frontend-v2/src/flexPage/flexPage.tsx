import React from "react";
import {PageProperties, UiScope} from "@bloomreach/ui-extension";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import {Button, Container} from "@material-ui/core";
import SaveOutlinedIcon from "@material-ui/icons/SaveOutlined";
import {ChannelFlexPageOperationsApi} from "../api";
import {Page} from "../api/models";
import {convertPageToTreeModel} from "./page-util";
import PageEditor from "./PageEditor";

type FlexPageState = {
  channelId?: string
  path?: string
  page?: Page
}
type FlexPageProps = {
  ui: UiScope
}

class FlexPage extends React.Component<FlexPageProps, FlexPageState> {
  private api: ChannelFlexPageOperationsApi;

  constructor (props: FlexPageProps) {
    super(props);

    this.api = new ChannelFlexPageOperationsApi({
      baseOptions: {withCredentials: true}
    }, `${this.props.ui.baseUrl}ws/config/v2`);

    this.state = {}
  }

  componentDidMount (): void {
    // @ts-ignore
    this.props.ui.channel.on('navigate', this.updatePage);

    this.props.ui.channel.page.get().then((page: PageProperties) => {
      this.updatePage(page);
    });
  }

  updatePage (page: PageProperties) {
    this.setState({channelId: page.channel.id, path: page.path}, () => {
      // @ts-ignore
      this.api.getChannelPage(this.state.channelId, this.state.path).then(response => this.setState({page: response.data}))
    });
  }

  onPageModelChange (page: Page): void {
    console.log('changed..', page);
  }

  render () {
    return <>
      <AppBar position="sticky" variant={'outlined'} color={'default'}>
        <Toolbar>
            <Button
              disabled={true}
              variant="outlined"
              color="primary"
              style={{marginRight: '10px'}}
              startIcon={<SaveOutlinedIcon/>}
              // onClick={() => this.saveSiteMap()}
            >
         Save
          </Button>
        </Toolbar>
      </AppBar>
      <Container>
        {this.state.page !== undefined &&
        <PageEditor treeModel={convertPageToTreeModel(this.state.page)} onPageModelChange={page => this.onPageModelChange(page)}/>
        }
      </Container>

    </>
  }

}

export default FlexPage