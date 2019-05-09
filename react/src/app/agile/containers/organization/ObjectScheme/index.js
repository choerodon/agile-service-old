import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const ObjectSchemeHome = asyncRouter(() => (import('./ObjectSchemeHome')), () => import('../../../stores/organization/ObjectScheme/ObjectSchemeStore'));
const ObjectSchemeDetail = asyncRouter(() => (import('./ObjectSchemeDetail')), () => import('../../../stores/organization/ObjectScheme/ObjectSchemeStore'));
const ObjectSchemeField = asyncRouter(() => (import('./ObjectSchemeField')), () => import('../../../stores/organization/ObjectScheme/ObjectSchemeStore'));


const ObjectSchemeIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={ObjectSchemeHome} />
    <Route path={`${match.url}/detail/:code`} component={ObjectSchemeDetail} />
    <Route path={`${match.url}/field/:id`} component={ObjectSchemeField} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default ObjectSchemeIndex;
