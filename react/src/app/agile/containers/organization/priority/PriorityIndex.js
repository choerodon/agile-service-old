import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const PriorityList = asyncRouter(() => import('./priorityList'), () => import('../../../stores/organization/priority'));

const PriorityIndex = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={PriorityList} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default PriorityIndex;
