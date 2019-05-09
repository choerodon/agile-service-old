import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const BacklogHome = asyncRouter(() => (import('./BacklogHome')), () => import('../../../stores/project/backlog/BacklogStore'));

const BacklogIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={BacklogHome} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default BacklogIndex;
