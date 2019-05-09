import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const FeatureList = asyncRouter(() => (import('./FeatureList')));
const FeatureIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={FeatureList} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default FeatureIndex;
