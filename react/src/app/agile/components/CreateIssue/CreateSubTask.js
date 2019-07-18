import React from 'react';
import CreateIssue from './CreateIssue';
import { createSubIssue } from '../../api/NewIssueApi';
import { getProjectName } from '../../common/utils';

const CreateSubTask = ({ ...props }) => (
  <CreateIssue
    mode="sub_task"
    request={createSubIssue}
    defaultTypeCode="sub_task"
    title="创建子任务"
    contentTitle={`在项目“${getProjectName()}”中创建子任务`}
    contentDescription="请在下面输入子任务的详细信息，创建问题的子任务。子任务会与父级问题的冲刺、史诗保持一致，并且子任务的状态会受父级问题的限制。"
    contentLink={null}
    {...props}
  />
);
export default CreateSubTask;
