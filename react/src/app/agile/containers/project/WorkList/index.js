import React, { Fragment } from 'react';
import { Route, Link, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';
import TabRoute from '../../../components/TabRoute';

const Backlog = asyncRouter(() => import('../Backlog'), undefined, undefined);
const Issue = asyncRouter(() => (import('../Issue')));


const WorkList = ({ match }) => (
  <Switch>
    <TabRoute routes={[{
      title: '待办事项',
      path: `${match.url}/backlog`,
      component: Backlog,
    }, {
      title: '问题管理',
      path: `${match.url}/issue`,
      component: Issue,
    }]}
    />
    {/* <Route exact path={match.url} component={issueHome} /> */}
    <Route path="*" component={nomatch} />
  </Switch>
);
export default WorkList;
