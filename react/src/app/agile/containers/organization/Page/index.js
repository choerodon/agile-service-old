import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const PageHome = asyncRouter(() => (import('./PageHome')), () => import('../../../stores/organization/Page/PageStore'));
const PageDetail = asyncRouter(() => (import('./PageDetail')), () => import('../../../stores/organization/Page/PageStore'));

const PageIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={PageHome} />
    <Route path={`${match.url}/detail/:code`} component={PageDetail} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default PageIndex;
