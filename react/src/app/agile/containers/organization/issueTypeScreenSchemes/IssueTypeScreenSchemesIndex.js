import { asyncRouter, nomatch } from '@choerodon/boot';
import React from 'react';
import { Route, Switch } from 'react-router-dom';
import IssueTypeScreenSchemesEditHOC from './IssueTypeScreenSchemesEditHOC';

const IssueTypeScreenSchemesList = asyncRouter(() => import('./issueTypeScreenSchemesList'), () => import('../../../stores/organization/issueTypeScreenSchemes'));
const IssueTypeScreenSchemesEditTemplate = asyncRouter(() => import('./issueTypeScreenSchemesEditTemplate'), () => import('../../../stores/organization/issueTypeScreenSchemes'));

const IssueTypeScreenSchemesEdit = IssueTypeScreenSchemesEditHOC(IssueTypeScreenSchemesEditTemplate, 'edit');
const IssueTypeScreenSchemesCreate = IssueTypeScreenSchemesEditHOC(IssueTypeScreenSchemesEditTemplate, 'create');

const IssueTypeScreenSchemesIndex = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={IssueTypeScreenSchemesList} />
    <Route exact path={`${match.url}/create`} component={IssueTypeScreenSchemesCreate} />
    <Route exact path={`${match.url}/edit/:id`} component={IssueTypeScreenSchemesEdit} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default IssueTypeScreenSchemesIndex;
