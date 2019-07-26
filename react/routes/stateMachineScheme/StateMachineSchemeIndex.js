import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const StateMachineSchemeList = asyncRouter(
  () => import('./stateMachineSchemeList'),
  () => import('../../../stores/organization/stateMachineScheme')
);
const EditStateMachineScheme = asyncRouter(
  () => import('./editStateMachineScheme'),
  () => import('../../../stores/organization/stateMachineScheme')
);
const StateMachineSchemeIndex = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={StateMachineSchemeList} />
    <Route
      exact
      path={`${match.url}/edit/:id`}
      component={EditStateMachineScheme}
    />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default StateMachineSchemeIndex;
