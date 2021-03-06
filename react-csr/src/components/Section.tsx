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
import {BrComponent, BrProps} from '@bloomreach/react-sdk';

export function Section (props: BrProps) {


  return (
    <div className={`jumbotron mb-3 ${props.page.isPreview() ? 'has-edit-button' : ''}`}>
      {/*{props.component.getName()}*/}
      {/*{console.log('props')}*/}
      {/*<h2>{props.component.getParameters().rowtitle}</h2>*/}
      <div className={'container'}>
        <BrComponent>
        </BrComponent>
      </div>
    </div>
  );
}
