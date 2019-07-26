import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const PageHome = asyncRouter(() => (import('./page-home')));
const PageDetail = asyncRouter(() => (import('./page-detail')));

const PageIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={PageHome} />
    <Route path={`${match.url}/detail/:code`} component={PageDetail} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default PageIndex;
