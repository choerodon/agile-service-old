import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const ReleaseHome = asyncRouter(() => (import('./ReleaseHome')));
const ReleaseDetail = asyncRouter(() => (import('./ReleaseDetail')));
const ReleaseLogs = asyncRouter(() => (import('./ReleaseLogs')));

const ReleaseIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={ReleaseHome} />
    <Route path={`${match.url}/detail/:id`} component={ReleaseDetail} />
    <Route path={`${match.url}/logs/:id`} component={ReleaseLogs} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default ReleaseIndex;
