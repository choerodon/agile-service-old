import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Button, Icon, Progress, Input, Tooltip,
} from 'choerodon-ui';
import { stores } from '@choerodon/boot';
import { injectIntl } from 'react-intl';
import { createSubIssue, createIssueField } from '../../../../api/NewIssueApi';
import CreateSubTask from '../../../CreateIssue/CreateSubTask';
import IssueList from '../../Component/IssueList';
import './SubTask.scss';

const { AppState } = stores;

@observer class SubTask extends Component {
  constructor(props) {
    super(props);
    this.sign = false;
    this.state = {
      expand: false,
      summary: false,
    };
  }

  componentDidMount() {
  }

  /**
   * IssueList
   * @param {*} issue
   * @param {*} i
   */
  renderIssueList = (issue, i) => {
    const { reloadIssue, store, onDeleteSubIssue } = this.props;
    const { issueId: id } = store.getIssue;
    return (
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
            reloadIssue(id);
          }
          if (onDeleteSubIssue) {
            onDeleteSubIssue();
          }
        }}
      />
    );
  };

  renderSubIssues = () => {
    const { store } = this.props;
    const { subIssueVOList = [] } = store.getIssue;
    return (
      <div className="c7n-tasks">
        {
          subIssueVOList.map((subIssue, i) => this.renderIssueList(subIssue, i))
        }
      </div>
    );
  };

  handleCreateSubIssue = () => {
    const { onUpdate, reloadIssue, store } = this.props;
    store.setCreateSubTaskShow(false);
    if (onUpdate) {
      onUpdate();
    }
    if (reloadIssue) {
      reloadIssue();
    }
  };

  getPercent = () => {
    const { store } = this.props;
    const { subIssueVOList = [] } = store.getIssue;
    const completeList = subIssueVOList.filter(issue => issue.completed);
    const allLength = (subIssueVOList && subIssueVOList.length) || 0;
    const completeLength = completeList.length;
    if (allLength === 0) {
      return 100;
    } else {
      return parseInt(completeLength / allLength * 100, 10);
    }
  };

  handleCancel = () => {
    this.setState({
      expand: false,
      summary: false,
    });
  };

  handleSave = () => {
    const { store } = this.props;
    const { summary } = this.state;
    const { issueId, priorityId, sprintId } = store.getIssue;
    const subIssueType = store.getIssueTypes && store.getIssueTypes.find(t => t.typeCode === 'sub_task');
    if (summary) {
      const issue = {
        summary,
        priorityId,
        priorityCode: `priority-${priorityId}`,
        projectId: AppState.currentMenuType.id,
        parentIssueId: issueId,
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
          this.handleCancel();
          this.handleCreateSubIssue();
        })
        .catch(() => {
        });
    } else {
      Choerodon.prompt('子任务概要不能为空！');
    }
  };

  onSummaryChange = (e) => {
    this.setState({
      summary: e.target && e.target.value && e.target.value.trim(),
    });
  };

  render() {
    const { expand } = this.state;
    const { store, disabled } = this.props;
    const { issueId, summary } = store.getIssue;
    const { getCreateSubTaskShow: createSubTaskShow } = store;
    const { subIssueVOList = [] } = store.getIssue;
    return (
      <div id="sub_task">
        <div className="c7n-title-wrapper">
          <div className="c7n-title-left">
            <Icon type="filter_none c7n-icon-title" />
            <span>子任务</span>
          </div>
          <div style={{
            flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px',
          }}
          />
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
              <Progress percent={this.getPercent()} />
            </div>
          ) : ''
        }
        {this.renderSubIssues()}
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
                  onPressEnter={this.handleSave}
                  onChange={this.onSummaryChange}
                />
                <Button
                  type="primary"
                  funcType="raised"
                  style={{ margin: '0 10px' }}
                  // loading={loading}
                  onClick={this.handleSave}
                >
                    确定
                </Button>
                <Button
                  funcType="raised"
                  onClick={this.handleCancel}
                >
                    取消
                </Button>               
              </div>
            ) : (
              <Button
                className="leftBtn"
                functyp="flat"
                onClick={() => {
                  this.setState({
                    expand: true,
                  });
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
              parentIssueId={issueId}
              parentSummary={summary}
              visible={createSubTaskShow}
              onCancel={() => store.setCreateSubTaskShow(false)}
              onOk={this.handleCreateSubIssue}
              store={store}
            />
          ) : null
        }
      </div>
    );
  }
}

export default withRouter(injectIntl(SubTask));
