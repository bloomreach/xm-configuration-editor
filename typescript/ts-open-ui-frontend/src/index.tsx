import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import reportWebVitals from './reportWebVitals';
import {HashRouter, Route, Switch} from "react-router-dom";
import Navigation from "./Navigation";
import Fallback from "./other/Fallback";

ReactDOM.render(
  <React.StrictMode>
    <React.Fragment>
      <HashRouter>
        <Switch>
          <Route path="/navigation" render={() => <Navigation/>}/>
          <Route exact path="/" render={() => <Fallback/>}/>
        </Switch>
      </HashRouter>
    </React.Fragment>,
  </React.StrictMode>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
