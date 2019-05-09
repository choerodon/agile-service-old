import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const ProjectSettingHome = asyncRouter(() => (import('./ProjectSettingHome')));

const ProjectSettingIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={ProjectSettingHome} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default ProjectSettingIndex;
