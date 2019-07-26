import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const IssueTypeList = asyncRouter(() => import('./issueTypeList'), () => import('../../stores/organization/issueType'));

const IssueTypeIndex = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={IssueTypeList} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default IssueTypeIndex;
