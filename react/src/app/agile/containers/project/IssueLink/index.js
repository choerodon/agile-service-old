import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const IssueLinkHome = asyncRouter(() => (import('./IssueLinkHome')));

const IssueLinkIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={IssueLinkHome} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default IssueLinkIndex;
