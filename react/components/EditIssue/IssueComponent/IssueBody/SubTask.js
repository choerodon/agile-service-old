import React, { useState, useContext } from 'react';
import { observer } from 'mobx-react-lite';
import {
  Button, Icon, Progress, Input, Tooltip,
} from 'choerodon-ui';
import { stores } from '@choerodon/boot';
import { createSubIssue, createIssueField } from '../../../../api/NewIssueApi';
import CreateSubTask from '../../../CreateIssue/CreateSubTask';
import IssueList from '../../Component/IssueList';
import EditIssueContext from '../../stores';
import './SubTask.scss';
import Divider from './Divider';

const { AppState } = stores;

const SubTask = observer(({
  onDeleteSubIssue, reloadIssue, onUpdate,
}) => {
  const { store, disabled } = useContext(EditIssueContext);
  const [expand, setExpand] = useState(false);
  const [summary, setSummary] = useState(false);
  const {
    issueId: parentIssueId, subIssueVOList = [], priorityId, sprintId, 
  } = store.getIssue;
  const { getCreateSubTaskShow: createSubTaskShow } = store;

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
          reloadIssue(parentIssueId);
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
          subIssueVOList.map((subIssue, i) => renderIssueList(subIssue, i))
        }
    </div>
  );

  const handleCreateSubIssue = () => {
    store.setCreateSubTaskShow(false);
    if (onUpdate) {
      onUpdate();
    }
    if (reloadIssue) {
      reloadIssue();
    }
  };

  const getPercent = () => {    
    const completeList = subIssueVOList.filter(issue => issue.completed);
    const allLength = (subIssueVOList && subIssueVOList.length) || 0;
    const completeLength = completeList.length;
    if (allLength === 0) {
      return 100;
    } else {
      return parseInt(completeLength / allLength * 100, 10);
    }
  };

  const handleCancel = () => {
    setExpand(false);
    setSummary(false);    
  };

  const handleSave = () => {    
    const subIssueType = store.getIssueTypes && store.getIssueTypes.find(t => t.typeCode === 'sub_task');
    if (summary) {
      const issue = {
        summary,
        priorityId,
        priorityCode: `priority-${priorityId}`,
        projectId: AppState.currentMenuType.id,
        parentIssueId,
        sprintId,
        issueTypeId: subIssueType && subIssueType.id,
      };
      createSubIssue(issue)
        .then((res) => {
          const dto = {
            schemeCode: 'agile_issue',
            context: subIssueType && subIssueType.typeCode,
            pageCode: 'agile_issue_create',
          };
          createIssueField(res.issueId, dto);
          handleCancel();
          handleCreateSubIssue();
        })
        .catch(() => {
        });
    } else {
      Choerodon.prompt('子任务概要不能为空！');
    }
  };

  const onSummaryChange = (e) => {
    setSummary(e.target && e.target.value && e.target.value.trim());  
  };
  return (
    <div id="sub_task">
      <Divider />
      <div className="c7n-title-wrapper">
        <div className="c7n-title-left">         
          <span>子任务</span>
        </div>        
        {!disabled && (
          <div className="c7n-title-right" style={{ marginLeft: '14px' }}>
            <Tooltip title="创建子任务" getPopupContainer={triggerNode => triggerNode.parentNode}>
              <Button style={{ padding: '0 6px' }} className="leftBtn" funcType="flat" onClick={() => store.setCreateSubTaskShow(true)}>
                <Icon type="playlist_add icon" />
              </Button>
            </Tooltip>
          </div>
        )}
      </div>
      {subIssueVOList && subIssueVOList.length
        ? (
          <div className="c7n-subTask-progress">
            <Progress percent={getPercent()} />
          </div>
        ) : ''
        }
      {renderSubIssues()}
      {!disabled && (
        <div className="c7n-subTask-quickCreate">
          {expand
            ? (
              <div style={{ display: 'flex', alignItems: 'center' }}>
                <Input
                  autoFocus
                  className="hidden-label"
                  placeholder="在此输入子任务概要"
                  maxLength={44}
                  onPressEnter={handleSave}
                  onChange={onSummaryChange}
                />
                <Button
                  type="primary"
                  funcType="raised"
                  style={{ margin: '0 10px' }}        
                  onClick={handleSave}
                >
                    确定
                </Button>
                <Button
                  funcType="raised"
                  onClick={handleCancel}
                >
                    取消
                </Button>               
              </div>
            ) : (
              <Button
                className="leftBtn"
                functyp="flat"
                onClick={() => {
                  setExpand(true);
                }}
              >
                <Icon type="playlist_add" />
                {'创建问题'}
              </Button>
            )
          }
        </div>
      )}
      {
          createSubTaskShow ? (
            <CreateSubTask
              parentIssueId={parentIssueId}
              parentSummary={summary}
              visible={createSubTaskShow}
              onCancel={() => store.setCreateSubTaskShow(false)}
              onOk={handleCreateSubIssue}
              store={store}
            />
          ) : null
        }
    </div>
  );
});

export default SubTask;
