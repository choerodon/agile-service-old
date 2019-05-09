import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const RoadMap = asyncRouter(() => (import('./RoadMap')));
const RoadMapIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={RoadMap} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default RoadMapIndex;
