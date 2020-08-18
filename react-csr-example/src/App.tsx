/*
 * Copyright 2019 Hippo B.V. (http://www.onehippo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import axios from 'axios';
import {Link, RouteComponentProps} from 'react-router-dom';
import {BrComponent, BrPage, BrPageContext} from '@bloomreach/react-sdk';
import {Banner, Content, Menu, NewsList} from './components';
import {Static} from "./components/Static";
import {WhiteSection} from "./components/WhiteSection";
import {GreySection} from "./components/GreySection";
import {Row} from "./components/Row";
import {TYPE_CONTAINER_BOX, TYPE_CONTAINER_INLINE} from '@bloomreach/spa-sdk';
import Vbox from './util/vbox';
import VSpan from "./util/vspan";
import {Section} from "./components/Section";

export default function App (props: RouteComponentProps) {
  const configuration = {
    httpClient: axios,
    cmsBaseUrl: process.env.REACT_APP_CMS_BASE_URL!,
    spaBaseUrl: process.env.REACT_APP_SPA_BASE_URL,
    request: {
      path: `${props.location.pathname}${props.location.search}`,
    },
  };
  const mapping = {
    Banner,
    Content,
    'News List': NewsList,
    'row-x': Static,
    'section-x': Section,
    'section-1': Section,
    'section-2': Section,
    'section-3': Section,
    'row-1': Row,
    'row-2': Row,
    'row-3': Row,
    'row-4': Row,
    'row-5': Row,
    'row-6': Row,
    'section-white-1': WhiteSection,
    'section-white-2': WhiteSection,
    'section-white-3': WhiteSection,
    'section-grey-1': GreySection,
    'section-grey-2': GreySection,
    'section-grey-3': GreySection,
    [TYPE_CONTAINER_BOX]: Vbox,
    [TYPE_CONTAINER_INLINE]: VSpan,
  };

  return (
    <BrPage configuration={configuration} mapping={mapping}>
      <header>
        <nav className="navbar navbar-expand-sm navbar-dark sticky-top bg-dark" role="navigation">
          <div className="container">
            <BrPageContext.Consumer>
              {page => (
                <Link to={page!.getUrl('/')} className="navbar-brand">
                  {page!.getTitle() || 'brXM + React = ♥️'}
                </Link>
              )}
            </BrPageContext.Consumer>
            <div className="collapse navbar-collapse">
              <BrComponent path="menu">
                <Menu/>
              </BrComponent>
            </div>
          </div>
        </nav>
      </header>
      <section className="container flex-fill pt-3">
        <BrComponent path="top"/>
      </section>
      <section className="container flex-fill pt-3">
        <BrComponent path="main-1"/>
      </section>
      <footer className="bg-dark text-light py-3">
        <div className="container clearfix">
          <div className="float-left pr-3">&copy; Bloomreach</div>
          <div className="overflow-hidden">
            <BrComponent path="footer"/>
          </div>
        </div>
      </footer>
    </BrPage>
  );
}


