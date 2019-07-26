import React from 'react';
import { Switch } from 'react-router-dom';
import { asyncRouter } from '@choerodon/boot';
import TabRoute from '../../components/TabRoute';

const Backlog = asyncRouter(() => import('../Backlog/BacklogHome'), () => import('../../stores/project/backlog/BacklogStore'), undefined, undefined);
const Issue = asyncRouter(() => (import('../Issue/Issue')));

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
  </Switch>
);
export default WorkList;
