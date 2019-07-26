import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const StateList = asyncRouter(() => import('./stateList'), () => import('../../../stores/organization/state'));

const StateIndex = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={StateList} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default StateIndex;
