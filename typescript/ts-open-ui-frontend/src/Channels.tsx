import React from 'react';
import {
  Accordion,
  AccordionActions,
  AccordionDetails,
  AccordionSummary,
  Divider,
  IconButton,
  Typography
} from "@material-ui/core";
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import DeleteOutlinedIcon from "@material-ui/icons/DeleteOutlined";
import SaveOutlinedIcon from "@material-ui/icons/SaveOutlined";
import {Channel} from "./api/models";

type ChannelsState = {
  channels: Array<Channel>
}
type ChannelsProps = {}

class Channels extends React.Component<ChannelsProps, ChannelsState> {

  constructor (props: ChannelsProps) {
    super(props);

    this.state = {
      channels: [
        {
          "id": "brxsaas:vT9bR",
          "name": "BrX SaaS",
          "branch": "vT9bR",
          "branchOf": "brxsaas",
          "contentRootPath": "/content/documents/brxsaas",
          "locale": null,
          "devices": [],
          "defaultDevice": null,
          "responseHeaders": null,
          "linkurlPrefix": null,
          "cdnHost": null,
          "parameters": {
            "spaUrl": "https://brxm-react-spa.herokuapp.com/"
          }
        },
        {
          "id": "brxsaas",
          "name": "BrX SaaS",
          "branch": null,
          "branchOf": null,
          "contentRootPath": "/content/documents/brxsaas",
          "locale": null,
          "devices": [],
          "defaultDevice": null,
          "responseHeaders": null,
          "linkurlPrefix": null,
          "cdnHost": null,
          "parameters": {
            "spaUrl": "https://brxm-react-spa.herokuapp.com/"
          }
        }
      ]
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
      {this.state.channels.map((channel, index) => {
        return <Accordion key={index}>
          <AccordionSummary
            expandIcon={<ExpandMoreIcon/>}
            aria-controls="panel1c-content"
            id="panel1c-header"
          >
            <Typography>{channel.id}</Typography>
          </AccordionSummary>
          <AccordionDetails>
            <pre>{JSON.stringify(channel, undefined, 2)}</pre>
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

export default Channels;
