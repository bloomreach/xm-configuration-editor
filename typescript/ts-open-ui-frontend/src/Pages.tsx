import React from 'react';
import {
  Accordion,
  AccordionActions,
  AccordionDetails,
  AccordionSummary,
  Divider,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
  Select,
  Typography
} from "@material-ui/core";
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import DeleteOutlinedIcon from "@material-ui/icons/DeleteOutlined";
import SaveOutlinedIcon from "@material-ui/icons/SaveOutlined";
import {Channel, Page} from "./api/models";
import {exampleChannels, examplePages} from "./samples/Example";

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
    return <>
      <FormControl fullWidth>
        <InputLabel id="demo-simple-select-label">Channel</InputLabel>
        <Select
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          // value={age}
          // onChange={handleChange}
        >
          {this.state.channels.map(channel => {
            return <MenuItem value={channel.id}>{channel.id}</MenuItem>
          })}
        </Select>
      </FormControl>
      {this.state.pages.map((page, index) => {
        return <Accordion key={index}>
          <AccordionSummary
            expandIcon={<ExpandMoreIcon/>}
            aria-controls="panel1c-content"
            id="panel1c-header"
          >
            <Typography>{page.name}</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <pre>{JSON.stringify(page, undefined, 2)}</pre>
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
