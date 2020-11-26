import React, {Fragment} from 'react';
import Tabs from "@material-ui/core/Tabs";
import AppBar from "@material-ui/core/AppBar";
import Tab from "@material-ui/core/Tab";
import TabPanel from "./TabPanel";
import Channels from "./Channels";
import Pages from "./Pages";

type NavigationState = {
  tab: number
}
type NavigationProps = {}

class Navigation extends React.Component<NavigationProps, NavigationState> {

  constructor (props: NavigationProps) {
    super(props);
    this.state = {
      tab: 0,
    }
  }

  onTabChange (nextTab: number) {
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
          <Tab label="Channels"/>
          <Tab label="Pages"/>
        </Tabs>
      </AppBar>
      <TabPanel value={tab} index={0}>
        <Channels/>
      </TabPanel>
      <TabPanel value={tab} index={1}>
        <Pages/>
      </TabPanel>
    </Fragment>
  }

}

export default Navigation;
