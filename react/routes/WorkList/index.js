import React from 'react';
import { asyncRouter } from '@choerodon/boot';
import TabRoute from '../../components/TabRoute';

const Backlog = asyncRouter(() => import('../Backlog'));
const Issue = asyncRouter(() => (import('../Issue')));

const WorkList = ({ match }) => (
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
);
export default WorkList;
