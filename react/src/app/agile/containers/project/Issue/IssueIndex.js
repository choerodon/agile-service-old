/**
 * Created by Qyellow on 2018/4/10.
 */
import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const issueHome = asyncRouter(() => (import('./Issue')));

const IssueIndex = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={issueHome} />
    <Route path="*" component={nomatch} />
  </Switch>
);
export default IssueIndex;
