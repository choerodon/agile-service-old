import React from 'react';
import CreateIssue from './CreateIssue';

const CreateFeature = ({ ...props }) => (
  <CreateIssue
    mode="feature" 
    defaultTypeCode="feature"
    title="创建特性"
    contentTitle="在项目群中创建特性"
    contentDescription="请在下面输入问题的详细信息，包含详细描述、特性价值、验收标准等等。您可以通过丰富的问题描述帮助相关人员更快更全面的理解任务，同时更好的把控问题进度。"
    contentLink={null}  
    hiddenFields={['assignee', 'sprint', 'priority', 'label', 'fixVersion', 'component', 'epicName', 'remainingTime']}  
    {...props}
  />
);
export default CreateFeature;
