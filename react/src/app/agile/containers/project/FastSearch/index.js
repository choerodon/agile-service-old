import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const FastSearchHome = asyncRouter(() => (import('./FastSearchHome')));

const FastSearchIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={FastSearchHome} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default FastSearchIndex;
