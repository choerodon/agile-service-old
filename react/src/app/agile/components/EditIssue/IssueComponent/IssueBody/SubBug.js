import React, { useContext } from 'react';
import { Button, Icon, Tooltip } from 'choerodon-ui';
import CreateSubBug from '../../../CreateIssue/CreateSubBug';
import IssueList from '../../Component/IssueList';
import EditIssueContext from '../../stores';

const SubBug = ({
  reloadIssue, onDeleteSubIssue, onUpdate,
}) => {
  const { store, disabled } = useContext(EditIssueContext);
  const { issueId, summary, subBugVOList = [] } = store.getIssue;
  const { getCreateSubBugShow: createSubBugShow } = store;
  const renderIssueList = (issue, i) => (
    <IssueList
      showAssignee
      key={issue.issueId}
      issue={{
        ...issue,
        typeCode: issue.typeCode || 'sub_task',
      }}
      i={i}
      onOpen={() => {
        if (reloadIssue) {
          reloadIssue(issue.issueId);
        }
      }}
      onRefresh={() => {
        if (reloadIssue) {
          reloadIssue(issueId);
        }
        if (onDeleteSubIssue) {
          onDeleteSubIssue();
        }
      }}
    />
  );

  const renderSubIssues = () => (
    <div className="c7n-tasks">
      {
          subBugVOList.map((subIssue, i) => renderIssueList(subIssue, i))
        }
    </div>
  );

  const handleCreateSubIssue = () => {   
    store.setCreateSubBugShow(false);
    if (onUpdate) {
      onUpdate();
    }
    if (reloadIssue) {
      reloadIssue();
    }
  };

 
  return (
    <div id="bug">
      <div className="c7n-title-wrapper">
        <div className="c7n-title-left">
          <Icon type="bug_report c7n-icon-title" />
          <span>缺陷</span>
        </div>
        <div style={{
          flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px',
        }}
        />
        {!disabled && (
        <div className="c7n-title-right" style={{ marginLeft: '14px' }}>
          <Tooltip title="创建缺陷" getPopupContainer={triggerNode => triggerNode.parentNode}>
            <Button style={{ padding: '0 6px' }} className="leftBtn" funcType="flat" onClick={() => store.setCreateSubBugShow(true)}>
              <Icon type="playlist_add icon" />
            </Button>
          </Tooltip>
        </div>
        )}
      </div>
      {renderSubIssues()}
      {
          createSubBugShow ? (
            <CreateSubBug
              relateIssueId={issueId}
              parentSummary={summary}
              visible={createSubBugShow}
              onCancel={() => store.setCreateSubBugShow(false)}
              onOk={handleCreateSubIssue}
              store={store}
            />
          ) : null
        }
    </div>
  );
};

export default SubBug;
