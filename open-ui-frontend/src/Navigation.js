import React, {Fragment} from 'react';
import Tabs from "@material-ui/core/Tabs";
import AppBar from "@material-ui/core/AppBar";
import Tab from "@material-ui/core/Tab";
import TabPanel from "./TabPanel";
import Components from "./components";
import CurrentPage from "./current-page";
import Badge from "@material-ui/core/Badge";

class Navigation extends React.Component {

  constructor (props) {
    super(props);
    this.ui = props.ui;
    this.state = {
      tab: 0,
    }
  }

  onTabChange (nextTab) {
    this.setState({tab: nextTab});
  }

  render () {
    const {tab} = this.state || 0;

    return <Fragment>
      <AppBar position="static" color={'default'}>
        <Tabs
          variant="scrollable"
          scrollButtons="auto"
          value={tab} onChange={(event, nextTab) => this.onTabChange(nextTab)}>
          <Tab label="Current Page"/>
          <Tab label="Components" icon={
            <Badge
              style={{
                right: '30px',
                position: 'absolute',
                top: '15px'
              }}
              badgeContent={'U/C'} color="primary">
            </Badge>
          }>
          </Tab>
          <Tab disabled={true} label="Pages"/>
          <Tab disabled={true} label="Abstract Pages"/>
          <Tab disabled={true} label="Prototypes"/>
          <Tab disabled={true} label="Catalog"/>
          <Tab disabled={true} label="Sitemap"/>
          <Tab disabled={true} label="Site Menus"/>
        </Tabs>
      </AppBar>
      <TabPanel value={tab} index={0}>
        <CurrentPage/>
      </TabPanel>
      <TabPanel value={tab} index={1}>
        <Components/>
      </TabPanel>
    </Fragment>
  }

}

export default Navigation;
