import React from 'react';
import { Route, Switch, withRouter } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const ComponentHome = asyncRouter(() => import('./ComponentHome'));

const ComponentIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={ComponentHome} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default withRouter(ComponentIndex);
