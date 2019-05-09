import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const BoardHome = asyncRouter(() => (import('./BoardHome')));
const BoardSetting = asyncRouter(() => (import('./BoardSetting')));

const BoardIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={BoardHome} />    
    <Route exact path={`${match.url}/setting`} component={BoardSetting} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default BoardIndex;
