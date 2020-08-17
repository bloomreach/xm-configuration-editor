import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import * as serviceWorker from './serviceWorker';
import Navigation from "./Navigation";
import {HashRouter, Route, Switch} from "react-router-dom";
import CurrentPage from "./current-page";
import Components from "./components";
import UiExtension from "@bloomreach/ui-extension";
import {getAcl} from "./config-api";
import {ACLProvider} from "./ACLContext";
import Fallback from "./Fallback";

document.addEventListener('DOMContentLoaded', async () => {
  try {
    let ui = await UiExtension.register();
    let acl = await getAcl(ui.baseUrl);
    ReactDOM.render(
      <React.Fragment>
        <ACLProvider value={acl}>
          <HashRouter>
            <Switch>
              <Route path="/current-page" render={props => <CurrentPage/>}/>
              <Route path="/components" render={props => <Components/>}/>
              <Route exact path="/" render={props => <Navigation/>}/>
            </Switch>
          </HashRouter>
        </ACLProvider>
      </React.Fragment>,
      document.getElementById('root')
    );
  } catch (error) {
    console.log(error);
    console.error('Failed to register extension:', error.message);
    console.error('- error code:', error.code);
    ReactDOM.render(<Fallback/>, document.getElementById('root'));
  }
});

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
