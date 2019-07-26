import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const ObjectSchemeHome = asyncRouter(() => (import('./object-scheme-home')));
const ObjectSchemeDetail = asyncRouter(() => (import('./object-scheme-detail')));
const ObjectSchemeField = asyncRouter(() => (import('./object-scheme-field')));


const ObjectSchemeIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={ObjectSchemeHome} />
    <Route path={`${match.url}/detail/:code`} component={ObjectSchemeDetail} />
    <Route path={`${match.url}/field/:id`} component={ObjectSchemeField} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default ObjectSchemeIndex;
