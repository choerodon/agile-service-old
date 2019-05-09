import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const UserMapHome = asyncRouter(() => (import('./home')), () => (import('../../../stores/project/userMap/UserMapStore')));

const UserMapIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={UserMapHome} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default UserMapIndex;
