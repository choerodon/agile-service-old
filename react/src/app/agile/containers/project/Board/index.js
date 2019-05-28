import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const BoardHome = asyncRouter(() => (import('./BoardHome')));
const BoardIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={BoardHome} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default BoardIndex;
