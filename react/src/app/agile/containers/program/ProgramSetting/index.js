import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const ProgramSetting = asyncRouter(() => (import('./ProgramSetting')));
const ArtIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={ProgramSetting} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default ArtIndex;
