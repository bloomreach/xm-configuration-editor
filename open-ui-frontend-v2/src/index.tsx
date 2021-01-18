import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import reportWebVitals from './reportWebVitals';
import Fallback from "./other/Fallback";
import {HashRouter, Route, Switch} from "react-router-dom";
import UiExtension, {UiScope} from "@bloomreach/ui-extension";
import {ACLProvider} from "./ACLContext";
import FlexPage from "./flexPage/flexPage";
import {ChannelOtherOperationsApi} from "./api";

document.addEventListener('DOMContentLoaded', async () => {
  try {
    const ui: UiScope = await UiExtension.register();

    const channelOtherOperationsApi: ChannelOtherOperationsApi = new ChannelOtherOperationsApi({
      baseOptions: {withCredentials: true}
    }, `${ui.baseUrl}ws/config/v2`);

    const acl: { [x: string]: boolean } = await channelOtherOperationsApi.getAcl().then(value => value.data);

    ReactDOM.render(
      <React.Fragment>
        <ACLProvider value={acl}>
          <HashRouter>
            <Switch>
              <Route path="/flex-page" render={() => <FlexPage ui={ui}/>}/>
              {/*<Route path="/components" render={props => <Components ui={ui}/>}/>*/}
              {/*<Route exact path="/" render={props => <Navigation ui={ui} />}/>*/}
            </Switch>
          </HashRouter>
        </ACLProvider>
      </React.Fragment>,
      document.getElementById('root')
    );
  } catch (error) {
    console.error('Failed to register extension:', error.message);
    console.error('- error code:', error.code);
    ReactDOM.render(<Fallback/>, document.getElementById('root'));
  }
});

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
