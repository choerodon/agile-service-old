import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';
import { injectIntl } from 'react-intl';

const IssueTypeSchemeList = asyncRouter(() => import('./issueTypeSchemeList'), () => import('../../../stores/organization/issueTypeScheme'));
const RelateIssueTypeScheme = asyncRouter(() => import('./relateIssueTypeScheme'), () => import('../../../stores/organization/issueTypeScheme'));
const RelateMergeMatchFst = asyncRouter(() => import('./relateMergeMatchFst'), () => import('../../../stores/organization/issueTypeScheme'));
const RelateMergeMatchSed = asyncRouter(() => import('./relateMergeMatchSed'), () => import('../../../stores/organization/issueTypeScheme'));
const RelateMergeMatchTrd = asyncRouter(() => import('./relateMergeMatchTrd'), () => import('../../../stores/organization/issueTypeScheme'));
const RelateMergeUnMatch = asyncRouter(() => import('./relateMergeUnMatch'), () => import('../../../stores/organization/issueTypeScheme'));

const IssueTypeSchemeIndex = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={IssueTypeSchemeList} />
    <Route exact path={`${match.url}/ralation/:id`} component={RelateIssueTypeScheme} />
    <Route exact path={`${match.url}/relateMergeMatchFst`} component={RelateMergeMatchFst} />
    <Route exact path={`${match.url}/relateMergeMatchSed`} component={RelateMergeMatchSed} />
    <Route exact path={`${match.url}/relateMergeMatchTrd`} component={RelateMergeMatchTrd} />
    <Route exact path={`${match.url}/relateMergeUnMatch`} component={RelateMergeUnMatch} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default IssueTypeSchemeIndex;
