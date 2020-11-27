import React from 'react';
import {
  Accordion,
  AccordionActions,
  AccordionDetails,
  AccordionSummary,
  AppBar,
  Divider,
  FormControl,
  IconButton,
  MenuItem,
  Select,
  Toolbar,
  Typography
} from "@material-ui/core";
import 'react-sortable-tree/style.css';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import DeleteOutlinedIcon from "@material-ui/icons/DeleteOutlined";
import SaveOutlinedIcon from "@material-ui/icons/SaveOutlined";
import {Channel, Page} from "./api/models";
import {exampleChannels, examplePages} from "./samples/Example";
import AddOutlinedIcon from "@material-ui/icons/Add";
import SortableTree, {TreeItem, TreeNode} from 'react-sortable-tree';
import {componentToNode} from "./util";
import NodeRendererDefault from "./fork/NodeRendererDefault";

type PagesState = {
  channels: Array<Channel>
  pages: Array<Page>
}
type PagesProps = {}

class Pages extends React.Component<PagesProps, PagesState> {

  constructor (props: PagesProps) {
    super(props);

    this.state = {
      channels: exampleChannels,
      pages: examplePages
    }
  }

  componentDidMount (): void {
    // const api = new ChannelOperationsApi({
    //   baseOptions: {auth: {username: 'admin', password: 'admin'}, withCredentials: true,}
    // }, 'http://localhost:8080/management/site/v1')
    // api.getChannels().then(value => {
    //   this.setState({channels: value.data})
    // });
  }

  render () {

    const treeData = [
      {
        title: 'Title',
        subtitle: 'Subtitle',
        children: [
          { title: 'Child 1', subtitle: 'Subtitle', children: [] },
          { title: 'Child 2', subtitle: 'Subtitle' },
        ],
      },
    ];
    return <>
      <AppBar position="sticky" variant={'outlined'} color={'default'}>
        <Toolbar>
          <FormControl fullWidth>
          <Select
            displayEmpty
            inputProps={{'aria-label': 'Without label'}}
          >
           {this.state.channels.map(channel => {
             return <MenuItem value={channel.id}>Channel: {channel.id}</MenuItem>
           })}
        </Select>
      </FormControl>
           <IconButton
             edge="start"
             color="inherit"
             aria-label="Add"
             disabled={true}
             // onClick={event => this.handleOpen()}
           >
            <AddOutlinedIcon/>
          </IconButton>
        </Toolbar>
      </AppBar>

      {this.state.pages.map((page, index) => {
        // @ts-ignore
        return <Accordion key={index}>
          <AccordionSummary
            expandIcon={<ExpandMoreIcon/>}
            aria-controls="panel1c-content"
            id="panel1c-header"
          >
            <Typography>{page.name}</Typography>

          </AccordionSummary>
          <AccordionDetails>

            <SortableTree style={{minHeight: '70px', width: '100%'}} reactVirtualizedListProps={{autoHeight: true}}
                          isVirtualized={false}
              // @ts-ignore
                          treeData={[componentToNode(page)]}

              // getNodeKey={{node} => node.id}
              onChange={treeData => console.log(treeData)}
              // canNodeHaveChildren={node => (node.type !== 'container')}
              // onMoveNode={({treeData, node, nextParentNode, prevPath, prevTreeIndex, nextPath, nextTreeIndex}) =>
              //   this.onMove(treeData, node, nextParentNode, prevPath, prevTreeIndex, nextPath, nextTreeIndex)}
              // canDrag={({treeIndex}) => treeIndex !== 0}
              // canDrop={({nextParent}) => nextParent !== null}
              // @ts-ignore
                          nodeContentRenderer={NodeRendererDefault}
              // generateNodeProps={rowInfo => ({
              //   buttons: [rowInfo.node.type !== 'container' ? this.getStaticComponentMenu(rowInfo) : this.getManageableComponentMenu(rowInfo)],
              //   rowLabelClickEventHandler: (event) => this.handleNodeClick(rowInfo)
              // })}
            />
            {/*<pre>{JSON.stringify(page, undefined, 2)}</pre>*/}
          </AccordionDetails>
          <Divider/>
          <AccordionActions>
            <IconButton
              disabled={true}
              edge="start"
              color="inherit"
              aria-label="Delete"
              // onClick={event => this.deleteComponent(item)}
            >
              <DeleteOutlinedIcon/>
            </IconButton>
            <IconButton
              edge="start"
              color="inherit"
              aria-label="Save"
              // disabled={!item.changed}
              disabled={true}
              // onClick={event => this.handleSave(item)}
            >
              <SaveOutlinedIcon/>
            </IconButton>
          </AccordionActions>
        </Accordion>
      })}
    </>
  }

}

export default Pages;
