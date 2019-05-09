import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const PIAims = asyncRouter(() => (import('./PIAims')));
const FeatureIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={PIAims} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default FeatureIndex;
